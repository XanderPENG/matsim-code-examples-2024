package be.kuleuven.xanderpeng.emissions.network.osm;

import de.topobyte.osm4j.pbf.seq.PbfReader;

import java.io.FileInputStream;
import java.io.InputStream;

public class MultimodalOsmReader {

    String inputFilePath;
    MultimodalOsmHandler multimodalOsmHandler;

    public MultimodalOsmReader(String path, OsmElementHandler handler) {
        this.inputFilePath = path;
        this.multimodalOsmHandler =new MultimodalOsmHandler(handler);
    }

    public void read() {
        try (InputStream inputStream = new FileInputStream(this.inputFilePath)) {
            PbfReader reader = new PbfReader(inputStream, false);
            reader.setHandler(this.multimodalOsmHandler);
            reader.read();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
