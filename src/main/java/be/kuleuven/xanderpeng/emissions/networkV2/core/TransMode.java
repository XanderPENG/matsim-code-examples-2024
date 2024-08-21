package be.kuleuven.xanderpeng.emissions.networkV2.core;

import org.matsim.api.core.v01.TransportMode;

import java.util.Map;
import java.util.Set;

public class TransMode {
    private final Mode mode;
    private final ModeKeyValueMapping keyValueMapping;

    public TransMode(Mode mode, ModeKeyValueMapping keyValueMapping) {
        this.mode = mode;
        this.keyValueMapping = keyValueMapping;
    }

    public Mode getMode() {
        return mode;
    }

    public ModeKeyValueMapping getModeKeyValueMapping() {
        return keyValueMapping;
    }

    public boolean matchLinkMode(Map<String, String> keyValuePairs) {
        boolean match = false;
        for (Map<String, String> mapping : keyValueMapping.getKeyValueMapping()) {
            Set<String> predefinedKeys = mapping.keySet();  // keys that are predefined in the mapping
            if (keyValuePairs.keySet().containsAll(predefinedKeys)) {  // if the keyValuePairs contain all the predefined keys
                // check if the values of the predefined keys match those in the keyValuePairs
                for (String key : predefinedKeys) {
                    if (!keyValuePairs.get(key).equals(mapping.get(key))) {  // if any key-value pair is not equal to the predefined mapping, return false
                        match = false;
                        break;
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

    public enum Mode {
        CAR(TransportMode.car, 130 / 3.6, 0.242, 1800, 3.5, 2),
        PT(TransportMode.pt, 40 / 3.6, 0.142, 1200, 3.5, 1),
        TRAIN(TransportMode.train, 100 / 3.6, 0.542, 100, 5, 1),
        BIKE(TransportMode.bike, 20 / 3.6, 0, 2000, 2, 1),
        WALK(TransportMode.walk, 5 / 3.6, 0, 5000, 1, 1),
        SHIP(TransportMode.ship, 20 / 3.6, 0.142, 1000, 10, 1),
        OTHER("other", 20 / 3.6, 0.142, 1000, 3.5, 1);

        public final String name;
        public final double defaultMaxSpeed;
        public final double defaultEmissionFactor;
        public final double defaultLaneCapacity;
        public final double defaultLaneWidth;
        public final double defaultLanes;


        Mode(String name, double defaultMaxSpeed, double defaultEmissionFactor,
             double defaultLaneCapacity, double defaultLaneWidth, double defaultLanes) {
            this.name = name;
            this.defaultMaxSpeed = defaultMaxSpeed;
            this.defaultEmissionFactor = defaultEmissionFactor;
            this.defaultLaneCapacity = defaultLaneCapacity;
            this.defaultLaneWidth = defaultLaneWidth;
            this.defaultLanes = defaultLanes;
        }
    }

}
