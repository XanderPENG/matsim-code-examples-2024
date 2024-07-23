package be.kuleuven.xanderpeng.emissions.network.osmv2;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

import java.util.Map;

public class OsmElementHandlerImpl implements OsmElementHandler {


    @Override
    public void handleNode(OsmNode node, Map<Long, OsmNode> nodes) {

    }

    @Override
    public void handleWay(OsmWay way, Map<Long, OsmWay> ways, Map<Long, String> wayModes) {

    }

    @Override
    public void handleRelation(OsmRelation relation, Map<Long, OsmRelation> relations) {

    }

    @Override
    public void complete() {

    }
}
