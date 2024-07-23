package be.kuleuven.xanderpeng.emissions.network.osmv2;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

import java.util.Map;

/**
 * This is an interface for customizing the handling OSM elements.
 */
public interface OsmElementHandler {
    void handleNode(OsmNode node, Map<Long, OsmNode> nodes);

    void handleWay(OsmWay way, Map<Long, OsmWay> ways, Map<Long, String> wayModes);

    void handleRelation(OsmRelation relation, Map<Long, OsmRelation> relations);

    void complete();
}
