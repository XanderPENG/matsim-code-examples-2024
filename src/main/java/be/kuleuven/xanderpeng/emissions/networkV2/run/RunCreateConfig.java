package be.kuleuven.xanderpeng.emissions.networkV2.run;

import be.kuleuven.xanderpeng.emissions.networkV2.config.NetworkConverterConfigGroup;
import be.kuleuven.xanderpeng.emissions.networkV2.tools.Utils;

public class RunCreateConfig {
    public static void main(String[] args) {
        NetworkConverterConfigGroup config = NetworkConverterConfigGroup.createDefaultConfig();
        config.setOnewayKeyValuePair("oneway: yes");
        config.writeConfigFile(Utils.networkOutputDir + "multimodalNetworkConverterConfig.xml");
    }
}
