package be.kuleuven.xanderpeng.emissions.network.osm;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

import java.util.HashSet;
import java.util.Map;

public interface OsmElementHandler {
    void handleNode(OsmNode node);

    void handleWay(OsmWay way);

    void handleRelation(OsmRelation relation);

    void complete();

    Map<Long, OsmNode> getNodes();

    Map<Long, OsmWay> getWays();

    Map<Long, HashSet<String>> getWayModes();
}
