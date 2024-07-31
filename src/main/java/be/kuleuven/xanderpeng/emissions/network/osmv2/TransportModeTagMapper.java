package be.kuleuven.xanderpeng.emissions.network.osmv2;


import org.matsim.api.core.v01.TransportMode;

import java.util.*;

public class TransportModeTagMapper {
    /*
        Available Multimodal Transport Modes
     */
    public static final String CAR = TransportMode.car;
    public static final String BIKE = TransportMode.bike;
    public static final String WALK = TransportMode.walk;
    public static final String PT = TransportMode.pt;

    // TODO: add more transport modes if needed
//    public static final String TRAIN = TransportMode.train;
//    public static final String SHIP = TransportMode.ship;
//    public static final String TRUCK = TransportMode.truck;
//    public static final String DRT = TransportMode.drt;

    private  final Map<String, OsmTagsAndValueLists> transportModeTagMapper;


    private TransportModeTagMapper(Builder builder){

        this.transportModeTagMapper = builder.transportModeTagMapper;
    }

    public Map<String, OsmTagsAndValueLists> getTransportModeTagMapper() {
        return transportModeTagMapper;
    }

    public Set<String> matchTransportMode(Map<String, String> tagAndValuePairs){
        Set <String> matchedModes = new HashSet<>();

        for (Map.Entry<String, OsmTagsAndValueLists> modeEntry : transportModeTagMapper.entrySet()){
            // Get current transport mode and the corresponding tags-valueLists
            String mode = modeEntry.getKey();
            OsmTagsAndValueLists tagValueLists = modeEntry.getValue();
            Map<String, List<String>> tagAndValueLists = tagValueLists.getTagsAndValueLists();
            // check if the tag-value pairs match the current transport mode
            for (Map.Entry<String, String> tagValuePair: tagAndValuePairs.entrySet()){
                String tag = tagValuePair.getKey();
                String value = tagValuePair.getValue();
                if (tagAndValueLists.containsKey(tag) && tagAndValueLists.get(tag).contains(value)){
                    matchedModes.add(mode);
                }
            }

        }
        return matchedModes;
    }




    public static class Builder{

        private final Map<String, OsmTagsAndValueLists> transportModeTagMapper = new HashMap<>();

        public Builder addModeAndTagValueLists(String mode, OsmTagsAndValueLists tagValueLists){
            transportModeTagMapper.put(mode, tagValueLists);
            return this;
        }

        public TransportModeTagMapper build(){
            return new TransportModeTagMapper(this);
        }
    }

}
