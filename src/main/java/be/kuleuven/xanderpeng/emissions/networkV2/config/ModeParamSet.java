package be.kuleuven.xanderpeng.emissions.networkV2.config;

import org.matsim.core.api.internal.MatsimParameters;
import org.matsim.core.config.ReflectiveConfigGroup;
import be.kuleuven.xanderpeng.emissions.networkV2.core.TransMode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class ModeParamSet extends ReflectiveConfigGroup implements MatsimParameters {

    public static final String GROUP_NAME = "modeParamSet";

    @Parameter
    @Comment("The name of the mode, which should be one of the following: \n" +
             "\t\t\t[car, pt, train, bike, walk, ship, other.]. see @TransMode.Mode for more details.")
    public String MODE_NAME;

    @Parameter
    public double FREE_SPEED;

    @Parameter
    public double EMISSION_FACTOR;

    @Parameter
    public double LANE_CAPACITY;

    @Parameter
    public double LANE_WIDTH;

    @Parameter
    public double LANES;


    @Comment("The key-value mapping for the specific mode")
    public Set<Map<String, String>> KEY_VALUE_MAPPING = new HashSet<>();

    // @StringGetter and @StringSetter for the KEY_VALUE_MAPPING
    @StringGetter("KEY_VALUE_MAPPING")
    public String getKeyValMappingString() {
        StringBuilder sb = new StringBuilder();
        for (Map<String, String> map : KEY_VALUE_MAPPING) {
            sb.append("{");

            map.forEach((key, value) -> sb.append(key).append(":").append(value).append(", "));
            if (!map.isEmpty()) {
                sb.deleteCharAt(sb.length() - 1); // delete the last comma
            }
            sb.append("}; ");
        }
        return sb.toString().trim(); // remove the last space
    }

    @StringSetter("KEY_VALUE_MAPPING")
    public void setKeyValMappingString(String keyValMappingString) {
        // Create a new HashSet to store the parsed key-value mappings
        Set<Map<String, String>> set = new HashSet<>();
        // Split the input string by semicolons to get individual map strings
        String[] mapStrings = keyValMappingString.split(";");

        for (String mapString : mapStrings) {
            // Check if the map string is not empty after trimming whitespace
            if (!mapString.trim().isEmpty()) {
                // Remove curly braces and trim whitespace from the map string
                mapString = mapString.trim().replace("{", "").replace("}", "");
                // Split the map string by commas to get individual key-value pairs
                String[] keyValuePairs = mapString.split(",");
                Map<String, String> map = new HashMap<>();

                for (String keyValue : keyValuePairs) {
                    // Split the key-value pair by colon to separate key and value
                    String[] keyValueArray = keyValue.split(":");
                    // Check if the key-value pair has exactly two elements (key and value)
                    if (keyValueArray.length == 2) {
                        map.put(keyValueArray[0].trim(), keyValueArray[1].trim());
                    }
                }
                set.add(map);
            }
        }
        this.KEY_VALUE_MAPPING = set;
    }


    public ModeParamSet() {
        super(GROUP_NAME);
    }

    // Constructor for the default ModeParamSet, using the default values of the mode
    public ModeParamSet(TransMode transMode) {
        super(GROUP_NAME);
        this.MODE_NAME = transMode.getMode().name;
        this.FREE_SPEED = transMode.getMode().defaultMaxSpeed;
        this.EMISSION_FACTOR = transMode.getMode().defaultEmissionFactor;
        this.LANE_CAPACITY = transMode.getMode().defaultLaneCapacity;
        this.LANE_WIDTH = transMode.getMode().defaultLaneWidth;
        this.LANES = transMode.getMode().defaultLanes;
        this.KEY_VALUE_MAPPING = transMode.getModeKeyValueMapping().getKeyValueMapping();
    }

    // Constructor for totally customized params
    public ModeParamSet(String modeName, double freeSpeed, double emissionFactor,
                        double laneCapacity, double laneWidth, double lanes,
                        Set<Map<String, String>> keyValueMapping) {
        super(GROUP_NAME);
        this.MODE_NAME = modeName;
        this.FREE_SPEED = freeSpeed;
        this.EMISSION_FACTOR = emissionFactor;
        this.LANE_CAPACITY = laneCapacity;
        this.LANE_WIDTH = laneWidth;
        this.LANES = lanes;
        this.KEY_VALUE_MAPPING = keyValueMapping;
    }

}
