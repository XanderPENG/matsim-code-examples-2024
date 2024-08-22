package be.kuleuven.xanderpeng.emissions.network.osmv2;

import be.kuleuven.xanderpeng.emissions.networkV2.config.ModeParamSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import be.kuleuven.xanderpeng.emissions.networkV2.readers.OsmReader;
import be.kuleuven.xanderpeng.emissions.networkV2.tools.utils;

import java.io.IOException;

public class OsmReaderTest {
    private final static Logger LOG = LogManager.getLogger(ModeParamSet.class);
    @Test
    public void testRead() throws IOException {
        LOG.info("Testing OsmReader");
        OsmReader reader = new OsmReader();
        reader.read(utils.aldiNetworkInput);
        Assertions.assertFalse(reader.getRawNodes().isEmpty());

        reader.getRawNodes().forEach((k, v) -> {
            LOG.info("Node: {} : {}", k, v.getId());
        });
        LOG.info("test passed");
    }
}
