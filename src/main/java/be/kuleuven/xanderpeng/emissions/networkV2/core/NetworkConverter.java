package be.kuleuven.xanderpeng.emissions.networkV2.core;

import be.kuleuven.xanderpeng.emissions.networkV2.config.NetworkConverterConfigGroup;
import be.kuleuven.xanderpeng.emissions.networkV2.readers.GeoJsonReader;
import be.kuleuven.xanderpeng.emissions.networkV2.readers.OsmReader;
import be.kuleuven.xanderpeng.emissions.networkV2.readers.Reader;
import be.kuleuven.xanderpeng.emissions.networkV2.readers.ShpReader;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.core.network.NetworkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class NetworkConverter {

    Logger LOG = LogManager.getLogger(NetworkConverter.class);
    private final Reader reader;
    private final NetworkConverterConfigGroup config;

    private final Map<String, NetworkElement.Node> interimNodes = new HashMap<>();
    private final Map<String, NetworkElement.Link> interimLinks = new HashMap<>();

    public NetworkConverter(NetworkConverterConfigGroup config) {
        {
            this.config = config;
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
        }
    }


    public Network convert() throws IOException {
        LOG.info("Start converting the input network file to MATSim network...");
        // Create matsim network and factory instance
        Network network = NetworkUtils.createNetwork();
        NetworkFactory networkFactory = network.getFactory();
        // Read the input network file
        LOG.info("Reading the input network file: {}", config.INPUT_NETWORK_FILE);
        reader.read(config.INPUT_NETWORK_FILE);

        return network;
    }

    // Split the link if it is composed of multiple nodes


}
