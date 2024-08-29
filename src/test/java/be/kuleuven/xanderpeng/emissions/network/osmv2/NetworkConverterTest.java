package be.kuleuven.xanderpeng.emissions.network.osmv2;

import be.kuleuven.xanderpeng.emissions.networkV2.tools.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.*;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.NetworkWriter;

public class NetworkConverterTest {
    private final static Logger LOG = LogManager.getLogger(NetworkConverterTest.class);

    @Test
    public void testNetworkConverter(){
        LOG.info("Testing NetworkConverter");
        Network network = NetworkUtils.createNetwork();
        NetworkFactory networkFactory = network.getFactory();
        Node node1 = networkFactory.createNode(Id.createNodeId("1"), new Coord(0, 0));
        Node node2 = networkFactory.createNode(Id.createNodeId("2"), new Coord(1, 4));
        Link link = networkFactory.createLink(Id.createLinkId("1"), node1, node2);

        network.addNode(node1);
        network.addNode(node2);
        network.addLink(link);

        new NetworkWriter(network).write(Utils.networkOutputDir + "testNetwork.xml");

    }
}
