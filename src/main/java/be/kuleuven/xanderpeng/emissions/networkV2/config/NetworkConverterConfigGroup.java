package be.kuleuven.xanderpeng.emissions.networkV2.config;

import be.kuleuven.xanderpeng.emissions.networkV2.tools.TransModeImpl;
import org.matsim.core.api.internal.MatsimParameters;
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
    private ConnectedNetworkParamSet connectedNetworkParamSet;

    // Global parameters
    @Parameter
    @Comment("Indicate the input network file type, which should be one of the following: osm, shp, geojson.")
    public String FILE_TYPE;

    @Parameter
    public String INPUT_CRS;

    @Parameter
    public String INPUT_NETWORK_FILE;

    @Parameter
    @Comment("If ture, the link will be split into multiple links if it is connected to multiple nodes, so as to keep the real shape of the link.")
    public boolean KEEP_DETAILED_LINK;

    @Parameter
    @Comment("If true, the link will be kept although it is not aligned with any pre-defined @TransMode.")
    public boolean KEEP_UNDEFINED_LINK;

    @Parameter
    public String OUTPUT_NETWORK_FILE;

    @Comment("Fill in the file path if you want to output the network in the shp/geojson format.")
    @Parameter
    public String OUTPUT_SHP_FILE;
    @Parameter
    public String OUTPUT_GEOJSON_FILE;

    @Parameter
    @Comment("If true, the network will be processed to be strongly connected, which means that each node/link can be reached from any other node/link.")
    public boolean CONNECTED_NETWORK;

    @Parameter
    @Comment(""" 
             If true, the network will be processed to be one-way based on the `ONEWAY_KEY_VALUE_PAIR`,
             \t\t\t which means that the traffic can only flow in specified direction. Otherwise, the traffic can flow in both directions of the whole network.
             """)
    public boolean ONEWAY;
    @Comment("The key-value mapping for the specific mode; the format should be like a map (e.g., 'oneway:yes')")
    public Map<String, String> ONEWAY_KEY_VALUE_PAIR;


    public NetworkConverterConfigGroup() {
        super(GROUP_NAME);
    }

    public NetworkConverterConfigGroup(String filetype, String inputCRS, String inputNetworkFile,
                                       boolean keepDetailedLink, boolean keepUndefinedLink, String outputNetworkFile,
                                       String outputShpFile, String outputGeojsonFile, boolean connectedNetwork, boolean oneway,
                                       Map<String, String> onewayKeyValuePair) {
        super(GROUP_NAME);
        this.FILE_TYPE = filetype;
        this.INPUT_CRS = inputCRS;
        this.INPUT_NETWORK_FILE = inputNetworkFile;
        this.KEEP_DETAILED_LINK = keepDetailedLink;
        this.KEEP_UNDEFINED_LINK = keepUndefinedLink;
        this.OUTPUT_NETWORK_FILE = outputNetworkFile;
        this.OUTPUT_SHP_FILE = outputShpFile;
        this.OUTPUT_GEOJSON_FILE = outputGeojsonFile;
        this.CONNECTED_NETWORK = connectedNetwork;
        this.ONEWAY = oneway;
        this.ONEWAY_KEY_VALUE_PAIR = onewayKeyValuePair;
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
        config.CONNECTED_NETWORK = true;
        config.ONEWAY_KEY_VALUE_PAIR.put("oneway", "yes");

        // Add a default mode parameter set
        config.addParameterSet(new ModeParamSet(TransModeImpl.CAR));

        config.addParameterSet(new ModeParamSet(TransModeImpl.BIKE));

        config.addParameterSet(new ModeParamSet(TransModeImpl.PT));

        config.addParameterSet(new ModeParamSet(TransModeImpl.WALK));

        config.addParameterSet(new ModeParamSet(TransModeImpl.OTHER));

        config.addParameterSet(new ConnectedNetworkParamSet(true,
                Set.of(TransMode.Mode.CAR, TransMode.Mode.PT, TransMode.Mode.BIKE, TransMode.Mode.WALK, TransMode.Mode.OTHER), "reduce"));

        return config;
    }


    // Recognize the parameter set type when loading the config file
    @Override
    public ConfigGroup createParameterSet(String type) {
        if (type.equals(ModeParamSet.GROUP_NAME)) {
            return new ModeParamSet();
        } else if(type.equals(ConnectedNetworkParamSet.GROUP_NAME)){
            return new ConnectedNetworkParamSet();
        }
        else {
            throw new IllegalArgumentException("Unsupported parameter set type: " + type);
        }
    }

    // Load config file; TODO: Switch to V2 reader
    public static NetworkConverterConfigGroup loadConfigFile(String filename){
        Config config = ConfigUtils.loadConfig(filename, new NetworkConverterConfigGroup());
        NetworkConverterConfigGroup configGroup = ConfigUtils.addOrGetModule(config, NetworkConverterConfigGroup.GROUP_NAME, NetworkConverterConfigGroup.class);
        configGroup.readParameterSets(ModeParamSet.GROUP_NAME);
        configGroup.readParameterSets(ConnectedNetworkParamSet.GROUP_NAME);
        return configGroup;
    }

    // Add a parameter set
    public void readParameterSets(String setName) {
        switch (setName) {
            case ModeParamSet.GROUP_NAME -> readModeParamSets();
            case ConnectedNetworkParamSet.GROUP_NAME -> readConnectedNetworkParamSets();
        }
    }

    public void readModeParamSets() {
        this.getParameterSets(ModeParamSet.GROUP_NAME).forEach(group -> {
            ModeParamSet modeParamSet = (ModeParamSet) group;
            modeParamSets.put(modeParamSet.MODE_NAME, modeParamSet);
        });

    }

    public void readConnectedNetworkParamSets() {
        this.getParameterSets(ConnectedNetworkParamSet.GROUP_NAME).forEach(group -> {
            this.connectedNetworkParamSet = (ConnectedNetworkParamSet) group;
        });
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
        defaultConfig.addModule(NetworkConverterConfigGroup.this);
        ConfigUtils.writeConfig(defaultConfig, filename);
    }

    @StringGetter("ONEWAY_KEY_VALUE_PAIR")
    public String getOnewayKeyValuePair() {
        StringBuilder sb = new StringBuilder();
        ONEWAY_KEY_VALUE_PAIR.forEach((key, value) -> sb.append(key).append(":").append(value));
        return sb.toString().trim();
    }

    @StringSetter("ONEWAY_KEY_VALUE_PAIR")
    public void setOnewayKeyValuePair(String onewayKeyValuePairs) {
        String[] keyValuePair= onewayKeyValuePairs.split(":");

        if (keyValuePair.length == 2) {
            ONEWAY_KEY_VALUE_PAIR.put(keyValuePair[0].trim(), keyValuePair[1].trim());
        }
    }

}


