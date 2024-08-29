package be.kuleuven.xanderpeng.emissions.networkV2.config;

import org.matsim.core.api.internal.MatsimParameters;
import org.matsim.core.config.ReflectiveConfigGroup;
/**
 * This class is used to configure the Tags/Keys of link-related parameters (e.g., freespeed, lanes, capacity, etc.) in the KeyValuePais.
 */
public class LinkAttrParamSet extends ReflectiveConfigGroup implements MatsimParameters {

    public static final String GROUP_NAME = "linkAttrParamSet";

    @Parameter
    public String MAX_SPEED_FIELD;

    @Parameter
    public String CAPACITY_FIELD;

    @Parameter
    public String LANES_FIELD;

    @Parameter
    public String WIDTH_FIELD;

    @Parameter
    public String LENGTH_FIELD;


    public LinkAttrParamSet() {
        super(GROUP_NAME);
    }

    public LinkAttrParamSet(String maxSpeedField, String capacityField, String lanesField, String widthField, String lengthField) {
        super(GROUP_NAME);
        this.MAX_SPEED_FIELD = maxSpeedField;
        this.CAPACITY_FIELD = capacityField;
        this.LANES_FIELD = lanesField;
        this.WIDTH_FIELD = widthField;
        this.LENGTH_FIELD = lengthField;
    }
}

