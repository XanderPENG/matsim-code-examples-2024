package be.kuleuven.xanderpeng.emissions.network.osmv2;

import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.pbf.seq.PbfReader;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MultimodalOsmReader implements OsmHandler {

    // The raw OSM elements
    private final Map<Long, OsmNode> nodes;
    private final Map<Long, OsmWay> ways;
    private final Map<Long, OsmRelation> relations;
    private final Map<Long, Set<String>> wayModes;

    // The processed Network elements
    private final Map<Id<Node>, Node> processedNodes = new HashMap<>();
    private final Map<Id<Link>, Link> processedWays = new HashMap<>();
    private final Map<Long, OsmRelation> processedRelations = new HashMap<>();
    private final Map<Id<Link>, Set<String>> processedWayModes = new HashMap<>();


    private final OsmElementHandler elementHandler;
    private final String inputFilePath;
    private final TransportModeTagMapper transportModeTagMapper;
    private final CoordinateTransformation transformation;

    private MultimodalOsmReader(Builder builder) {
        nodes = new HashMap<>();
        ways = new HashMap<>();
        relations = new HashMap<>();
        wayModes = new HashMap<>();

        elementHandler = builder.elementHandler;
        inputFilePath = builder.inputFilePath;
        transportModeTagMapper = builder.transportModeTagMapper;
        transformation = builder.transformation;
    }


    @Override
    public void handle(OsmBounds osmBounds) throws IOException {

    }

    @Override
    public void handle(OsmNode osmNode) throws IOException {
        elementHandler.handleNode(osmNode, nodes);
    }

    @Override
    public void handle(OsmWay osmWay) throws IOException {
        elementHandler.handleWay(osmWay, ways, wayModes, transportModeTagMapper);
    }

    @Override
    public void handle(OsmRelation osmRelation) throws IOException {
         elementHandler.handleRelation(osmRelation, relations);
    }

    @Override
    public void complete() throws IOException {

    }

    public Network read(){

        Network network = NetworkUtils.createNetwork();
        NetworkFactory networkFactory = network.getFactory();

        // Read the PBF file
        try (InputStream inputStream = new FileInputStream(this.inputFilePath)) {
            PbfReader reader = new PbfReader(inputStream, false);
            reader.setHandler(this);
            reader.read();
            this.splitWayAtIntersection(networkFactory, network);
            // Add processed nodes and ways to the network
            for (Node node : processedNodes.values()){

                network.addNode(node);
            }
            for (Link link : processedWays.values()){
                network.addLink(link);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read PBF file", e);
        }
        return  network;
    }

    /*
    * Process the raw OSM data, including
    *  - splitting ways at intersections,
    *  - creating reversed ways (based on the 'oneway' tag),
    *  - filtering out useless nodes and ways
     */
    private void processRawOsmData(NetworkFactory networkFactory){

    }


    // Convert the OSM data to a MATSim network
    private void convert(){


    }

    private void splitWayAtIntersection(NetworkFactory networkFactory, Network network){
        // Count the number of times each node is used
        Map<Long, Integer> nodeRefCount = new HashMap<>();
        for (OsmWay way : ways.values()){
            int nNodes = way.getNumberOfNodes();
            for (int i = 0; i < nNodes; i++){
                long nodeId = way.getNodeId(i);
                if (nodeRefCount.containsKey(nodeId)){
                    nodeRefCount.put(nodeId, nodeRefCount.get(nodeId) + 1);
                } else {
                    nodeRefCount.put(nodeId, 1);
                }
            }
        }
        // Split the way at intersections (if applicable)
        for (OsmWay way : ways.values()){
            Set <String> reversedWayModes = onewayHandler(way);
            boolean isOneway = reversedWayModes.isEmpty();
            Node startNode;

            int nNodes = way.getNumberOfNodes();
            // Get the Coord of start node of the way, and transform it
            Coord startCoord = new Coord(nodes.get(way.getNodeId(0)).getLongitude(), nodes.get(way.getNodeId(0)).getLatitude());
            Coord transformedStartCoord = transformation.transform(startCoord);

            if (!processedNodes.containsKey(Id.createNodeId(way.getNodeId(0)))){
                // Create a MATSim node at the start of the way
                startNode = networkFactory.createNode(Id.createNodeId(way.getNodeId(0)), transformedStartCoord);
                // Add the node to the processedNodes
                processedNodes.put(startNode.getId(), startNode);
            } else {
                startNode = processedNodes.get(Id.createNodeId(way.getNodeId(0)));
            }

            int [] segmentIndex = new int[] {1};
            Node [] fromNodeContainer = new Node[]{startNode};
//            Node fromNode = startNode;
            for (int i = 1; i < nNodes-1; i++){

                long nodeId = way.getNodeId(i);
                // Split the way at the intersection
                if (nodeRefCount.get(nodeId) > 1){
                    // Firstly create a MATSim node at the intersection (if needed)
                    Node  intersectionNode;
                    if (processedNodes.containsKey(Id.createNodeId(nodeId))){
                        // If the node is already created, get the node
                        intersectionNode = processedNodes.get(Id.createNodeId(nodeId));
                    } else {
                        // If the node is not created, create the node
                        Coord intersectionNodeCoord = new Coord(nodes.get(nodeId).getLongitude(), nodes.get(nodeId).getLatitude());
                        Coord transformedIntersectionNodeCoord = transformation.transform(intersectionNodeCoord);
                        intersectionNode = networkFactory.createNode(Id.createNodeId(nodeId), transformedIntersectionNodeCoord);
                        // Add the node to the processedNodes
                        processedNodes.put(intersectionNode.getId(), intersectionNode);
                    }
                    // Add the link segment to the matsim network
                    Link linkSeg = networkFactory.createLink(Id.createLinkId(way.getId() + "_f" + segmentIndex[0]),
                            fromNodeContainer[0], intersectionNode);
                    // Add the attributes
                    linkSeg.getAttributes().putAttribute("osm_id", way.getId());
                    // TODO: This can be improved by match all "cycleway"-related tags, and reclassified to [share, exclusive, advisory, separated, etc.
                    linkSeg.getAttributes().putAttribute("cycleway_type", OsmModelUtil.getTagsAsMap(way).get("cycleway") == null ? "none" : OsmModelUtil.getTagsAsMap(way).get("cycleway"));
                    linkSeg.getAttributes().putAttribute("cycleway_width", OsmModelUtil.getTagsAsMap(way).get("cycleway:width") == null ? 2.3 : Float.parseFloat(OsmModelUtil.getTagsAsMap(way).get("cycleway:width")));
                    // Add allowed modes
                    linkSeg.setAllowedModes(wayModes.get(way.getId()));
                    // Add the link to the processedWays
                    processedWays.put(linkSeg.getId(), linkSeg);
                    /*
                        Add a reverse link if the way is not oneway
                     */
                    if (!isOneway){
                        Link reverseLink = networkFactory.createLink(Id.createLinkId(way.getId() + "_r" + segmentIndex[0]),
                                intersectionNode, fromNodeContainer[0]);
                        // Add the attributes
                        reverseLink.getAttributes().putAttribute("osm_id", way.getId());
                        reverseLink.getAttributes().putAttribute("cycleway_type", OsmModelUtil.getTagsAsMap(way).get("cycleway") == null ? "none" : OsmModelUtil.getTagsAsMap(way).get("cycleway"));
                        reverseLink.getAttributes().putAttribute("cycleway_width", OsmModelUtil.getTagsAsMap(way).get("cycleway:width") == null ? 2.3 : Float.parseFloat(OsmModelUtil.getTagsAsMap(way).get("cycleway:width")));
                        // Add allowed modes
                        reverseLink.setAllowedModes(reversedWayModes);
                        // Add the reverse link to the processedWays
                        processedWays.put(reverseLink.getId(), reverseLink);
                    }

                    // Update the fromNode
                    fromNodeContainer[0] = intersectionNode;
                    segmentIndex[0] += 1;
                }
            }

            // Add the end node (if needed)
            Node endNode;
            if (processedNodes.containsKey(Id.createNodeId(way.getNodeId(nNodes - 1)))){
                endNode = processedNodes.get(Id.createNodeId(way.getNodeId(nNodes - 1)));
            } else {
                Coord endCoord = new Coord(nodes.get(way.getNodeId(nNodes - 1)).getLongitude(), nodes.get(way.getNodeId(nNodes - 1)).getLatitude());
                Coord transformedEndCoord = transformation.transform(endCoord);
                endNode = networkFactory.createNode(Id.createNodeId(way.getNodeId(nNodes - 1)), transformedEndCoord);
                processedNodes.put(endNode.getId(), endNode);
            }
            // Add the last link segment
            Link lastLinkSeg = networkFactory.createLink(Id.createLinkId(way.getId() + "_f" + segmentIndex[0]),
                    fromNodeContainer[0], endNode);
            // Add the attributes
            lastLinkSeg.getAttributes().putAttribute("osm_id", way.getId());
            lastLinkSeg.getAttributes().putAttribute("cycleway_type", OsmModelUtil.getTagsAsMap(way).get("cycleway") == null ? "none" : OsmModelUtil.getTagsAsMap(way).get("cycleway"));
            lastLinkSeg.getAttributes().putAttribute("cycleway_width", OsmModelUtil.getTagsAsMap(way).get("cycleway:width") == null ? 2.3 : Float.parseFloat(OsmModelUtil.getTagsAsMap(way).get("cycleway:width")));
            // Add allowed modes
            lastLinkSeg.setAllowedModes(wayModes.get(way.getId()));
            // Add the link to the processedWays
            processedWays.put(lastLinkSeg.getId(), lastLinkSeg);
            /*
                Add a reverse link if the way is not oneway
             */
            if (!isOneway){
                Link reverseLink = networkFactory.createLink(Id.createLinkId(way.getId() + "_r" + segmentIndex[0]),
                        endNode, fromNodeContainer[0]);
                // Add the attributes
                reverseLink.getAttributes().putAttribute("osm_id", way.getId());
                reverseLink.getAttributes().putAttribute("cycleway_type", OsmModelUtil.getTagsAsMap(way).get("cycleway") == null ? "none" : OsmModelUtil.getTagsAsMap(way).get("cycleway"));
                reverseLink.getAttributes().putAttribute("cycleway_width", OsmModelUtil.getTagsAsMap(way).get("cycleway:width") == null ? 2.3 : Float.parseFloat(OsmModelUtil.getTagsAsMap(way).get("cycleway:width")));
                // Add allowed modes
                reverseLink.setAllowedModes(reversedWayModes);
                // Add the reverse link to the processedWays
                processedWays.put(reverseLink.getId(), reverseLink);

            }
        }
    }

    private Set<String> onewayHandler(OsmWay way){
        Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
        Set<String> reversedWayTypes = new HashSet<>();
        List <String> diWayValues = List.of("no", "0", "false");  // common values for two-way streets

        if (tags.containsKey("oneway") && diWayValues.contains(tags.get("oneway")) ){
            reversedWayTypes.addAll(wayModes.get(way.getId()));
//            reversedWayTypes.add(TransportModeTagMapper.CAR);
//            reversedWayTypes.add(TransportModeTagMapper.BIKE);
//            reversedWayTypes.add(TransportModeTagMapper.WALK);
        } else if (tags.containsKey("oneway:bicycle") && diWayValues.contains(tags.get("oneway:bicycle"))){
            reversedWayTypes.add(TransportModeTagMapper.BIKE);
        }else if (tags.containsKey("oneway:foot") && diWayValues.contains(tags.get("oneway:foot"))
                ||  (tags.containsKey("sidewalk") && "both".equals(tags.get("sidewalk")))
                    ) {
            reversedWayTypes.add(TransportModeTagMapper.WALK);
        }
        return reversedWayTypes;
    }




    public static class Builder {

        private OsmElementHandler elementHandler;
        private String inputFilePath;
        private TransportModeTagMapper transportModeTagMapper;
        private CoordinateTransformation transformation;

        public Builder(String inputFilePath) {
            this.inputFilePath = inputFilePath;
        }

        public Builder setInputFilePath(String inputFilePath) {
            this.inputFilePath = inputFilePath;
            return this;
        }

        public Builder setHandler(OsmElementHandler elementHandler) {
            this.elementHandler = elementHandler;
            return this;
        }

        public Builder setTransportModeTagMapper(TransportModeTagMapper transportModeTagMapper) {
            this.transportModeTagMapper = transportModeTagMapper;
            return this;
        }

        public Builder setTransformation(CoordinateTransformation transformation) {
            this.transformation = transformation;
            return this;
        }


        public MultimodalOsmReader build() {
            return new MultimodalOsmReader(this);
        }
    }

}
