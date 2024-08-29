package be.kuleuven.xanderpeng.emissions.networkV2.core;

import org.matsim.api.core.v01.TransportMode;

import java.util.Map;
import java.util.Set;

public final class TransMode {
    private final Mode mode;
    private final ModeKeyValueMapping keyValueMapping;

    private final double defaultMaxSpeed;
    private final double defaultEmissionFactor;
//    private final double defaultLaneCapacity;
    private final double defaultLaneWidth;
    private final double defaultLanes;


    public TransMode(Mode mode, ModeKeyValueMapping keyValueMapping,
                     double defaultMaxSpeed, double defaultEmissionFactor,
                     double defaultLaneWidth, double defaultLanes) {
        this.mode = mode;
        this.keyValueMapping = keyValueMapping;
        this.defaultMaxSpeed = defaultMaxSpeed;
        this.defaultEmissionFactor = defaultEmissionFactor;
//        this.defaultLaneCapacity = defaultLaneCapacity;
        this.defaultLaneWidth = defaultLaneWidth;
        this.defaultLanes = defaultLanes;
    }


    public Mode getMode() {
        return mode;
    }

    public ModeKeyValueMapping getModeKeyValueMapping() {
        return keyValueMapping;
    }

    public double getDefaultMaxSpeed() {
        return defaultMaxSpeed;
    }

    public double getDefaultEmissionFactor() {
        return defaultEmissionFactor;
    }

//    public double getDefaultLaneCapacity() {
//        return defaultLaneCapacity;
//    }

    public double getDefaultLaneWidth() {
        return defaultLaneWidth;
    }

    public double getDefaultLanes() {
        return defaultLanes;
    }

    public boolean matchTransMode(Map<String, String> keyValuePairs) {
        boolean match = false;
        for (Map<String, String> mapping : this.keyValueMapping.getKeyValueMapping()) {
            Set<String> predefinedKeys = mapping.keySet();  // keys that are predefined in the mapping
            if (keyValuePairs.keySet().containsAll(predefinedKeys)) {  // if the keyValuePairs contain all the predefined keys
                // check if the values of the predefined keys match those in the keyValuePairs
                for (String key : predefinedKeys) {
                    if (!keyValuePairs.get(key).equals(mapping.get(key))) {  // if any key-value pair is not equal to the predefined mapping, return false
                        match = false;
                        break;  // break the inner for-loop, and continue to the next mapping
                    } else {
                        match = true;
                    }
                }
            } else {
                continue;
            }
            // once all the key-value pairs are matched with one of the mappings, break the loop
            if (match) {
                break;
            }
        }
        return match;  // if there is no any matched mapping, return false
    }

    public boolean matchLinkTransMode(NetworkElement.Link link) {
        Map<String, String> keyValuePairs = link.getKeyValuePairs();
        final boolean[] match = {false};
        // outer loop for each mapping
        for (Map<String, String> mapping : this.keyValueMapping.getKeyValueMapping()){
            // inner loop for each key-value pair in the mapping
            for (Map.Entry<String, String> entry : mapping.entrySet()) {
                String key = entry.getKey().trim();
                String value = entry.getValue().trim();
                // if both key and value are "*"
                if (key.equals("*") && value.equals("*")) {
                    match[0] = true;
                    break;
                } else if (key.equals("*")) {
                    if (keyValuePairs.containsValue(value)) {
                        match[0] = true;
                    } else {
                        match[0] = false;
                        break;
                    }
                } else if (value.equals("*")) {
                    if (keyValuePairs.containsKey(key)) {
                        match[0] = true;
                    } else {
                        match[0] = false;
                        break;
                    }
                } else {
                    if (keyValuePairs.containsKey(key) && keyValuePairs.get(key).equals(value)) {
                        match[0] = true;
                    } else {
                        match[0] = false;
                        break;
                    }
                }
            }
            // if the link keyValuePairs match successfully with the mapping (any one of the keyValueMapping), break the loop
            if (match[0]) {
                break;
            }
        }
        return match[0];
    }

    public enum Mode {

        CAR(TransportMode.car),
        PT(TransportMode.pt),
        TRAIN(TransportMode.train),
        BIKE(TransportMode.bike),
        WALK(TransportMode.walk),
        SHIP(TransportMode.ship),
        OTHER("other");

        public final String name;


        Mode(String name) {
            this.name = name;
        }
    }

}
