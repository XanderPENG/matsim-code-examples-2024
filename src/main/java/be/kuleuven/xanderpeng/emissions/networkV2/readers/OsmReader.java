package be.kuleuven.xanderpeng.emissions.networkV2.readers;

import be.kuleuven.xanderpeng.emissions.networkV2.core.NetworkElement;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OsmReader implements OsmHandler, Reader<Long> {

    // The raw nodes and links from the OSM file
    private final Map<Long, NetworkElement.Node> rawNodes = new HashMap<>();
    private final Map<Long, NetworkElement.Link> rawLinks = new HashMap<>();

    public OsmReader() {
    }


    private void handleNode(OsmNode osmNode){
        // Convert the OsmNode to NetworkElement.Node
        NetworkElement.Node rawNode = new NetworkElement.Node(osmNode.getId(), osmNode.getLongitude(), osmNode.getLatitude());
        rawNodes.put(osmNode.getId(), rawNode);
    }

    // Convert the OsmWay to NetworkElement.Link
    private void handleWay(OsmWay osmWay){

        Map<String, String> tagValuePairs = OsmModelUtil.getTagsAsMap(osmWay);
        // Get all the node ids of the way
        int numNodes = osmWay.getNumberOfNodes();
        Set<Long> nodeIds = new HashSet<>();
        for(int i = 0; i < numNodes; i++){
            nodeIds.add(osmWay.getNodeId(i));
        }
        // Create the link
        NetworkElement.Link rawLink = new NetworkElement.Link(osmWay.getId(),
                rawNodes.get(osmWay.getNodeId(0)), rawNodes.get(osmWay.getNodeId(numNodes-1)));

        // Add the composed nodes to the link if there are more than 2 nodes
        if (nodeIds.size() > 2) {
            // filter out the first and last node
            nodeIds.remove(osmWay.getNodeId(0));
            nodeIds.remove(osmWay.getNodeId(numNodes - 1));
            nodeIds.forEach(nodeId -> rawLink.addComposedNode(rawNodes.get(nodeId)));
        }

        rawLink.setKeyValuePairs(tagValuePairs);
        rawLinks.put(osmWay.getId(), rawLink);
    }

    @Override
    public void read(String file) throws IOException {

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
    public Map<Long, NetworkElement.Node> getRawNodes() {
        return rawNodes;
    }

    @Override
    public Map<Long, NetworkElement.Link> getRawLinks() {
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
