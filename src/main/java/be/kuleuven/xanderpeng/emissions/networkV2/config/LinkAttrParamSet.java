package be.kuleuven.xanderpeng.emissions.networkV2.config;

import org.matsim.core.api.internal.MatsimParameters;
import org.matsim.core.config.ReflectiveConfigGroup;
/**
 * This class is used to configure the Tags/Keys of link-related parameters (e.g., freespeed, lanes, capacity, etc.) in the KeyValuePais.
 */
public class LinkAttrParamSet extends ReflectiveConfigGroup implements MatsimParameters {

    public static final String GROUP_NAME = "linkAttrParamSet";

    @Parameter
    public String MAX_SPEED;

    @Parameter
    public String CAPACITY;

    @Parameter
    public String LANES;

    @Parameter
    public String WIDTH;

    @Parameter



    public LinkAttrParamSet() {
        super(GROUP_NAME);
    }
}
