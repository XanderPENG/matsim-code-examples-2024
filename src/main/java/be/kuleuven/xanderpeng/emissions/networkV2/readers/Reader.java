package be.kuleuven.xanderpeng.emissions.networkV2.readers;

import be.kuleuven.xanderpeng.emissions.networkV2.core.NetworkElement;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface Reader<T> {

    void read(String file) throws IOException;

    Map<T, NetworkElement.Node> getRawNodes();

    Map<T, NetworkElement.Link> getRawLinks();
}
