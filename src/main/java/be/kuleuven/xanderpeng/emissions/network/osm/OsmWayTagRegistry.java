package be.kuleuven.xanderpeng.emissions.network.osm;

import java.util.ArrayList;
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

        public Builder(){

        }

        public Builder setKeysAndValues(Map<String, List<String>> keysAndValues){
            this.keysAndValues = keysAndValues;
            return this;
        }

        // A method for adding specific Key-Value Pair into the Map
        public Builder addKeyValuePair(Map<String, String> keyValuePairs){
            for (Map.Entry<String, String> entry: keyValuePairs.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();

                if (this.keysAndValues.containsKey(key)){
                    if (this.keysAndValues.get(key).contains(value))
                        continue;
                    else
                        this.keysAndValues.get(key).add(value);
                }

            }
            return null;
        }


//        public Builder keysAndValues(Map<String, List<String>> keysAndValues) {
//            this.keysAndValues = keysAndValues;
//            return this;
//        }

        public OsmWayTagRegistry build() {
            return new OsmWayTagRegistry(this);
        }
    }

    // An inner class to define a new data structure for one key and several values
    private static class keyAndValues{
        private final String key;
        private final List<String> values;

        // A default constructor for data structure KeyAndValues
        private keyAndValues(String key, List<String> values) {
            this.key = key;
            this.values = values;
        }

        private void addValues(String v){
            this.values.add(v);
        }

        private void addValues(ArrayList<String> v){
            this.values.addAll(v);
        }

        private List<String> getValues(){
            return this.values;
        }

        private String getKey(){
            return this.key;
        }


    }


}
