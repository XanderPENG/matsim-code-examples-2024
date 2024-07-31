package be.kuleuven.xanderpeng.emissions.network.osmv2;

import be.kuleuven.xanderpeng.emissions.network.utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.util.List;


public class RunCreateNetwork {
    private final static Logger LOG = LogManager.getLogger(be.kuleuven.xanderpeng.emissions.network.osm.RunCreateNetwork.class);
    static String inputFilePath = utils.aldiNetworkInput;
    static String outputCrs = utils.outputCrs;
    static String outputDir = utils.networkOutputDir;




    public static void main(String[] args) {
        LOG.info("Started");

        CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation(
                TransformationFactory.WGS84, outputCrs
        );

        // Create a Handler for the OSM elements
        OsmElementHandler handler = new OsmElementHandlerImpl();

        /**
         *  Customize the to-be-processed OSM Tags-ValueLists,
         * and then create a {@link TransportModeTagMapper} object
         */
        // Car-related tags
        OsmTagsAndValueLists carTagAndValueLists = new OsmTagsAndValueLists.Builder()
                .addTagAndValues("highway",
                        List.of("trunk", "primary", "secondary", "tertiary", "living_street", "motorway" ,"unclassified",
                        "track", "service",  // TODO: not sure about these two types of roads
                        "residential", "motorway_link", "trunk_link", "primary_link", "secondary_link"))
                .build();

        // Bicycle-related tags
        OsmTagsAndValueLists bicycleTagAndValueLists = new OsmTagsAndValueLists.Builder()
                .addTagAndValues("bicycle", List.of("yes", "use_sidepath", "lane", "designated", "optional_sidepath", "customers"))
                .addTagAndValues("highway", List.of("cycleway", "living_street", "path", "track", "residential", "service", "unclassified"))
                .addTagAndValues("cycleway", List.of("lane", "exclusive", "shared", "track", "advisory"))
                .build();

        // Walk-related tags
        OsmTagsAndValueLists walkTagAndValueLists = new OsmTagsAndValueLists.Builder()
                .addTagAndValues("highway", List.of("footway", "pedestrian", "path", "steps"))
                .build();
        // Pt-related tags
        OsmTagsAndValueLists ptTagAndValueLists = new OsmTagsAndValueLists.Builder()
                .addTagAndValues("busway", List.of("lane"))
                .addTagAndValues("highway", List.of("primary", "secondary", "tertiary", "platform"))
                .build();

        TransportModeTagMapper transportModeTagMapper = new TransportModeTagMapper.Builder()
                .addModeAndTagValueLists(TransportModeTagMapper.CAR, carTagAndValueLists)
                .addModeAndTagValueLists(TransportModeTagMapper.BIKE, bicycleTagAndValueLists)
                .addModeAndTagValueLists(TransportModeTagMapper.WALK, walkTagAndValueLists)
                .addModeAndTagValueLists(TransportModeTagMapper.PT, ptTagAndValueLists)
                .build();
        MultimodalOsmReader reader = new MultimodalOsmReader.Builder(inputFilePath).setTransformation(transformation)
                        .setHandler(handler).setTransportModeTagMapper(transportModeTagMapper).build();
        LOG.info("Reading the OSM file");
        Network network = reader.read();
        LOG.info("Finished reading the OSM file");

        // Write the network to a file
        LOG.info("Writing the network");
        new NetworkWriter(network).write(outputDir + "multimodalNetworkV2.xml");

    }
}
