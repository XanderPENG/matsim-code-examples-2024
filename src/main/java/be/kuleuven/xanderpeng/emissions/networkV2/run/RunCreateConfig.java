package be.kuleuven.xanderpeng.emissions.networkV2.run;

import be.kuleuven.xanderpeng.emissions.networkV2.config.ConnectedNetworkParamSet;
import be.kuleuven.xanderpeng.emissions.networkV2.config.LinkAttrParamSet;
import be.kuleuven.xanderpeng.emissions.networkV2.config.ModeParamSet;
import be.kuleuven.xanderpeng.emissions.networkV2.config.NetworkConverterConfigGroup;
import be.kuleuven.xanderpeng.emissions.networkV2.core.ModeKeyValueMapping;
import be.kuleuven.xanderpeng.emissions.networkV2.core.TransMode;
import be.kuleuven.xanderpeng.emissions.networkV2.tools.Utils;

import java.util.Map;
import java.util.Set;

public class RunCreateConfig {
//    NetworkConverterConfigGroup config;

    public static void main(String[] args) {
        // create a default config
//        NetworkConverterConfigGroup config = NetworkConverterConfigGroup.createDefaultConfig();

        // create a customized config
        NetworkConverterConfigGroup config = new NetworkConverterConfigGroup("shp", "EPSG:4326", Utils.outputCrs, Utils.aldiNetworkInput,
                false, true, "highway",
                Utils.networkOutputDir + "multimodalNetwork.xml",
                "NA", "NA",
                true,  Map.of("oneway", "yes"));

        // Create customized TransMode
        TransMode bikeMode = new TransMode(TransMode.Mode.BIKE, new ModeKeyValueMapping.Builder()
                .addKeyValueMapping(Map.of("highway", "cycleway"))
                .addKeyValueMapping(Map.of("bicycle", "yes"))
                .build(),
                20 / 3.6, 0, 2000, 2, 1);

        TransMode carMode = new TransMode(TransMode.Mode.CAR, new ModeKeyValueMapping.Builder()
                .addKeyValueMapping(Map.of("highway", "motorway"))
                .addKeyValueMapping(Map.of("highway", "trunk"))
                .build(),
                130 / 3.6, 0.242, 1800, 3.5, 2);

        TransMode ptMode = new TransMode(TransMode.Mode.PT, new ModeKeyValueMapping.Builder()
                .addKeyValueMapping(Map.of("route", "bus"))
                .addKeyValueMapping(Map.of("route", "trolleybus"))
                .addKeyValueMapping(Map.of("route", "share_taxi"))
                .addKeyValueMapping(Map.of("route", "train"))
                .addKeyValueMapping(Map.of("route", "light_rail"))
                .addKeyValueMapping(Map.of("route", "subway"))
                .addKeyValueMapping(Map.of("route", "tram"))
                .build(),
                40 / 3.6, 0.142, 1200, 3.5, 1);

        TransMode walkMode = new TransMode(TransMode.Mode.WALK, new ModeKeyValueMapping.Builder()
                .addKeyValueMapping(Map.of("highway", "footway"))
                .addKeyValueMapping(Map.of("highway", "pedestrian"))
                .addKeyValueMapping(Map.of("highway", "steps"))
                .build(),
                5 / 3.6, 0, 5000, 1, 1);

        // create customized ModeParamSets
        ModeParamSet bikeParamSet = new ModeParamSet(bikeMode);
        ModeParamSet carParamSet = new ModeParamSet(carMode);
        ModeParamSet ptParamSet = new ModeParamSet(ptMode);
        ModeParamSet walkParamSet = new ModeParamSet(walkMode);

        // create a customized ConnectedNetworkParamSet
        Set<TransMode.Mode> modes = Set.of(TransMode.Mode.CAR, TransMode.Mode.PT, TransMode.Mode.BIKE, TransMode.Mode.WALK);
        ConnectedNetworkParamSet connectedNetworkParamSet = new ConnectedNetworkParamSet(true, modes, "reduce");

        // create a customized LinkAttrParamSet
        LinkAttrParamSet linkAttrParamSet = new LinkAttrParamSet("max_speed", "capacity", "lanes", "width", "length");

        // Add customized Parameter sets
        config.addParameterSet(bikeParamSet);
        config.addParameterSet(carParamSet);
        config.addParameterSet(ptParamSet);
        config.addParameterSet(walkParamSet);
        config.addParameterSet(connectedNetworkParamSet);
        config.addParameterSet(linkAttrParamSet);

        // write the config file
        config.writeConfigFile(Utils.networkOutputDir + "multimodalNetworkConverterConfig.xml");
    }







}
