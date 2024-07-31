package be.kuleuven.xanderpeng.emissions.network.RunExistingReader;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

public class RunCreateBasicNXFromOSM {
    // choose an appropriate coordinate transformation: Leuven is in UTM zone 31N with EPSG code 32631
    private final String UTM31nAsEpsg = "EPSG:32631";
    private final String input = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/input/GreatLeuven.pbf";

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        new RunCreateBasicNXFromOSM().create();
    }

    private void create() {
        // Transformation from WGS84 to UTM zone 31N
        CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation(
                TransformationFactory.WGS84, UTM31nAsEpsg
        );
        // Create an OSM network reader
        SupersonicOsmNetworkReader reader = new SupersonicOsmNetworkReader.Builder()
                .setCoordinateTransformation(transformation)
                .build();

        Network network = reader.read(input);
        new NetworkWriter(network).write("src/main/resources/be.kuleuven.xanderpeng.emissions/network/output/GreatLeuven.xml");
    }

    private void createV2(){
        CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation(
                TransformationFactory.WGS84, UTM31nAsEpsg
        );
        // Create an OSM network reader
        SupersonicOsmNetworkReader reader = new SupersonicOsmNetworkReader.Builder()
                .setCoordinateTransformation(transformation)
                .build();
        Network network = reader.read(input);

    }


}
