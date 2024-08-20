package be.kuleuven.xanderpeng.emissions.network.osmv2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import be.kuleuven.xanderpeng.emissions.networkV2.config.NetworkConverterConfigGroup;

public class NetworkConverterConfigTest {
    private final static Logger LOG = LogManager.getLogger(NetworkConverterConfigTest.class);

    @Test
    public void testNetworkConverterConfig(){
        LOG.info("Testing NetworkConverterConfig");

        NetworkConverterConfigGroup networkConverterConfigGroup = new NetworkConverterConfigGroup();
        networkConverterConfigGroup.setInputNetworkFile("src/main/resources/be.kuleuven.xanderpeng.emissions/network/input/mapSample.pbf");
        networkConverterConfigGroup.setFILE_TYPE("fileType");
        networkConverterConfigGroup.writeConfigFile("src/main/resources/be.kuleuven.xanderpeng.emissions/network/output/");

        LOG.info("test passed");
    }
}
