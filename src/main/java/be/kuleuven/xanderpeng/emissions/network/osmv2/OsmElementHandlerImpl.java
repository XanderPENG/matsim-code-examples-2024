package be.kuleuven.xanderpeng.emissions.network.osmv2;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

import java.util.Map;
import java.util.Set;

public class OsmElementHandlerImpl implements OsmElementHandler {


    @Override
    public void handleNode(OsmNode node, Map<Long, OsmNode> nodes) {
        nodes.put(node.getId(), node);
    }

    @Override
    public void handleWay(OsmWay way, Map<Long, OsmWay> ways, Map<Long, Set<String>> wayModes) {
        if (ways.isEmpty()) {
            ways.put(way.getId(), way);
            wayModes.put(way.getId(), Set.of("car"));
        } else {
            ;
        }


    }

    @Override
    public void handleRelation(OsmRelation relation, Map<Long, OsmRelation> relations) {

    }

    @Override
    public void complete() {

    }



}
