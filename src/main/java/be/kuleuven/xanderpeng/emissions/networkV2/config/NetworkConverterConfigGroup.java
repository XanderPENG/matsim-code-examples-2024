package be.kuleuven.xanderpeng.emissions.networkV2.config;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ReflectiveConfigGroup;

public class NetworkConverterConfigGroup extends ReflectiveConfigGroup {

    public static final String GROUP_NAME = "multimodalNetworkConverter";

    // Global parameters
    private String FILE_TYPE = "fileType";


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

    //write getter and setter for FILE_TYPE
    @StringGetter("FILE_TYPE")
    public String getFILE_TYPE() {
        return FILE_TYPE;
    }

    @StringSetter("FILE_TYPE")
    public void setFILE_TYPE(String FILE_TYPE) {
        this.FILE_TYPE = FILE_TYPE;
    }

    // Write config.xml file
    public void writeConfigFile(String path) {
        Config defaultConfig = ConfigUtils.createConfig();
        defaultConfig.addModule(this);
        ConfigUtils.writeConfig(defaultConfig, path+"multimodalNetworkConverterConfig.xml");
    }



}
