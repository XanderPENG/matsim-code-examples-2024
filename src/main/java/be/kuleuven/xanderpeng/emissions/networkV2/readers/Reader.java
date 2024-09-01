package be.kuleuven.xanderpeng.emissions.networkV2.readers;

import be.kuleuven.xanderpeng.emissions.networkV2.core.NetworkElement;

import java.util.Map;

/**
 * This interface is used to read the network with different file format (e.g., shp, osm, geojson.)
 * TODO: Add shp and geojson reader
 */

public interface Reader {

    void read(String file);

    Map<String, NetworkElement.Node> getRawNodes();

    Map<String, NetworkElement.Link> getRawLinks();

}
