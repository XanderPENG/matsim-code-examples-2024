package be.kuleuven.xanderpeng.emissions.network;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.contrib.osm.networkReader.OsmBicycleReader;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

public class RunAddAttr2Network {
    private static final String osmFile = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/input/mapSample.pbf"; //  OSM file path
    private static final String outputCRS = "EPSG:31370"; //UTM-31N; lambert 72; 31370
    private static final String outputNetworkFile = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/output/bicycleNetworkWithAttr.xml";

    public static void main(String[] args) {
        // Transformation from WGS84 to UTM zone 31N
        CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation(
                TransformationFactory.WGS84, outputCRS
        );

        // add '"accessType": "access" to the link attributes if the link has the "access" tag
        SupersonicOsmNetworkReader.AfterLinkCreated addAccessType = (link, osmTags, direction) -> {
            if (osmTags.containsKey("access")) {
                String accessType = osmTags.get("access");
                link.getAttributes().putAttribute("accessType", accessType);
            }
        };

        OsmBicycleReader reader = new OsmBicycleReader.Builder()
                .setCoordinateTransformation(transformation)
                .setAfterLinkCreated(addAccessType)
                .build();

        Network network = reader.read(osmFile);

        new NetworkWriter(network).write(outputNetworkFile);
    }
}
