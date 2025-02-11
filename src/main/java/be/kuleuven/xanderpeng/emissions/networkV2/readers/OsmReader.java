package be.kuleuven.xanderpeng.emissions.networkV2.readers;

import be.kuleuven.xanderpeng.emissions.networkV2.core.NetworkElement;
import be.kuleuven.xanderpeng.emissions.networkV2.tools.Utils;
import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.pbf.seq.PbfReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to read the OSM file (.pbf format) and convert it to raw nodes and links.
 * TODO: Process the OSM relation to get the PT-related information
 */
public class OsmReader implements OsmHandler, Reader {

    // The raw nodes and links from the OSM file
    private final Map<String, NetworkElement.Node> rawNodes = new HashMap<>();
    private final Map<String, NetworkElement.Link> rawLinks = new HashMap<>();

    public OsmReader() {
    }


    private void handleNode(OsmNode osmNode){
        // Convert the OsmNode to NetworkElement.Node
        NetworkElement.Node rawNode = new NetworkElement.Node(osmNode.getId(), osmNode.getLongitude(), osmNode.getLatitude());
        rawNodes.put(Utils.id2String(osmNode.getId()), rawNode);
    }

    // Convert the OsmWay to NetworkElement.Link
    private void handleWay(OsmWay osmWay){

        Map<String, String> tagValuePairs = OsmModelUtil.getTagsAsMap(osmWay);
        // Get all the node ids of the way
        int numNodes = osmWay.getNumberOfNodes();
        Set<Long> nodeIds = new LinkedHashSet<>();
        for(int i = 0; i < numNodes; i++){
            nodeIds.add(osmWay.getNodeId(i));
        }
        // Create the link
        NetworkElement.Link rawLink = new NetworkElement.Link(osmWay.getId(),
                rawNodes.get(Utils.id2String(osmWay.getNodeId(0))), rawNodes.get(Utils.id2String(osmWay.getNodeId(numNodes-1))));

        // Add the composed nodes to the link if there are more than 2 nodes
        if (nodeIds.size() > 2) {
            // filter out the first and last node
            nodeIds.remove(osmWay.getNodeId(0));
            nodeIds.remove(osmWay.getNodeId(numNodes - 1));
            nodeIds.forEach(nodeId -> rawLink.addComposedNode(rawNodes.get(Utils.id2String(nodeId))));
        }

        rawLink.setKeyValuePairs(tagValuePairs);
        rawLinks.put(Utils.id2String(osmWay.getId()), rawLink);
    }

    @Override
    public void read(String file) {

        // Read the PBF file
        try (InputStream inputStream = new FileInputStream(file)) {
            PbfReader reader = new PbfReader(inputStream, false);
            reader.setHandler(this);
            reader.read();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read PBF file", e);
        }
    }

    @Override
    public Map<String, NetworkElement.Node> getRawNodes() {
        return rawNodes;
    }

    @Override
    public Map<String, NetworkElement.Link> getRawLinks() {
        return rawLinks;
    }


    // Override the methods from OsmHandler
    @Override
    public void handle(OsmBounds osmBounds) throws IOException {

    }

    @Override
    public void handle(OsmNode osmNode) throws IOException {
        handleNode(osmNode);
    }

    @Override
    public void handle(OsmWay osmWay) throws IOException {
        handleWay(osmWay);
    }

    @Override
    public void handle(OsmRelation osmRelation) throws IOException {

    }

    @Override
    public void complete() throws IOException {

    }
}
