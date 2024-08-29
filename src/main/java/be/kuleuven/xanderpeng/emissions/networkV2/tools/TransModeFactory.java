package be.kuleuven.xanderpeng.emissions.networkV2.tools;

import be.kuleuven.xanderpeng.emissions.networkV2.core.ModeKeyValueMapping;
import be.kuleuven.xanderpeng.emissions.networkV2.core.TransMode;

import java.util.Map;

/**
 * This class is used to store the default transportation modes and their parameters.
 */
public class TransModeFactory {

    public static TransMode CAR = new TransMode(TransMode.Mode.CAR, new ModeKeyValueMapping.Builder()
            .setMode(TransMode.Mode.CAR)
            .addKeyValueMapping(Map.of("highway", "motorway"))
            .addKeyValueMapping(Map.of("highway", "trunk"))
            .build(), 130 / 3.6, 0.242, 1800, 3.5, 2);

    public static TransMode PT = new TransMode(TransMode.Mode.PT, new ModeKeyValueMapping.Builder()
            .setMode(TransMode.Mode.PT)
            .addKeyValueMapping(Map.of("route", "bus"))
            .addKeyValueMapping(Map.of("route", "trolleybus"))
            .addKeyValueMapping(Map.of("route", "share_taxi"))
            .addKeyValueMapping(Map.of("route", "train"))
            .addKeyValueMapping(Map.of("route", "light_rail"))
            .addKeyValueMapping(Map.of("route", "subway"))
            .addKeyValueMapping(Map.of("route", "tram"))
            .build(), 40 / 3.6, 0.142, 1200, 3.5, 1);

    public static TransMode TRAIN = new TransMode(TransMode.Mode.TRAIN, new ModeKeyValueMapping.Builder()
            .setMode(TransMode.Mode.TRAIN)
            .addKeyValueMapping(Map.of("route", "train"))
            .build(), 100 / 3.6, 0.542, 100, 5, 1);

    public static TransMode BIKE = new TransMode(TransMode.Mode.BIKE, new ModeKeyValueMapping.Builder()
            .setMode(TransMode.Mode.BIKE)
            .addKeyValueMapping(Map.of("highway", "cycleway"))
            .addKeyValueMapping(Map.of("highway", "path"))
            .addKeyValueMapping(Map.of("highway", "footway"))
            .addKeyValueMapping(Map.of("highway", "pedestrian"))
            .addKeyValueMapping(Map.of("highway", "steps"))
            .addKeyValueMapping(Map.of("highway", "bridleway"))
            .addKeyValueMapping(Map.of("highway", "track"))
            .addKeyValueMapping(Map.of("highway", "unclassified"))
            .addKeyValueMapping(Map.of("highway", "residential"))
            .addKeyValueMapping(Map.of("highway", "service"))
            .addKeyValueMapping(Map.of("highway", "tertiary"))
            .addKeyValueMapping(Map.of("highway", "secondary"))
            .addKeyValueMapping(Map.of("highway", "primary"))
            .addKeyValueMapping(Map.of("highway", "trunk"))
            .addKeyValueMapping(Map.of("highway", "motorway"))
            .build(), 20 / 3.6, 0, 2000, 2, 1);

    public static TransMode WALK = new TransMode(TransMode.Mode.WALK, new ModeKeyValueMapping.Builder()
            .setMode(TransMode.Mode.WALK)
            .addKeyValueMapping(Map.of("highway", "cycleway", "bicycle", "yes"))
            .addKeyValueMapping(Map.of("highway", "footway"))
            .addKeyValueMapping(Map.of("highway", "pedestrian"))
            .addKeyValueMapping(Map.of("highway", "steps"))
            .addKeyValueMapping(Map.of("highway", "path"))
            .addKeyValueMapping(Map.of("highway", "track"))
            .addKeyValueMapping(Map.of("highway", "unclassified"))
            .addKeyValueMapping(Map.of("highway", "residential"))
            .addKeyValueMapping(Map.of("highway", "service"))
            .addKeyValueMapping(Map.of("highway", "tertiary"))
            .addKeyValueMapping(Map.of("highway", "secondary"))
            .addKeyValueMapping(Map.of("highway", "primary"))
            .addKeyValueMapping(Map.of("highway", "trunk"))
            .addKeyValueMapping(Map.of("highway", "motorway"))
            .build(), 5 / 3.6, 0, 5000, 1, 1);

    public static TransMode SHIP = new TransMode(TransMode.Mode.SHIP, new ModeKeyValueMapping.Builder()
            .setMode(TransMode.Mode.SHIP)
            .addKeyValueMapping(Map.of("route", "ferry"))
            .build(), 20 / 3.6, 0.142, 1000, 10, 1);

    public static TransMode OTHER = new TransMode(TransMode.Mode.OTHER, new ModeKeyValueMapping.Builder()
            .setMode(TransMode.Mode.OTHER)
            .addKeyValueMapping(Map.of("*", "*"))
            .build(), 20 / 3.6, 0.142, 1000, 3.5, 1);




}
