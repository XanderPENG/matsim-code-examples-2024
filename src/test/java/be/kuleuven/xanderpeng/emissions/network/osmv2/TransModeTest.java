package be.kuleuven.xanderpeng.emissions.network.osmv2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import be.kuleuven.xanderpeng.emissions.networkV2.core.ModeKeyValueMapping;
import be.kuleuven.xanderpeng.emissions.networkV2.core.TransMode;
import org.matsim.api.core.v01.Id;

import java.util.HashMap;
import java.util.Map;

public class TransModeTest {
    private final static Logger LOG = LogManager.getLogger(TransModeTest.class);

    @Test
    public void testMode(){
        LOG.info("Testing TransMode");

        TransMode.Mode car = TransMode.Mode.CAR;
//        Assertions.assertEquals("car", car.toString(), "Wrong String");
        Assertions.assertEquals("car", car.name, "Wrong");

        LOG.info("Node test passed");
    }

    @Test
    public void testMatchTransMode(){
        LOG.info("Testing matchTransMode");

        ModeKeyValueMapping carMapping = new ModeKeyValueMapping.Builder()
                .setMode(TransMode.Mode.CAR)
                .addKeyValueMapping(Map.of("highway", "motorway"))
                .addKeyValueMapping(Map.of("highway", "trunk"))
                .addKeyValueMapping(Map.of("highway", "cycleway", "bicycle", "yes"))
                .addKeyValueMapping(Map.of("highway", "livingstreet", "bicycle", "yes", "cycleway", "lane"))
                .addKeyValueMapping(Map.of("bicycle", "yes"))
                .build();
        System.out.println(carMapping.getKeyValueMapping());

        TransMode carMode = new TransMode(TransMode.Mode.CAR, carMapping);

        Map<String, String> testLinkKeyValuePairs = new HashMap<>();
        /* As "bicycle: yes" is solely defined in the mapping, it will return true only when the key-value pair contains "bicycle: yes";
           Hence, it should be careful when defining single KEY_VALUE_PAIR in the mapping
        */
        testLinkKeyValuePairs.put("highway", "primary");
        testLinkKeyValuePairs.put("bicycle", "yes");
        testLinkKeyValuePairs.put("cycleway", "no");
        System.out.println(testLinkKeyValuePairs);

        Assertions.assertTrue(carMode.matchTransMode(testLinkKeyValuePairs), "Should be true");
    }

}
