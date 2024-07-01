package be.kuleuven.xanderpeng.emissions.network.osm;

import be.kuleuven.xanderpeng.emissions.network.utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.opengis.referencing.operation.Transformation;


public class RunCreateNetwork {
    private final static Logger LOG = LogManager.getLogger(RunCreateNetwork.class);

    static String inputFilePath = utils.aldiNetworkInput;
    static String outputCrs = utils.outputCrs;
    static String outputDir = utils.networkOutputDir;

    public static void main(String[] args) {
        LOG.info("Started");

       CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation(
                TransformationFactory.WGS84, outputCrs
        );
       OsmElementHandler handler = new OsmElementHandlerImpl();

        Osm2MatsimMultimodalNetwork osm2MatsimMultimodalNetwork =
                new Osm2MatsimMultimodalNetwork(inputFilePath, transformation, handler);

        osm2MatsimMultimodalNetwork.convert(outputDir+"myMultimodalNetwork.xml");
        LOG.info("Finished");
    }
}
