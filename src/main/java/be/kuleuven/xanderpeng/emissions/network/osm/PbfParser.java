package be.kuleuven.xanderpeng.emissions.network.osm;

import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.access.OsmReader;
import de.topobyte.osm4j.core.model.iface.*;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.core.model.impl.Relation;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.pbf.seq.PbfReader;
import de.topobyte.osm4j.pbf.util.PbfUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import  java.util.Map.Entry;

import be.kuleuven.xanderpeng.emissions.network.utils;
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
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

public class PbfParser {

    public static void main(String[] args) {
        String filePath = utils.aldiNetworkInput;
        String fileOutputPath = utils.networkOutputDir + "MyOwnTransformedNetwork.xml";
        MyOsmHandler myHandler = new MyOsmHandler();

        try (InputStream inputStream = new FileInputStream(filePath)) {
            PbfReader reader = new PbfReader(inputStream, false);
            reader.setHandler(myHandler);
            reader.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Osm2MatsimNetwork converter = new Osm2MatsimNetwork("EPSG:31370");
        converter.createMatsimNetwork(myHandler.nodes, myHandler.ways, fileOutputPath);

    }

static class Osm2MatsimNetwork {
    private final CoordinateTransformation transformation;

    public Osm2MatsimNetwork(String outputCRS) {
        this.transformation = TransformationFactory.getCoordinateTransformation(
                TransformationFactory.WGS84, outputCRS
        );
    }

    public void createMatsimNetwork(Map<Long, OsmNode> osmNodes, Map<Long, OsmWay> osmWays, String outputNetworkFile) {
        // 创建一个空的MATSim网络
        Network matsimNetwork = NetworkUtils.createNetwork();
        NetworkFactory networkFactory = matsimNetwork.getFactory();


        // 遍历所有的OSM节点，为每个节点在MATSim网络中创建一个对应的节点
        for (OsmNode osmNode : osmNodes.values()) {
            Coord coord = new Coord(osmNode.getLongitude(), osmNode.getLatitude());
            Coord transformedCoord = transformation.transform(coord);
            org.matsim.api.core.v01.network.Node matsimNode = networkFactory.createNode(Id.createNodeId(osmNode.getId()), transformedCoord);
            matsimNetwork.addNode(matsimNode);
        }

        // 遍历所有的OSM路段，为每个路段在MATSim网络中创建一个对应的链接
        for (OsmWay osmWay : osmWays.values()) {
            int nNodes = osmWay.getNumberOfNodes();
            org.matsim.api.core.v01.network.Node fromNode = matsimNetwork.getNodes().get(Id.createNodeId(osmWay.getNodeId(0)));
            Node toNode = matsimNetwork.getNodes().get(Id.createNodeId(osmWay.getNodeId(nNodes - 1)));

            Link matsimLink = networkFactory.createLink(Id.createLinkId(osmWay.getId()), fromNode, toNode);
            // Add Attributes
            Map<String, String> tags = OsmModelUtil.getTagsAsMap(osmWay);
            matsimLink.getAttributes().putAttribute("osm_id", osmWay.getId());
            matsimLink.getAttributes().putAttribute("highway", tags.get("highway"));

            // Add Modes
            HashSet<String> allowedModes = new HashSet<>();
            if ("cycleway".equals(tags.get("highway"))) {
                allowedModes.add(TransportMode.bike);}
            if("footway".equals(tags.get("highway"))){
                allowedModes.add(TransportMode.walk);
            }
            matsimLink.setAllowedModes(allowedModes);

            matsimNetwork.addLink(matsimLink);
            // if the link is not oneway link (tags.get("oneway") == "no"), then add the reverse link
            if (!"yes".equals(tags.get("oneway"))) {
                Link reverseLink = networkFactory.createLink(Id.createLinkId(osmWay.getId() + "_reverse"), toNode, fromNode);

                reverseLink.getAttributes().putAttribute("osm_id", osmWay.getId());
                reverseLink.getAttributes().putAttribute("highway", tags.get("highway"));
                reverseLink.setAllowedModes(allowedModes);
                matsimNetwork.addLink(reverseLink);
            }
        }

        // 将创建的MATSim网络写入文件
        new NetworkWriter(matsimNetwork).write(outputNetworkFile);
    }
}



    static class MyOsmHandler implements OsmHandler {
        HashMap <Long, OsmNode> nodes = new HashMap<>();
        HashMap <Long, OsmWay> ways = new HashMap<>();

        @Override
        public void handle(OsmBounds osmBounds) throws IOException {
            System.out.println("Bounds: " + osmBounds);
        }

        @Override
        public void handle(OsmNode node) {
            nodes.put(node.getId(), node);

            System.out.println("Node: " + node.getId() + " - Lat: " + node.getLatitude() + ", Lon: " + node.getLongitude());
            int nTags = node.getNumberOfTags();
            for (int i = 0; i < nTags; i++) {
                OsmTag tag = node.getTag(i);
                System.out.println("  Tag: " + tag.getKey() + " = " + tag.getValue());
            }
        }

        @Override
        public void handle(OsmWay way) {

            // Map<tagKey, tagValue>
            Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
            /* 如果这条 way tags符合以下几种情况，则保留：
                * 1. highway=cycleway
                * 2. highway=steps 且 ramp=no
                * 3. bicycle=yes
                * 4. tag的key里包含“cycleway” (例如：cycleway:left), value不是no
            */


            if ("cycleway".equals(tags.get("highway"))  || ("footway".equals(tags.get("highway")))){
                System.out.println("Way: " + way.getId());

                for (Entry<String, String> entry : tags.entrySet()) {
                    System.out.println("  Tag: " + entry.getKey() + " = " + entry.getValue());
                }
                for (int i = 0; i < way.getNumberOfNodes(); i++) {
                    System.out.println("  Node: " + way.getNodeId(i));
                }
                ways.put(way.getId(), way);
            }
        }

        @Override
        public void handle(OsmRelation relation) {
            System.out.println("Relation: " + relation.getId());
        }

        @Override
        public void complete() {
            System.out.println("Parsing complete");
        }
    }
}
