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

    private void splitWayAtIntersection(NetworkFactory networkFactory){
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



            int nNodes = way.getNumberOfNodes();
            // Get the Coord of start node of the way, and transform it
            Coord startCoord = new Coord(nodes.get(way.getNodeId(0)).getLongitude(), nodes.get(way.getNodeId(0)).getLatitude());
            Coord transformedStartCoord = transformation.transform(startCoord);

            // Create a MATSim node at the start of the way
            Node fromNode = networkFactory.createNode(Id.createNodeId(way.getNodeId(0)), transformedStartCoord);


            for (int i = 1; i < nNodes; i++){
                long nodeId = way.getNodeId(i);
                if (nodeRefCount.get(nodeId) > 1){
                    // Split the way at the intersection
                    // Firstly create a MATSim node at the intersection
                    Coord intersectionNodeCoord = new Coord(nodes.get(nodeId).getLongitude(), nodes.get(nodeId).getLatitude());
                    Coord transformedIntersectionNodeCoord = transformation.transform(intersectionNodeCoord);
                    Node intersectionNode = networkFactory.createNode(Id.createNodeId(nodeId), transformedIntersectionNodeCoord);

                }
            }
        }

    }

    private List<String> isOneway(OsmWay way){
        Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
        List<String> reversedWayTypes = new ArrayList<>();
        if (tags.containsKey("oneway") && "no".equals(tags.get("oneway"))){
            reversedWayTypes.add(TransportModeTagMapper.CAR);
            reversedWayTypes.add(TransportModeTagMapper.BIKE);
        } else if (tags.containsKey("oneway:bicycle") && "no".equals(tags.get("oneway:bicycle"))){
            reversedWayTypes.add(TransportModeTagMapper.BIKE);
        }

        return reversedWayTypes;
    }



    private static class Builder {

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
