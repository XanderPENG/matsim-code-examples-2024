package be.kuleuven.xanderpeng.emissions.networkV2.core;

import be.kuleuven.xanderpeng.emissions.networkV2.config.NetworkConverterConfigGroup;
import be.kuleuven.xanderpeng.emissions.networkV2.readers.GeoJsonReader;
import be.kuleuven.xanderpeng.emissions.networkV2.readers.OsmReader;
import be.kuleuven.xanderpeng.emissions.networkV2.readers.Reader;
import be.kuleuven.xanderpeng.emissions.networkV2.readers.ShpReader;
import be.kuleuven.xanderpeng.emissions.networkV2.tools.Utils;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.network.algorithms.MultimodalNetworkCleaner;
import org.matsim.core.network.algorithms.NetworkTransform;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class NetworkConverter {

    Logger LOG = LogManager.getLogger(NetworkConverter.class);
    private final Reader reader;
    private final NetworkConverterConfigGroup config;

    private final Map<String, NetworkElement.Node> interimNodes = new HashMap<>();
    private final Map<String, NetworkElement.Link> interimLinks = new HashMap<>();
    private final Set<TransMode> configuredTransModes = new HashSet<>();


    public NetworkConverter(NetworkConverterConfigGroup config) {

        this.config = config;
        // Create the reader based on the file type
        switch (this.config.FILE_TYPE) {
            case "osm":
                reader = new OsmReader();
                break;
            case "shp":
                reader = new ShpReader();
                break;
            case "geojson":
                reader = new GeoJsonReader();
                break;
            default:
                throw new IllegalArgumentException("Unsupported file type: " + this.config.FILE_TYPE);
        }
        // Initialize the configuredTransModes
        config.getModeParamSets().forEach((mode, modeParamSet) -> {
            configuredTransModes.add(modeParamSet.getTransMode());
        });

    }

    public Network convert() throws IOException {
        LOG.info("Start converting the input network file to MATSim network...");
        // Create matsim network and factory instance
        Network network = NetworkUtils.createNetwork();
        NetworkFactory factory = network.getFactory();
        // Read the input network file
        LOG.info("Reading the input network file: {}", config.INPUT_NETWORK_FILE);
        reader.read(config.INPUT_NETWORK_FILE);

        // Process the link by a for-loop
        Map<String, Integer> nodeRefCount = countNodeRef();

        reader.getRawLinks().forEach((linkId, link) -> {
            // match the TransMode of the link
            matchLinkMode(link);
            // Process the oneway attribute of the link
            NetworkElement.Link reversedLink = processOneway(link);
            // Split the link and store the interim nodes and links
            if (config.KEEP_DETAILED_LINK){
                // Split link at each composed node
                splitLinkAtComposedNodes(link);
                if (reversedLink != null){
                    splitLinkAtComposedNodes(reversedLink);
                }
            } else {
                // Only split the link at the intersections
                splitLinkAtIntersections(link, nodeRefCount);
                if (reversedLink != null){
                    splitLinkAtIntersections(reversedLink, nodeRefCount);
                }
            }
        });

        // Add the interim nodes and links to the MATSim network
        interimLinks.forEach((linkId, link) -> {;
            Node fromNode = NetworkUtils.createNode(Id.createNodeId(link.getFromNode().getId()), link.getFromNode().getCoord());
            Node toNode = NetworkUtils.createNode(Id.createNodeId(link.getToNode().getId()), link.getToNode().getCoord());
            network.addNode(fromNode);
            network.addNode(toNode);
            // Create the link. Configure the attr, since the default value is irrational.
            Map<String, Double> linkAttrs = matchAndGetLinkAttr(link);
            NetworkUtils.createAndAddLink(network, Id.createLinkId(linkId), fromNode, toNode,
                    linkAttrs.get("LENGTH_FIELD"), linkAttrs.get("MAX_SPEED_FIELD"),
                    linkAttrs.get("LANE_CAPACITY_FIELD"), linkAttrs.get("LANE_WIDTH_FIELD"));
            // TODO: Add user specified reserved_attributes to the link
        });

        // Process the connected network
        processConnectedNetwork(network);

        // Transform the network into the specified CRS
        if (this.config.OUTPUT_CRS != null && !this.config.OUTPUT_CRS.isEmpty()) {
            LOG.info("Transforming the network into the specified CRS: {}", this.config.OUTPUT_CRS);
            CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation(
                    this.config.INPUT_CRS, this.config.OUTPUT_CRS);
            new NetworkTransform(transformation).run(network);
        }
        return network;
    }

    private void matchLinkMode(NetworkElement.Link link) {
        boolean isReservedLink = false;
        // Match the transMode of the link based on the key-value pairs and the modeParamSets
        Set<TransMode.Mode> matchedModes = new HashSet<>();
        // Match the transMode based on the key-value pairs
        configuredTransModes.forEach(transMode -> {
            if (transMode.matchTransMode(link.getKeyValuePairs())) {
                matchedModes.add(transMode.getMode());
            }
        });
        // Set the allowed modes for the link
        link.addAllowedModes(matchedModes);
    }

    // Process the oneway attribute of the link
    private NetworkElement.Link processOneway(NetworkElement.Link link) {
        String onewayKey = (String) config.ONEWAY_KEY_VALUE_PAIR.keySet().toArray()[0];
        String onewayValue = config.ONEWAY_KEY_VALUE_PAIR.get(onewayKey);
        NetworkElement.Link reversedLink;
        // check if the link is one-way based on the key-value pairs
        if (link.getKeyValuePairs().containsKey(onewayKey) && link.getKeyValuePairs().get(onewayKey).equals(onewayValue)) {
            reversedLink = new NetworkElement.Link(link.getId()+"_r", link.getToNode(), link.getFromNode());
            reversedLink.setKeyValuePairs(link.getKeyValuePairs());
            reversedLink.addAllowedModes(link.getAllowedModes());
            reversedLink.addComposedNodes(Utils.reverseLinkedHashMap(link.getComposedNodes()));
            return reversedLink;
        } else {
            // if the oneway key is not found in the key-value pairs, or the value is not equal to the specified value
            return null;
        }
    }


    // Count the node occurrence in the links
    private Map<String, Integer> countNodeRef() {
        // Create a map to store the reference count of each node
        Map<String, Integer> nodeRefCount = new HashMap<>();
        for (NetworkElement.Link link : reader.getRawLinks().values()) {
            link.getComposedNodes().forEach((nodeId, node) -> {
//                String nodeStringId = node.toString();
                nodeRefCount.put(nodeId, nodeRefCount.getOrDefault(nodeId, 0) + 1);
            });
        }
        return nodeRefCount;
    }

    // Split the link at the intersection(s)
    private void splitLinkAtIntersections(NetworkElement.Link link, Map<String, Integer> nodeRefCount) {
        final NetworkElement.Node[] fromNode = {link.getFromNode()};
        final NetworkElement.Node endNode = link.getToNode();
        // Set an index to count the number of new links
        AtomicInteger idx = new AtomicInteger(0);
        if (!link.getComposedNodes().isEmpty()) {
            link.getComposedNodes().forEach((nodeId, node) -> {
                // If the node is connected to more than one link, split the link
                if (nodeRefCount.get(nodeId) > 1) {
                    // Create a new link
                    NetworkElement.Link newLink = new NetworkElement.Link(link.getId()+"_"+ idx, fromNode[0], node);
                    newLink.setKeyValuePairs(link.getKeyValuePairs());
                    newLink.addAllowedModes(link.getAllowedModes());
                    interimLinks.put(newLink.getId(), newLink);
                    // Update the related link info
                    fromNode[0].addRelatedLink(newLink);
                    node.addRelatedLink(newLink);
                    interimNodes.put(fromNode[0].getId(), fromNode[0]);
                    interimNodes.put(node.getId(), node);
                    idx.getAndIncrement();
                    fromNode[0] = node;
                }
            });
        }
        // Create the last/only link
        NetworkElement.Link lastLink = new NetworkElement.Link(link.getId()+"_"+ idx, fromNode[0], endNode);
        fromNode[0].addRelatedLink(lastLink);
        endNode.addRelatedLink(lastLink);
        lastLink.setKeyValuePairs(link.getKeyValuePairs());
        lastLink.addAllowedModes(link.getAllowedModes());

        interimNodes.put(fromNode[0].getId(), fromNode[0]);
        interimNodes.put(endNode.getId(), endNode);
        interimLinks.put(lastLink.getId(), lastLink);
    }

    // Split the link at all the composed nodes
    private void splitLinkAtComposedNodes(NetworkElement.Link link) {
        final NetworkElement.Node[] fromNode = {link.getFromNode()};
        final NetworkElement.Node endNode = link.getToNode();
        // Set an index to count the number of new links
        AtomicInteger idx = new AtomicInteger(0);
        if (!link.getComposedNodes().isEmpty()) {
            link.getComposedNodes().forEach((nodeId, node) -> {
                // Create a new link
                NetworkElement.Link newLink = new NetworkElement.Link(link.getId()+"_"+idx, fromNode[0], node);
                newLink.setKeyValuePairs(link.getKeyValuePairs());
                newLink.addAllowedModes(link.getAllowedModes());
                // Update the related link info
                fromNode[0].addRelatedLink(newLink);
                node.addRelatedLink(newLink);
                interimNodes.put(fromNode[0].getId(), fromNode[0]);
                interimNodes.put(node.getId(), node);
                interimLinks.put(newLink.getId(), newLink);
                // Update the index and fromNode
                idx.getAndIncrement();
                fromNode[0] = node;
            });
        }
        // Create the last/only link
        NetworkElement.Link lastLink = new NetworkElement.Link(link.getId()+"_"+ idx, fromNode[0], endNode);
        fromNode[0].addRelatedLink(lastLink);
        endNode.addRelatedLink(lastLink);
        lastLink.setKeyValuePairs(link.getKeyValuePairs());
        lastLink.addAllowedModes(link.getAllowedModes());

        interimNodes.put(fromNode[0].getId(), fromNode[0]);
        interimNodes.put(endNode.getId(), endNode);
        interimLinks.put(lastLink.getId(), lastLink);
    }
    /*
    Match and get the key link-related attributes based on the key-value pairs, LinkAttrParamSet, and the allowedTransMode
     */
    private Map<String, Double> matchAndGetLinkAttr(NetworkElement.Link link){
        Map<String, Double> linkAttr = new HashMap<>();
        // Get the max default capacity, freespeed, width, etc. based on the allowedTransModes
        Map<String, Double> maxDefaultAttr = new HashMap<>();
        link.getAllowedModes().forEach(mode -> {
            this.configuredTransModes.forEach(transMode -> {
                if (transMode.getMode().equals(mode)){
                    Double maxFreeSpeed_ = maxDefaultAttr.putIfAbsent("MAX_SPEED_FIELD", transMode.getDefaultMaxSpeed());
                    if (maxFreeSpeed_ != null){
                        maxDefaultAttr.put("MAX_SPEED_FIELD", Math.max(maxFreeSpeed_, transMode.getDefaultMaxSpeed()));
                    }
                    Double maxCapacity_ = maxDefaultAttr.putIfAbsent("LANE_CAPACITY_FIELD", transMode.getDefaultLaneCapacity());
                    if (maxCapacity_ != null){
                        maxDefaultAttr.put("LANE_CAPACITY_FIELD", Math.max(maxCapacity_, transMode.getDefaultLaneCapacity()));
                    }
                    Double maxWidth_ = maxDefaultAttr.putIfAbsent("LANE_WIDTH_FIELD", transMode.getDefaultLaneWidth());
                    if (maxWidth_ != null){
                        maxDefaultAttr.put("LANE_WIDTH_FIELD", Math.max(maxWidth_, transMode.getDefaultLaneWidth()));
                    }
                    Double maxLanes_ = maxDefaultAttr.putIfAbsent("LANES_FIELD", transMode.getDefaultLanes());
                    if (maxLanes_ != null){
                        maxDefaultAttr.put("LANES_FIELD", Math.max(maxLanes_, transMode.getDefaultLanes()));
                    }
                }
            });
        });


        // Match the LinkAttrParamSet based on the key-value pairs
        this.config.getLinkAttrParamSet().getParams().forEach((param, field) -> {
            // if the key-value pairs contain the field, get the value
            if (link.getKeyValuePairs().containsKey(field)
                    && link.getKeyValuePairs().get(field) != null
                    && !link.getKeyValuePairs().get(field).trim().isEmpty()){
                try {
                    linkAttr.put(param, Double.parseDouble(link.getKeyValuePairs().get(field)));
                } catch (NumberFormatException e){
                    if (param.equals("LENGTH_FIELD")){
                        double length;
                        // if the field is length, calculate the length based on the coordinates of the fromNode and toNode
                        if (link.getFromNode().getCoord().hasZ()){
                            length = Utils.calculateDistWithElevation(link.getFromNode().getCoord(), link.getToNode().getCoord());
                        } else {
                            length = Utils.calculateHaversineDist(link.getFromNode().getCoord(), link.getToNode().getCoord());
                    }
                        linkAttr.put(param, length);
                }
                    linkAttr.put(param, maxDefaultAttr.get(param));
                }
            } else {
                linkAttr.put(param, maxDefaultAttr.get(param));
            }
        });

        return linkAttr;
    }

    private void processConnectedNetwork(Network network){
        // Process the connected network
        if (config.getConnectedNetworkParamSet().STRONGLY_CONNECTED) {
            // Process the connected network based on the specified strategy
            switch (config.getConnectedNetworkParamSet().METHOD) {
                case "reduce":
                    // Remove inaccessible nodes and links
                    MultimodalNetworkCleaner cleaner = new MultimodalNetworkCleaner(network);
                    Set<String> modes = new HashSet<>();
                    config.getConnectedNetworkParamSet().MODE.forEach(mode -> modes.add(mode.name));
                    // make carMode link is not removed
                    cleaner.run(modes, Set.of("car"));
                    break;
                case "insert":
                    // Remove the disconnected nodes and links
                    break;
                case "adapt_mode":
                    // Remove the nodes and links that are isolated based on the threshold
                    break;
            }
        }
    }



}
