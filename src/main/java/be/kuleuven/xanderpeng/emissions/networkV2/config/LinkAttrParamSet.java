package be.kuleuven.xanderpeng.emissions.networkV2.config;

import org.matsim.core.api.internal.MatsimParameters;
import org.matsim.core.config.ReflectiveConfigGroup;

import java.util.Map;
import java.util.Set;

/**
 * This class is used to configure the Tags/Keys of link-related parameters (e.g., freespeed, lanes, capacity, etc.) in the KeyValuePais.
 * TODO: 1. Consider the unit of the input parameters
 *       2. Optimize the default values of the parameters, i.e., the capacity and the width of the link should not be set as the default values directly if absent.
 *          Instead, they should be calculated based on the lanes, the length, and the width of the link.
 *              - for capacity: capacity = f(lanes, length, width)
 *              - for width: width = f(lanes, default width)
 */
public class LinkAttrParamSet extends ReflectiveConfigGroup implements MatsimParameters {
    @Comment
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

//    @Parameter
//    public Map<String, String> INPUT_PARAM_UNIT;

//    @Parameter
//    @Comment("The reserved fields in the link attributes, the value of these fields will be reserved as link attributes in the output network.")
//    public Set<String> RESERVED_LINK_FIELDS;

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

