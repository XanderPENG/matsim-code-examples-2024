package be.kuleuven.xanderpeng.emissions.networkV2.config;

import org.matsim.core.api.internal.MatsimParameters;
import org.matsim.core.config.ReflectiveConfigGroup;
import be.kuleuven.xanderpeng.emissions.networkV2.core.TransMode;

import java.util.Set;
import java.util.Map;

public class ModeParamSet extends ReflectiveConfigGroup implements MatsimParameters {

    public static final String GROUP_NAME = "modeParamSet";

    @Parameter
    @Comment("The name of the mode, which should be one of the following: \n" +
             "\t\t[car, pt, train, bike, walk, ship, other.]. see @TransMode.Mode for more details.")
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

    @Parameter
    @Comment("The key-value mapping for the specific mode")
    public Set<Map<String, String>> KEY_VALUE_MAPPING;


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
