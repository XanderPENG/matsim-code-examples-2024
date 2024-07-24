package be.kuleuven.xanderpeng.emissions.network.osmv2;


import org.matsim.api.core.v01.TransportMode;

import java.util.HashMap;
import java.util.Map;

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

    private final Map<String, OsmTagsAndValueLists> carMap;
    private final Map<String, OsmTagsAndValueLists> bikeMap;
    private final Map<String, OsmTagsAndValueLists> walkMap;
    private final Map<String, OsmTagsAndValueLists> ptMap;

    private TransportModeTagMapper(Builder builder){
        carMap = builder.carMap;
        bikeMap = builder.bikeMap;
        walkMap = builder.walkMap;
        ptMap= builder.ptMap;
    }

    private static class Builder{
        private final Map<String, OsmTagsAndValueLists> carMap = new HashMap<>();
        private final Map<String, OsmTagsAndValueLists> bikeMap = new HashMap<>();
        private final Map<String, OsmTagsAndValueLists> walkMap = new HashMap<>();
        private final Map<String, OsmTagsAndValueLists> ptMap = new HashMap<>();

        public Builder addCarMap(String key, OsmTagsAndValueLists value){
            carMap.put(key, value);
            return this;
        }

        public Builder addBikeMap(String key, OsmTagsAndValueLists value){
            bikeMap.put(key, value);
            return this;
        }

        public Builder addWalkMap(String key, OsmTagsAndValueLists value){
            walkMap.put(key, value);
            return this;
        }

        public Builder addPtMap(String key, OsmTagsAndValueLists value){
            ptMap.put(key, value);
            return this;
        }

        public TransportModeTagMapper build(){
            return new TransportModeTagMapper(this);
        }
    }

}
