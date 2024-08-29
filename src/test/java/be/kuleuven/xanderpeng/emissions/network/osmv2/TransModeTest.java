package be.kuleuven.xanderpeng.emissions.network.osmv2;

import be.kuleuven.xanderpeng.emissions.networkV2.core.NetworkElement;
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
//                .addKeyValueMapping(Map.of("highway", "motorway"))
//                .addKeyValueMapping(Map.of("highway", "trunk"))
                .addKeyValueMapping(Map.of("highway", "primary", "bicycle", "yes"))
                .addKeyValueMapping(Map.of("highway", "livingstreet", "bicycle", "yes", "cycleway", "*"))
//                .addKeyValueMapping(Map.of("bicycle", "yes"))
                .build();

        LOG.info("carMode pre-defined key-values: {}",carMapping.getKeyValueMapping());

        TransMode carMode = new TransMode(TransMode.Mode.CAR, carMapping, 130 / 3.6, 0.242, 1800, 3.5, 2);

        NetworkElement.Link link = new NetworkElement.Link("1",
                new NetworkElement.Node("1", 0, 0), new NetworkElement.Node("2", 0, 1));
        Map<String, String> testLinkKeyValuePairs = new HashMap<>();
        /* As "bicycle: yes" is solely defined in the mapping, it will return true only when the key-value pair contains "bicycle: yes";
           Hence, it should be careful when defining single KEY_VALUE_PAIR in the mapping
        */
        testLinkKeyValuePairs.put("highway", "livingstreet");
        testLinkKeyValuePairs.put("bicycle", "yes");
        testLinkKeyValuePairs.put("cycleway", "no");
        LOG.info("Test link key-value pairs: {}", testLinkKeyValuePairs);

        link.setKeyValuePairs(testLinkKeyValuePairs);

        Assertions.assertTrue(carMode.matchLinkTransMode(link), "Should be true");
    }


}
