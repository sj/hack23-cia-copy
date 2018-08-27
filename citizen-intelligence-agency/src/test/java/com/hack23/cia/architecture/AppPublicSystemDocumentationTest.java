/*
 * Copyright 2014 James Pether Sörling
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *	$Id$
 *  $HeadURL$
*/
package com.hack23.cia.architecture;

import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

/**
 * The Class AppPublicSystemDocumentationTest.
 */
public class AppPublicSystemDocumentationTest {

	/** The exit. */
	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	/**
	 * Generate architecture diagrams test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void generateArchitectureDiagramsTest() throws Exception {
        exit.expectSystemExit();
		AppPublicSystemDocumentation.main(new String[0]);
		assertFalse("expect System.exit",true);
	}
}