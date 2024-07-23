package be.kuleuven.xanderpeng.emissions.network.osmv2;

import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

public class MultimodalOsmReader implements OsmHandler {

    private Map<Long, OsmNode> nodes;
    private Map<Long, OsmWay> ways;
//    public Map<Long, OsmRelation> relations;
    private Map<Long, HashSet<String>> wayModes;

    private final OsmElementHandler elementHandler;
    private final String inputFilePath;


    private MultimodalOsmReader(Builder builder) {
        elementHandler = builder.elementHandler;
        inputFilePath = builder.inputFilePath;
    }


    @Override
    public void handle(OsmBounds osmBounds) throws IOException {

    }

    @Override
    public void handle(OsmNode osmNode) throws IOException {
        elementHandler.handleNode(osmNode, nodes);
    }

    @Override
    public void handle(OsmWay osmWay) throws IOException {

    }

    @Override
    public void handle(OsmRelation osmRelation) throws IOException {

    }

    @Override
    public void complete() throws IOException {

    }

    private static class Builder {

        private OsmElementHandler elementHandler;
        private final String inputFilePath;

        public Builder(String inputFilePath) {
            this.inputFilePath = inputFilePath;
        }

        public Builder setHandler(OsmElementHandler elementHandler) {
            this.elementHandler = elementHandler;
            return this;
        }

        public MultimodalOsmReader build() {
            return new MultimodalOsmReader(this);
        }
    }

}
