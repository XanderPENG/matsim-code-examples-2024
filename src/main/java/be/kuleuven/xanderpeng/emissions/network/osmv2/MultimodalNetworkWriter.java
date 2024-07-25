package be.kuleuven.xanderpeng.emissions.network.osmv2;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import org.matsim.core.utils.geometry.CoordinateTransformation;

import java.util.Map;

public class MultimodalNetworkWriter {

    private final CoordinateTransformation transformation;
    private final String outputFilePath;
    private final Map<String, Map<Long,?>> rawOsmNetwork;


    private MultimodalNetworkWriter(Builder builder) {
        this.transformation = builder.transformation;
        this.outputFilePath = builder.outputFilePath;
        this.rawOsmNetwork = builder.rawOsmNetwork;
    }

    private void convert(){
        Map<Long, ?> rawNodes = rawOsmNetwork.get("nodes");


    }



    private static class Builder{
        private CoordinateTransformation transformation;
        private String outputFilePath;
        private Map<String, Map<Long,?>> rawOsmNetwork;

        public Builder setTransformation(CoordinateTransformation transformation) {
            this.transformation = transformation;
            return this;
        }

        public Builder setOutputFilePath(String outputFilePath) {
            this.outputFilePath = outputFilePath;
            return this;
        }

        public Builder setRawOsmNetwork(Map<String, Map<Long,?>> rawOsmNetwork) {
            this.rawOsmNetwork = rawOsmNetwork;
            return this;
        }

        public MultimodalNetworkWriter build() {
            return new MultimodalNetworkWriter(this);
        }
    }

}
