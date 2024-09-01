package be.kuleuven.xanderpeng.emissions.networkV2.run;

import be.kuleuven.xanderpeng.emissions.networkV2.config.NetworkConverterConfigGroup;
import be.kuleuven.xanderpeng.emissions.networkV2.core.NetworkConverter;
import be.kuleuven.xanderpeng.emissions.networkV2.tools.Utils;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.io.NetworkWriter;

class RunNetworkConvertion {
    public static String configUrl = Utils.networkOutputDir + "multimodalNetworkConverterConfig.xml";

    public static void main(String[] args) {
        NetworkConverterConfigGroup config = NetworkConverterConfigGroup.loadConfigFile(configUrl);

        NetworkConverter networkConverter = new NetworkConverter(config);
        Network network = networkConverter.convert();
        new NetworkWriter(network).write(Utils.networkOutputDir + "multimodalNetwork.xml");

    }
}
