package be.kuleuven.xanderpeng.emissions.networkV2.readers;

import be.kuleuven.xanderpeng.emissions.networkV2.core.NetworkElement;

import java.util.Map;

public class GeoJsonReader implements Reader {
    @Override
    public void read(String file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, NetworkElement.Node> getRawNodes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, NetworkElement.Link> getRawLinks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
