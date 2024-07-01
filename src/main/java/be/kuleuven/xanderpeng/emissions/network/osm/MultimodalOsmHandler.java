package be.kuleuven.xanderpeng.emissions.network.osm;


import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

import java.io.IOException;

import java.util.HashSet;
import java.util.Map;


public class MultimodalOsmHandler implements OsmHandler {

    public Map<Long, OsmNode> nodes;
    public Map<Long, OsmWay> ways;
    public Map<Long, HashSet<String>> wayModes;

    // A private element handler
    private OsmElementHandler elementHandler = new OsmElementHandlerImpl();

    public MultimodalOsmHandler(OsmElementHandler elementHandler) {
        this.elementHandler = elementHandler;
    }

    @Override
    public void handle(OsmBounds osmBounds) throws IOException {

    }

    @Override
    public void handle(OsmNode osmNode) throws IOException {
        elementHandler.handleNode(osmNode);
    }

    @Override
    public void handle(OsmWay osmWay) throws IOException {
        elementHandler.handleWay(osmWay);
    }

    @Override
    public void handle(OsmRelation osmRelation) throws IOException {
        elementHandler.handleRelation(osmRelation);
    }

    @Override
    public void complete() throws IOException {

    }

    public Map<Long, OsmNode> getNodes() {
        return elementHandler.getNodes();
    }

    public Map<Long, OsmWay> getWays() {
        return elementHandler.getWays();
    }

    public Map<Long, HashSet<String>> getWayModes() {
        return elementHandler.getWayModes();
    }
}

