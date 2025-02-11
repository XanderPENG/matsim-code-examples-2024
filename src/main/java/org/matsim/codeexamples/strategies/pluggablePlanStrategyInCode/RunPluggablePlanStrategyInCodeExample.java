/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.codeexamples.strategies.pluggablePlanStrategyInCode;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ReplanningConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;

public class RunPluggablePlanStrategyInCodeExample {
	private static final String STRATEGY_NAME = "doSomethingSpecial";

	public static void main(final String[] args) {
//		ControlerUtils.initializeOutputLogging();
		OutputDirectoryLogging.catchLogEntries();
		
		
		Config config;
		if ( args.length==0 ) {
			config = ConfigUtils.loadConfig(IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("equil"), "config.xml")) ;
		} else {
			config = ConfigUtils.loadConfig(args[0]);
		}
		
		//add a strategy to the config
		StrategySettings stratSets = new StrategySettings();
		stratSets.setStrategyName(STRATEGY_NAME);
		stratSets.setWeight(0.1);
		config.replanning().addStrategySettings(stratSets);
		
		//let the output directory be overwritten
		config.controller().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		
		//only run one iteration
		config.controller().setFirstIteration(0);
		config.controller().setLastIteration(1);
		
		final Controler controler = new Controler(config);
		
		//add the binding strategy 
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addPlanStrategyBinding(STRATEGY_NAME).toProvider(MyPlanStrategyProvider.class);
			}
		});
		controler.run();

	}

}
