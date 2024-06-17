package be.kuleuven.xanderpeng.emissions.network;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.osm.networkReader.OsmBicycleReader;
import org.matsim.core.network.algorithms.MultimodalNetworkCleaner;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

public class RunCreateCycleNetwork {
    private static final String osmFile = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/input/mapSample.pbf"; //  OSM file path
    private static final String outputCRS = "EPSG:32631"; //UTM-31N
    private static final String outputNetworkFile = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/output/bicycleNetwork.xml";

    public static void main(String[] args) {
        // Transformation from WGS84 to UTM zone 31N
        CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation(
                TransformationFactory.WGS84, outputCRS
        );
        Network network = new OsmBicycleReader.Builder()
                .setCoordinateTransformation(transformation)
                .build()
                .read(osmFile);


        new NetworkWriter(network).write(outputNetworkFile);
    }
}
