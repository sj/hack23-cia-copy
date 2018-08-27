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
package com.hack23.cia.web.impl.ui.application.views.user.parliament.pagemode;

import org.springframework.beans.factory.annotation.Autowired;

import com.hack23.cia.web.impl.ui.application.views.common.menufactory.api.ParliamentMenuItemFactory;
import com.hack23.cia.web.impl.ui.application.views.common.pagemode.AbstractBasicPageModContentFactoryImpl;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.UserViews;

/**
 * The Class AbstractPoliticianPageModContentFactoryImpl.
 */
abstract class AbstractParliamentPageModContentFactoryImpl extends AbstractBasicPageModContentFactoryImpl {

	/** The Constant NAME. */
	public static final String NAME = UserViews.PARLIAMENT_RANKING_VIEW_NAME;

	/** The test menu item factory. */
	@Autowired
	private ParliamentMenuItemFactory parliamentMenuItemFactory;

	/**
	 * Instantiates a new abstract parliament page mod content factory impl.
	 */
	AbstractParliamentPageModContentFactoryImpl() {
		super();
	}

	/**
	 * Gets the test menu item factory.
	 *
	 * @return the test menu item factory
	 */
	protected final ParliamentMenuItemFactory getParliamentMenuItemFactory() {
		return parliamentMenuItemFactory;
	}

}
