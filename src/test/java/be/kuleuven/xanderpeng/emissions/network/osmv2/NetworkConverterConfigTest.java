package be.kuleuven.xanderpeng.emissions.network.osmv2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import be.kuleuven.xanderpeng.emissions.networkV2.tools.utils;
import be.kuleuven.xanderpeng.emissions.networkV2.config.NetworkConverterConfigGroup;

public class NetworkConverterConfigTest {
    private final static Logger LOG = LogManager.getLogger(NetworkConverterConfigTest.class);

    @Test
    public void testNetworkConverterConfig(){
        LOG.info("Testing NetworkConverterConfig");

        NetworkConverterConfigGroup networkConverterConfigGroup = new NetworkConverterConfigGroup();
        networkConverterConfigGroup.INPUT_NETWORK_FILE = utils.aldiNetworkInput;

        networkConverterConfigGroup.writeConfigFile(utils.networkOutputDir + "multimodalNetworkConverterConfig.xml");

        LOG.info("test passed");
    }
}
