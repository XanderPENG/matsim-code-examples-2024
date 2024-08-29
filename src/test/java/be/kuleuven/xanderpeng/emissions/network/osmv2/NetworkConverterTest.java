package be.kuleuven.xanderpeng.emissions.network.osmv2;

import be.kuleuven.xanderpeng.emissions.networkV2.tools.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

public class NetworkConverterTest {
    private final static Logger LOG = LogManager.getLogger(NetworkConverterTest.class);

    @Test
    public void testNetworkConverter(){
        LOG.info("Testing NetworkConverter");
        double dist = Utils.calculateHaversineDist(CoordUtils.createCoord(4.69235,50.87399), CoordUtils.createCoord(4.69152,50.87408));
        LOG.info("Distance: {}", dist);
    }
}
