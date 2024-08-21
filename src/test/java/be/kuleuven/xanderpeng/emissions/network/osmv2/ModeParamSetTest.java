package be.kuleuven.xanderpeng.emissions.network.osmv2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import be.kuleuven.xanderpeng.emissions.networkV2.config.ModeParamSet;

import java.util.Map;

public class ModeParamSetTest {

    private final static Logger LOG = LogManager.getLogger(ModeParamSet.class);

    @Test
    public void testParamSet(){
        LOG.info("Testing ModeParamSet");

        ModeParamSet modeParamSet = new ModeParamSet();
        modeParamSet.setKeyValMappingString("{key2:value,key3:value}; {key1:value};");
        Map<String, String> trueMap = Map.of("key2", "value", "key3", "value");

        Assertions.assertEquals(trueMap, modeParamSet.KEY_VALUE_MAPPING.iterator().next());

        LOG.info("test passed");

    }

}
