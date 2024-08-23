package be.kuleuven.xanderpeng.emissions.network.osmv2;

import be.kuleuven.xanderpeng.emissions.networkV2.config.ModeParamSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import be.kuleuven.xanderpeng.emissions.networkV2.tools.utils;
import be.kuleuven.xanderpeng.emissions.networkV2.config.NetworkConverterConfigGroup;
import org.matsim.core.config.ConfigGroup;

import java.util.Map;

public class NetworkConverterConfigTest {
    private final static Logger LOG = LogManager.getLogger(NetworkConverterConfigTest.class);

    @Test
    public void testNetworkConverterConfig(){
        LOG.info("Testing NetworkConverterConfig");

        NetworkConverterConfigGroup networkConverterConfigGroup = NetworkConverterConfigGroup.createDefaultConfig();
        networkConverterConfigGroup.INPUT_NETWORK_FILE = utils.aldiNetworkInput;

        LOG.info("before: {}", networkConverterConfigGroup.getModeParamSets());
        networkConverterConfigGroup.readParameterSets();
        LOG.info("after: {}", networkConverterConfigGroup.getModeParamSets());

//        networkConverterConfigGroup.writeConfigFile(utils.networkOutputDir + "multimodalNetworkConverterConfig.xml");

        LOG.info("test passed");
    }

    @Test
    public void testLoadConfig(){
        LOG.info("Testing loading NetworkConverterConfig");

        NetworkConverterConfigGroup networkConverterConfigGroup = NetworkConverterConfigGroup.loadConfigFile(utils.networkOutputDir + "multimodalNetworkConverterConfig.xml");
        LOG.info(networkConverterConfigGroup.getModeParamSets());

        LOG.info("test passed");

    }

}
