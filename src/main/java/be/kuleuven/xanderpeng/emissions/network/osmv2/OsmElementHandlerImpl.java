package be.kuleuven.xanderpeng.emissions.network.osmv2;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import org.matsim.api.core.v01.TransportMode;

import java.util.Map;
import java.util.Set;

public class OsmElementHandlerImpl implements OsmElementHandler {


    @Override
    public void handleNode(OsmNode node, Map<Long, OsmNode> nodes) {
        nodes.put(node.getId(), node);
    }

    @Override
    public void handleWay(OsmWay way, Map<Long, OsmWay> ways, Map<Long, Set<String>> wayModes, TransportModeTagMapper transportModeTagMapper) {

        Map<String, String> tagValuePairs = OsmModelUtil.getTagsAsMap(way);
        Set<String> allowedModes = transportModeTagMapper.matchTransportMode(tagValuePairs);
        if (!allowedModes.isEmpty()) {
            ways.put(way.getId(), way);
            wayModes.put(way.getId(), allowedModes);
        }
        // For non-recognized modes, we set it as airplane
//        else {
//            ways.put(way.getId(), way);
//            wayModes.put(way.getId(), Set.of(TransportMode.airplane));
//        }
    }

    @Override
    public void handleRelation(OsmRelation relation, Map<Long, OsmRelation> relations) {

    }

    @Override
    public void complete() {

    }



}
