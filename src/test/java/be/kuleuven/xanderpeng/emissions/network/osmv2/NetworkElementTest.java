package be.kuleuven.xanderpeng.emissions.network.osmv2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import be.kuleuven.xanderpeng.emissions.networkV2.core.NetworkElement;
import org.matsim.api.core.v01.Id;

public class NetworkElementTest {
    private final static Logger LOG = LogManager.getLogger(NetworkElementTest.class);

    @Test
    public void testNode(){
        LOG.info("Testing Node");

        NetworkElement.Node node = new NetworkElement.Node("189", 123.5, 22.98);
        Assertions.assertEquals("Node", node.getType(), "Wrong type");
        Assertions.assertEquals(Id.create("189", NetworkElement.Node.class), node.getId(), "Wrong id");

        LOG.info("Node test passed");
    }


}
