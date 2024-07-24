package be.kuleuven.xanderpeng.emissions.network.osmv2;

import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultimodalOsmReader implements OsmHandler {

    private final Map<Long, OsmNode> nodes;
    private final Map<Long, OsmWay> ways;
    public final Map<Long, OsmRelation> relations;
    private final Map<Long, Set<String>> wayModes;


    private final OsmElementHandler elementHandler;
    private final String inputFilePath;


    private MultimodalOsmReader(Builder builder) {
        nodes = new HashMap<>();
        ways = new HashMap<>();
        relations = new HashMap<>();
        wayModes = new HashMap<>();

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
        elementHandler.handleWay(osmWay, ways, wayModes);
    }

    @Override
    public void handle(OsmRelation osmRelation) throws IOException {
         elementHandler.handleRelation(osmRelation, relations);
    }

    @Override
    public void complete() throws IOException {

    }

    private static class Builder {

        private OsmElementHandler elementHandler;
        private String inputFilePath;

        public Builder(String inputFilePath) {
            this.inputFilePath = inputFilePath;
        }

        public Builder setInputFilePath(String inputFilePath) {
            this.inputFilePath = inputFilePath;
            return this;
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
