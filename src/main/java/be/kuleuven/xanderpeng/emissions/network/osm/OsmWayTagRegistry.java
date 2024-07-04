package be.kuleuven.xanderpeng.emissions.network.osm;

import java.util.List;
import java.util.Map;

/** This class is used to store the useful keys and values of a specific transport mode
 *
 */
public class OsmWayTagRegistry {

    private final Map<String, List<String>> keysAndValues;

    private OsmWayTagRegistry(Builder builder) {
        this.keysAndValues = builder.keysAndValues;
    }

    //  A  Builder class that has a method called build() that returns an instance of OsmWayKeysAndValues
    public static class Builder {
        private Map<String, List<String>> keysAndValues;

        public Builder keysAndValues(Map<String, List<String>> keysAndValues) {
            this.keysAndValues = keysAndValues;
            return this;
        }

        public OsmWayTagRegistry build() {
            return new OsmWayTagRegistry(this);
        }
    }

}
