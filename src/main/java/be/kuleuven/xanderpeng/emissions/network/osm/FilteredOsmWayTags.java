package be.kuleuven.xanderpeng.emissions.network.osm;

import org.matsim.api.core.v01.TransportMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * Here, we define the tags that are used to filter the OSM ways that are used in the network.
 * Currently, there are only some (incomplete) cases defined for {@link org.matsim.api.core.v01.TransportMode} as below:
 * - car
 * - bicycle
 * - walk
 */
public class FilteredOsmWayTags {

    HashMap<String, ArrayList<String>> carMap = new HashMap<>();
    HashMap<String, ArrayList<String>> bicycleMap = new HashMap<>();
    HashMap<String, ArrayList<String>> walkMap = new HashMap<>();

    public FilteredOsmWayTags() {
        // Car
       ArrayList<String> carHighwayValues = new ArrayList<>(Arrays.asList("trunk", "primary", "secondary", "tertiary", "residential", "motorway_link", "trunk_link", "primary_link", "secondary_link"));
        carMap.put("highway", carHighwayValues);

//        carMap.get("highway").add("unclassified");  // TODO: check if this is correct

//        carMap.get("highway").add("service");   // TODO: check if this is correct


        // Bicycle
        ArrayList<String> bicycleBicycleValues= new ArrayList<>(Arrays.asList("yes", "use_sidepath", "lane"));
        bicycleMap.put("bicycle", bicycleBicycleValues);

        ArrayList<String> bicycleHighwayValues = new ArrayList<>(Arrays.asList("cycleway"));
        bicycleMap.put("highway", bicycleHighwayValues);

        ArrayList<String> bicycleCyclewayValues = new ArrayList<>(Arrays.asList("lane"));
        bicycleMap.put("cycleway", bicycleCyclewayValues);


        // It indicates cyclists should use the adjacent way, so this link is not available for cycling (in my opinion)
        bicycleMap.get("bicycle").add("use_sidepath");

        // Walk
        ArrayList<String> walkHighwayValues = new ArrayList<>(Arrays.asList("footway", "pedestrian", "path", "steps"));
        walkMap.put("highway", walkHighwayValues);
    }

    public HashMap<String, ArrayList<String>> getCarMap() {
        return carMap;
    }

    public HashMap<String, ArrayList<String>> getBicycleMap() {
        return bicycleMap;
    }

    public HashMap<String, ArrayList<String>> getWalkMap() {
        return walkMap;
    }

    public HashMap<String, ArrayList<String>> getMap(String transportMode) {
        return switch (transportMode) {
            case "car" -> carMap;
            case "bicycle" -> bicycleMap;
            case "walk" -> walkMap;
            default -> null;
        };
    }

    public void setMap(String transportMode, HashMap<String, ArrayList<String>> map) {
        switch (transportMode) {
            case "car" -> carMap = map;
            case "bicycle" -> bicycleMap = map;
            case "walk" -> walkMap = map;
        };
    }


    public HashSet<String> matchTransportMode(Map<String, String> tags) {
        HashSet <String> transportModes = new HashSet<>();

        for (String tag: tags.keySet()) {
            if (carMap.containsKey(tag) && carMap.get(tag).contains(tags.get(tag))){
                transportModes.add(TransportMode.car);
            }
            if (bicycleMap.containsKey(tag) && bicycleMap.get(tag).contains(tags.get(tag))) {
                transportModes.add(TransportMode.bike);
            }
            if (walkMap.containsKey(tag) && walkMap.get(tag).contains(tags.get(tag))){
                transportModes.add(TransportMode.walk);
            }
        }

        return transportModes;
    }
}
