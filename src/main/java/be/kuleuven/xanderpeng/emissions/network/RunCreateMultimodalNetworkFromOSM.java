package be.kuleuven.xanderpeng.emissions.network;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.multimodal.MultiModalModule;
import org.matsim.contrib.multimodal.config.MultiModalConfigGroup;
import org.matsim.contrib.multimodal.tools.MultiModalNetworkCreator;
import org.matsim.contrib.multimodal.tools.PrepareMultiModalScenario;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

/**
 * This class is used to create a multimodal network from an OSM file (with .pbf format)
 * using the {@link } and {@link } classes, respectively.
 */

public class RunCreateMultimodalNetworkFromOSM {

    private static final Logger log = LogManager.getLogger(PrepareMultiModalScenario.class);
    private static final String UTM31nAsEpsg = "EPSG:32631";
    private static final String input = "src/main/resources/be.kuleuven.xanderpeng.emissions/network/output/mapSample.xml";
    private static final String berlinConfigPath = "D:\\IdeaProjects\\matsim-libs-2024\\examples\\scenarios\\berlin\\config_multimodal.xml";

    public static void main(String[] args) {

        if (!Files.exists(Paths.get(input))) {
            System.out.println("Cannot find the input file. Please make sure the file exists.");
            System.exit(0);
        }else{
            System.out.println("File exists.");

        }

        Config config = ConfigUtils.createConfig(new MultiModalConfigGroup());
//        Config config = ConfigUtils.loadConfig(berlinConfigPath, new MultiModalConfigGroup());
        config.controller().setOutputDirectory("src/main/resources/be.kuleuven.xanderpeng.emissions/network/output/multiModalContribBerlin/");
//        config.network().setInputFile(input);
        Scenario scenario = ScenarioUtils.loadScenario(config);
//        Scenario scenario = ScenarioUtils.createMutableScenario(config);

        MultiModalConfigGroup multiModalConfigGroup = ConfigUtils.addOrGetModule(config, MultiModalConfigGroup.GROUP_NAME, MultiModalConfigGroup.class);
        multiModalConfigGroup.setSimulatedModes("walk,bike");
        multiModalConfigGroup.setCutoffValueForNonCarModes(10.0 / 3.6);
        multiModalConfigGroup.setCreateMultiModalNetwork(true);

//        if (multiModalConfigGroup.isCreateMultiModalNetwork()) {
//            log.info("Creating multi modal network.");
//            new MultiModalNetworkCreator(multiModalConfigGroup).run(scenario.getNetwork());
//        }
        PrepareMultiModalScenario.run(scenario);
        Controler controler = new Controler(scenario);

        controler.addOverridingModule(new MultiModalModule());
//        controler.run();
        new NetworkWriter(scenario.getNetwork()).write("src/main/resources/be.kuleuven.xanderpeng.emissions/network/output/mapSampleMultimodalNetworkV2.xml");
    }
}
