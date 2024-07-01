package be.kuleuven.xanderpeng.emissions.network.osm;

import java.util.HashSet;
import java.util.Map;

public class ProcessedOsmElements {



    static class ProcessedNode {
        long id;
        double lat;
        double lon;
    }

    static class ProcessedWay {
        long id;
        long fromNodeId;
        long toNodeId;
        HashSet<String> modes;
        Map<String, String> attributes;

        public ProcessedWay(long id, long fromNodeId, long toNodeId, HashSet<String> modes, Map<String, String> attributes) {
            this.id = id;
            this.fromNodeId = fromNodeId;
            this.toNodeId = toNodeId;
            this.modes = modes;
            this.attributes = attributes;
        }
    }

    static class ProcessedRelation {
        long id;
        long[] members;
    }


}
