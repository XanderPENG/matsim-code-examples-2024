package be.kuleuven.xanderpeng.emissions.networkV2.config;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ReflectiveConfigGroup;

import be.kuleuven.xanderpeng.emissions.networkV2.core.TransMode;
import be.kuleuven.xanderpeng.emissions.networkV2.core.ModeKeyValueMapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NetworkConverterConfigGroup extends ReflectiveConfigGroup {

    public static final String GROUP_NAME = "multimodalNetworkConverter";
    private final Map<String, ModeParamSet> modeParamSets = new HashMap<>();

    // Global parameters
    @Parameter
    @Comment("Indicate the input network file type, which should be one of the following: osm, shp, geojson.")
    public String FILE_TYPE = "osm";

    @Parameter
    public String INPUT_CRS;

    @Parameter
    public String INPUT_NETWORK_FILE;

    @Parameter
    @Comment("If ture, the link will be split into multiple links if it is connected to multiple nodes, so as to keep the real shape of the link.")
    public boolean KEEP_DETAILED_LINK = true;

    @Parameter
    @Comment("If true, the link will be kept although it is not aligned with any pre-defined @TransMode.")
    public boolean KEEP_UNDEFINED_LINK = true;

    @Parameter
    public String OUTPUT_NETWORK_FILE;

    @Comment("Fill in the file path if you want to output the network in the shp/geojson format.")
    @Parameter
    public String OUTPUT_SHP_FILE = "NA";
    @Parameter
    public String OUTPUT_GEOJSON_FILE = "NA";

    @Parameter
    @Comment("If true, the network will be routable, which means that the network will be used for routing.")
    public boolean ROUTABLE_NETWORK = true;



    public NetworkConverterConfigGroup() {
        super(GROUP_NAME);
    }

    public Map<String, ModeParamSet> getModeParamSets() {
        return modeParamSets;
    }

    // create a default NetworkConverterConfigGroup
    public static NetworkConverterConfigGroup createDefaultConfig() {
        NetworkConverterConfigGroup config = new NetworkConverterConfigGroup();
        config.FILE_TYPE = "osm";
        config.INPUT_CRS = "EPSG:4326";
        config.INPUT_NETWORK_FILE = "yours/input/network/file";
        config.KEEP_DETAILED_LINK = true;
        config.KEEP_UNDEFINED_LINK = true;
        config.OUTPUT_NETWORK_FILE = "yours/output/network/file";
        config.OUTPUT_SHP_FILE = "NA";
        config.OUTPUT_GEOJSON_FILE = "NA";
        config.ROUTABLE_NETWORK = true;

        // Add a default mode parameter set
        config.addParameterSet(new ModeParamSet(new TransMode(TransMode.Mode.CAR, new ModeKeyValueMapping.Builder()
                .setMode(TransMode.Mode.CAR)
                .addKeyValueMapping(Map.of("key1", "value"))
                .addKeyValueMapping(Map.of("key2", "value", "key3", "value"))
                .build())));

        config.addParameterSet(new ModeParamSet(new TransMode(TransMode.Mode.BIKE, new ModeKeyValueMapping.Builder()
                .setMode(TransMode.Mode.BIKE)
                .addKeyValueMapping(Map.of("key1", "value"))
                .addKeyValueMapping(Map.of("key2", "value", "key3", "value"))
                .build())));
        return config;
    }

    // Load config file
    public static NetworkConverterConfigGroup loadConfigFile(String filename){
        Config config = ConfigUtils.loadConfig(filename);
        NetworkConverterConfigGroup configGroup = ConfigUtils.addOrGetModule(config, NetworkConverterConfigGroup.GROUP_NAME, NetworkConverterConfigGroup.class);
        configGroup.readParameterSets();
        return configGroup;
    }

    public void readParameterSets(){
        Collection<? extends ConfigGroup> groups = this.getParameterSets(ModeParamSet.GROUP_NAME);
        if (groups == null) {
            return;
        }

        for (ConfigGroup group : groups) {
            if (group instanceof ModeParamSet modeParamSet) {
                modeParamSets.put(modeParamSet.MODE_NAME, modeParamSet);
            } else {
                // try to cast the group to ModeParamSet
                ModeParamSet modeParamSet = new ModeParamSet();
                Map<String, String> groupParams = group.getParams();
                modeParamSet.setModeName(groupParams.get("MODE_NAME"));
                modeParamSet.setFreeSpeed(groupParams.get("FREE_SPEED"));
                modeParamSet.setEmissionFactor(groupParams.get("EMISSION_FACTOR"));
                modeParamSet.setLaneCapacity(groupParams.get("LANE_CAPACITY"));
                modeParamSet.setLaneWidth(groupParams.get("LANE_WIDTH"));
                modeParamSet.setLanes(groupParams.get("LANES"));
                modeParamSet.setKeyValMappingString(groupParams.get("KEY_VALUE_MAPPING"));
                modeParamSets.put(modeParamSet.MODE_NAME, modeParamSet);
            }
        }
    }


    // Write config.xml file
    public void writeConfigFile(String filename) {
        Config defaultConfig = ConfigUtils.createConfig();
        // delete all default modules
        Set<String> defaultModules = Set.copyOf(defaultConfig.getModules().keySet());
        for (String module : defaultModules) {
            defaultConfig.removeModule(module);
        }
        // Add the current module into the config
        defaultConfig.addModule(NetworkConverterConfigGroup.createDefaultConfig());
        ConfigUtils.writeConfig(defaultConfig, filename);
    }



}
