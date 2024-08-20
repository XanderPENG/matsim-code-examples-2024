package be.kuleuven.xanderpeng.emissions.networkV2.config;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ReflectiveConfigGroup;

import java.util.Set;

public class NetworkConverterConfigGroup extends ReflectiveConfigGroup {

    public static final String GROUP_NAME = "multimodalNetworkConverter";

    // Global parameters
    @Parameter
    public String FILE_TYPE = "osm";


    private String inputNetworkFile;

    public NetworkConverterConfigGroup() {
        super(GROUP_NAME);
    }

    //write getter and setter for inputNetworkFile
    @StringGetter("inputNetworkFile")
    public String getInputNetworkFile() {
        return inputNetworkFile;
    }

    @StringSetter("inputNetworkFile")
    public void setInputNetworkFile(String inputNetworkFile) {
        this.inputNetworkFile = inputNetworkFile;
    }


    // Write config.xml file
    public void writeConfigFile(String path) {
        Config defaultConfig = ConfigUtils.createConfig();
        // delete all default modules
        Set<String> defaultModules = Set.copyOf(defaultConfig.getModules().keySet());
        for (String module : defaultModules) {
            defaultConfig.removeModule(module);
        }
        // Add the current module into the config
        defaultConfig.addModule(this);
        ConfigUtils.writeConfig(defaultConfig, path+"multimodalNetworkConverterConfig.xml");
    }



}
