package be.kuleuven.xanderpeng.emissions.network.osm;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.utils.geometry.CoordinateTransformation;

import java.util.HashSet;
import java.util.Map;

public class Osm2MatsimMultimodalNetwork {
    private final CoordinateTransformation transformation;

    private final MultimodalOsmReader multimodalOsmReader;
    private final String inputFilePath;



//    private final Map<Long, ProcessedOsmElements.ProcessedNode> processedNodes;
//    private final Map<Long, ProcessedOsmElements.ProcessedWay> processedWays;
//    private final Map<Long, ProcessedOsmElements.ProcessedRelation> processedRelations;

    public Osm2MatsimMultimodalNetwork(String inputFilePath, CoordinateTransformation transformation, OsmElementHandler osmElementHandler) {
        this.transformation = transformation;
        this.multimodalOsmReader = new MultimodalOsmReader(inputFilePath, osmElementHandler);
        this.inputFilePath = inputFilePath;

    }

    public void convert(String outputNetworkFile){
        this.multimodalOsmReader.read();
        // Create a new instance of the Matsim network
        Network matsimNetwork = NetworkUtils.createNetwork();
        NetworkFactory networkFactory = matsimNetwork.getFactory();

        // Get the raw nodes and ways from the OSM file after reading
        Map<Long, OsmNode> rawNodes = this.multimodalOsmReader.multimodalOsmHandler.getNodes();
        Map<Long, OsmWay> rawWays = this.multimodalOsmReader.multimodalOsmHandler.getWays();
        Map<Long, HashSet<String>> wayModes = this.multimodalOsmReader.multimodalOsmHandler.getWayModes();

        // TODO: Process the raw nodes and ways
//        ProcessedOsmElements processedOsmElements = processRawNodesAndWays(rawNodes, rawWays, wayModes);
//        Map<Long, OsmNode> processedNodes = processRawNodesAndWays(rawNodes, rawWays, wayModes);


        // Add the nodes to the MATSim network (using rawNodes for now)
        for (OsmNode osmNode : rawNodes.values()) {
            Coord coord = new Coord(osmNode.getLongitude(), osmNode.getLatitude());
            Coord transformedCoord = transformation.transform(coord);
            org.matsim.api.core.v01.network.Node matsimNode = networkFactory.createNode(Id.createNodeId(osmNode.getId()), transformedCoord);
            matsimNetwork.addNode(matsimNode);
        }

        for (OsmWay osmWay : rawWays.values()) {
            int nNodes = osmWay.getNumberOfNodes();
            Node fromNode = matsimNetwork.getNodes().get(Id.createNodeId(osmWay.getNodeId(0)));
            Node toNode = matsimNetwork.getNodes().get(Id.createNodeId(osmWay.getNodeId(nNodes - 1)));

            Link matsimLink = networkFactory.createLink(Id.createLinkId(osmWay.getId()), fromNode, toNode);
            // Add Attributes
            Map<String, String> tags = OsmModelUtil.getTagsAsMap(osmWay);
            matsimLink.getAttributes().putAttribute("osm_id", osmWay.getId());
            matsimLink.getAttributes().putAttribute("highway", tags.get("highway"));

            // Add Modes
            matsimLink.setAllowedModes(wayModes.get(osmWay.getId()));
            matsimNetwork.addLink(matsimLink);

            // if the link is not oneway link (tags.get("oneway") == "no"), then add the reverse link
            if (!"yes".equals(tags.get("oneway"))) {
                Link reverseLink = networkFactory.createLink(Id.createLinkId(osmWay.getId() + "_reverse"), toNode, fromNode);

                reverseLink.getAttributes().putAttribute("osm_id", osmWay.getId());
                reverseLink.getAttributes().putAttribute("highway", tags.get("highway"));
                reverseLink.setAllowedModes(wayModes.get(osmWay.getId()));
                matsimNetwork.addLink(reverseLink);
            }
        }

        // Write the MATSim network to a file
        new NetworkWriter(matsimNetwork).write(outputNetworkFile);

    }

    /*
    * TODO: Function for processing the raw nodes and ways:
    *  - Create a reversed link for ways with tag "oneway": "no";
    *  -  Split ways at intersections; and create nodes at intersections; and create links between new node pairs;
     */
    private ProcessedOsmElements processRawNodesAndWays(Map<Long, OsmNode> rawNodes, Map<Long, OsmWay> rawWays, Map<Long, HashSet<String>> wayModes){
        // Process the raw ways
        for (Map.Entry<Long, OsmWay> entry : rawWays.entrySet()) {
            OsmWay way = entry.getValue();
            Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
            HashSet<String> allowedTransportModes = wayModes.get(way.getId());
            int nNodes = way.getNumberOfNodes();
            long fromNodeId = way.getNodeId(0);
            long toNodeId = way.getNodeId(nNodes - 1);

            // Create a new instance of the ProcessedOsmElements.ProcessedWay
            ProcessedOsmElements.ProcessedWay processedWay = new ProcessedOsmElements.ProcessedWay(way.getId(),  fromNodeId, toNodeId, allowedTransportModes, tags);

        }

        return null;
    }
}
