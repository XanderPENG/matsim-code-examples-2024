package be.kuleuven.xanderpeng.emissions.network.osm;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This is an implementation of the {@link OsmElementHandler interface.}
 */

public class OsmElementHandlerImpl implements  OsmElementHandler{

    // These are raw nodes and ways from the OSM file
    private final Map<Long, OsmNode> nodes = new HashMap<>();
    private final Map<Long, OsmWay> ways = new HashMap<>();
    private final Map<Long, HashSet<String>> wayModes = new HashMap<>();

    @Override
    public void handleNode(OsmNode node) {
        nodes.put(node.getId(), node);
    }

    @Override
    public void handleWay(OsmWay way) {
        Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
        // Create a new instance of the FilteredOsmWayTags
        FilteredOsmWayTags filteredOsmWayTags = new FilteredOsmWayTags();
        HashSet<String> allowedTransportModes = filteredOsmWayTags.matchTransportMode(tags);
        if (!allowedTransportModes.isEmpty()) {
            ways.put(way.getId(), way);
            wayModes.put(way.getId(), allowedTransportModes);
        }

    }

    @Override
    public void handleRelation(OsmRelation relation) {

    }

    @Override
    public void complete() {

    }

    public Map<Long, OsmNode> getNodes() {
        return nodes;
    }

    public Map<Long, OsmWay> getWays() {
        return ways;
    }

    public Map<Long, HashSet<String>> getWayModes() {
        return wayModes;
    }
}
