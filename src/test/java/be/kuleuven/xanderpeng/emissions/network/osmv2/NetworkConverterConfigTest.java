package be.kuleuven.xanderpeng.emissions.network.osmv2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import be.kuleuven.xanderpeng.emissions.networkV2.tools.Utils;
import be.kuleuven.xanderpeng.emissions.networkV2.config.NetworkConverterConfigGroup;

public class NetworkConverterConfigTest {
    private final static Logger LOG = LogManager.getLogger(NetworkConverterConfigTest.class);

    @Test
    public void testNetworkConverterConfig(){
        LOG.info("Testing NetworkConverterConfig");

        NetworkConverterConfigGroup networkConverterConfigGroup = NetworkConverterConfigGroup.createDefaultConfig();
        networkConverterConfigGroup.INPUT_NETWORK_FILE = Utils.aldiNetworkInput;

        networkConverterConfigGroup.writeConfigFile(Utils.networkOutputDir + "multimodalNetworkConverterConfig.xml");

        LOG.info("test passed");
    }

    @Test
    public void testLoadConfig(){
        LOG.info("Testing loading NetworkConverterConfig");

        NetworkConverterConfigGroup networkConverterConfigGroup = NetworkConverterConfigGroup.loadConfigFile(Utils.networkOutputDir + "multimodalNetworkConverterConfig.xml");
        LOG.info("modeParamSet: {}", networkConverterConfigGroup.getModeParamSets());

        LOG.info("test passed");

    }

}
