package be.kuleuven.xanderpeng.emissions.networkV2.readers;

import be.kuleuven.xanderpeng.emissions.networkV2.core.NetworkElement;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface Reader {

    void read(String file) throws IOException;

    Map<String, NetworkElement.Node> getRawNodes();

    Map<String, NetworkElement.Link> getRawLinks();

}
