package be.kuleuven.xanderpeng.emissions.network;

import org.matsim.pt2matsim.config.OsmConverterConfigGroup;
import org.matsim.pt2matsim.run.Osm2MultimodalNetwork;

public class RunCreateMultimodalNetworkByPT2Matsim {
    private static final String osmFile = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/input/mapSample.osm"; //  OSM file path
    private static final String outputNetworkFile = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/output/MultimodalNetworkPT.xml"; // output network path
    private static final String outputCoordinateSystem = "EPSG:32631"; // output coordinate system
    private static final String outputConfigFile = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/output/ptOsmNetworkConfig.xml"; // output config file path


    public static void main(String[] args) {
        OsmConverterConfigGroup config = OsmConverterConfigGroup.createDefaultConfig();
        config.setKeepPaths(true);
        config.setOsmFile(osmFile);
        config.setOutputNetworkFile(outputNetworkFile);
        config.setOutputCoordinateSystem(outputCoordinateSystem);
        config.writeToFile(outputConfigFile);
        Osm2MultimodalNetwork.run(config);
    }
}
