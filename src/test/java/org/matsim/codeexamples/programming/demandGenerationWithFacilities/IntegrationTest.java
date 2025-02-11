/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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
package org.matsim.codeexamples.programming.demandGenerationWithFacilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.matsim.codeexamples.population.demandGenerationWithFacilities.RunCreateFacilities;
import org.matsim.codeexamples.population.demandGenerationWithFacilities.RunCreatePopulationAndDemand;
import org.matsim.testcases.MatsimTestUtils;

/**
 * @author nagel
 *
 */
public class IntegrationTest {
	
	@RegisterExtension public MatsimTestUtils utils = new MatsimTestUtils() ;

	@Test
	final void test() {
		
//		try {
			RunCreateFacilities.main(null);
//		} catch ( Exception eee ) {
//			eee.printStackTrace();
//			Assert.fail();
//		}
		

//		try {
			RunCreatePopulationAndDemand.main(null);
//		} catch ( Exception eee ) {
//			eee.printStackTrace();
//			Assert.fail();
//		}

		// We don't want to check in the input network.
//		try {
//			RunCreateNetwork.main(null);
//		} catch ( Exception eee ) {
//			eee.printStackTrace();
//			Assert.fail();
//		}

		// The above test only tests if it runs, not if the output is reasonable.  Please go ahead and improve this. kai, jul'15
		
	}

}
