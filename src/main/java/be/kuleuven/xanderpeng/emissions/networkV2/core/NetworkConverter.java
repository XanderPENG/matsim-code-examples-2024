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
        switch (config.FILE_TYPE) {
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
                throw new IllegalArgumentException("Unsupported file type: " + config.FILE_TYPE);
        }
        // Initialize the configuredTransModes
        config.getModeParamSets().forEach((mode, modeParamSet) -> {
            configuredTransModes.add(modeParamSet.getTransMode());
        });

    }
    /* TODO: 1. Set "oneway" attribute, which should be indicated in the config file
                1.1. oneway tag-value pairs should be defined in the config file
                1.2 use a global parameter to indicate whether the network is one-way or two-way; e.g., boolean ONE_WAY = true;
             2. More options/parameters for the CONNECTED_NETWORK, and thus it is better to create a ParameterSet for it
                2.1. Users can opt to keep the original network structure -- CONNECTED_NETWORK = false
                2.2. Users can opt to connect the isolated nodes/links to the nearest node/link -- CONNECTED_NETWORK = true
                2.3. Users can opt to remove the isolated nodes/links, and only keep the largest connected subnetwork -- CONNECTED_NETWORK = true
                2.4. Users can opt a threshold to determine whether a node/link is isolated or not, which is a combination strategy of (2.2 & 2.3) -- CONNECTED_NETWORK = true
                2.5. Users can opt a specific transMode instead of the whole network -- CONNECTED_NETWORK = true
             4. function: Process Oneway
             5  function: Process the connected network
    */
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
            /* TODO: Here, we should specify the link capacity, freespeed, width, etc. To this end:
                        1. The field containing capacity, freespeed, width, etc. should be specified in the config file (maybe a new ParameterSet is needed)
                        2. The capacity, freespeed, width, etc. should be set based on the key-value pairs of the link
                        3. If there is no tag-value pair for a specific attr, the capacity, freespeed, width, etc. should be set based on the transMode of the link
                            ATTENTION: a link may have several allowed TransModes, and the value of attr (capacity, freespeed, width, etc.) should be set based on the greatest one.

             */
            // Create the link by NetworkUtils.createAndAddLink, whether we could configure the attr, since the default value is irrational.
//            NetworkUtils.createLink(network, Id.createLinkId(linkId), fromNode, toNode);
        });

        // Process the connected network



        return network;
    }

    private void matchLinkMode(NetworkElement.Link link) {
        // Match the transMode of the link based on the key-value pairs and the modeParamSets
        Set<TransMode.Mode> matchedModes = new HashSet<>();
        // Match the transMode based on the key-value pairs
        configuredTransModes.forEach(transMode -> {
            if (transMode.matchTransMode(link.getKeyValuePairs())) {
                matchedModes.add(transMode.getMode());
            }
        });
        // If the link is not aligned with any pre-defined transMode, while the KEEP_UNDEFINED_LINK is true, keep the link
        if (matchedModes.isEmpty() && config.KEEP_UNDEFINED_LINK) {
            matchedModes.add(TransMode.Mode.OTHER);
        }
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




}
