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
package com.hack23.cia.systemintegrationtest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.hack23.cia.testfoundation.AbstractSystemIntegrationTest;
import com.hack23.cia.testfoundation.Parallelized;
import com.hack23.cia.web.impl.ui.application.views.common.pagelinks.api.PageModeMenuCommand;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.ApplicationPageMode;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.CommonsViews;

import io.github.bonigarcia.wdm.ChromeDriverManager;

/**
 * The Class AbstractRoleSystemITest.
 */
@RunWith(Parallelized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractRoleSystemITest extends AbstractSystemIntegrationTest {

	/** The browser. */
	protected final String browser;

	/** The Constant webDriverMap. */
	private static final Map<String, WebDriver> webDriverMap = new ConcurrentHashMap<>();

	/** The Constant usingExternalServer. */
	private static final boolean usingExternalServer;

	private static String systemTestTargetAdminEmail;

	private static String systemTestTargetAdminPassword;

	static {
		 final String systemTestTargetUrlProperty = System.getProperty("system.test.target.url");
		 if (systemTestTargetUrlProperty != null && systemTestTargetUrlProperty.trim().length() > 0) {
			 usingExternalServer=true;
		 } else {
			 usingExternalServer=false;
		 }

		 systemTestTargetAdminEmail = System.getProperty("system.test.target.admin.email");
		 if (systemTestTargetAdminEmail == null) {
			 systemTestTargetAdminEmail = "admin@hack23.com";
		 }

		 systemTestTargetAdminPassword = System.getProperty("system.test.target.admin.password");
		 if (systemTestTargetAdminPassword == null) {
			 systemTestTargetAdminPassword = "Admin4hack23!";
		 }

		CitizenIntelligenceAgencyServer.setEnv("CIA_APP_ENCRYPTION_PASSWORD", "allhaildiscordia");
	}

	/**
	 * Instantiates a new abstract role system test.
	 *
	 * @param browser
	 *            the browser
	 */
	public AbstractRoleSystemITest(final String browser) {
		super();
		this.browser = browser;


	}


	/**
	 * Start server.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	static final synchronized public void startServer() throws Exception {
		System.setProperty("webdriver.gecko.driver", "/usr/bin/geckodriver-0.13.0-linux64");
		if (!usingExternalServer) {
			CitizenIntelligenceAgencyServer.startTestServer();
		}
		ChromeDriverManager.getInstance().setup();
	}

	/**
	 * Stop server.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@AfterClass
	static final synchronized public void stopServer() throws Exception {
		if (!usingExternalServer) {
			CitizenIntelligenceAgencyServer.stopTestServer();
		}
	}

	/**
	 * Close web driver.
	 */
	@After
	public final void closeWebDriver() {
		webDriverMap.get(browser).quit();
	}

	/**
	 * Gets the web driver.
	 *
	 * @return the web driver
	 */
	protected final synchronized WebDriver getWebDriver() {

		WebDriver driver = null;
		if ("firefox".equals(browser)) {
			final DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			capabilities.setCapability("marionette", true);
			capabilities.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, "true");
			driver = new FirefoxDriver(capabilities);
			driver.manage().window().maximize();
		} else if ("chrome".equals(browser)) {
			final ChromeOptions chromeOptions = new ChromeOptions();
		    chromeOptions.addArguments("--allow-insecure-localhost","--start-maximized");	    
		    chromeOptions.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, "true");			    
			driver = new ChromeDriver(chromeOptions);
		} else if ("htmlunit-firefox".equals(browser)) {
			final HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(BrowserVersion.FIREFOX_52);
			htmlUnitDriver.setJavascriptEnabled(true);
			driver = htmlUnitDriver;
		} else if ("htmlunit-ie11".equals(browser)) {
			final HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER);
			htmlUnitDriver.setJavascriptEnabled(true);
			driver = htmlUnitDriver;
		} else if ("htmlunit-edge".equals(browser)) {
			final HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(BrowserVersion.EDGE);
			htmlUnitDriver.setJavascriptEnabled(true);
			driver = htmlUnitDriver;
		} else if ("htmlunit-chrome".equals(browser)) {
			final HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
			htmlUnitDriver.setJavascriptEnabled(true);
			driver = htmlUnitDriver;
		} else {
			fail("No valid browser parameter:" + browser);
		}

		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	    driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);


	    webDriverMap.put(browser, driver);
		return driver;
	}


	/**
	 * Click first row in grid.
	 *
	 * @param userPageVisit
	 *            the user page visit
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	protected final  void clickFirstRowInGrid(final UserPageVisit userPageVisit) throws InterruptedException {
		final List<WebElement> gridRows = userPageVisit.getGridRows();
		assertFalse(gridRows.isEmpty());

		final WebElement choosenRow = gridRows.iterator().next();

		final List<WebElement> cells = choosenRow.findElements(By.className("v-grid-cell"));

		final WebElement choosenCell = cells.iterator().next();

		userPageVisit.performClickAction(choosenCell);

	}

	/**
	 * Login as admin.
	 *
	 * @param userPageVisit
	 *            the user page visit
	 * @throws Exception
	 *             the exception
	 */
	protected final void loginAsAdmin(final UserPageVisit userPageVisit) throws Exception {
		userPageVisit.visitDirectPage(new PageModeMenuCommand(CommonsViews.MAIN_VIEW_NAME, ApplicationPageMode.LOGIN.toString()));
		userPageVisit.loginUser(systemTestTargetAdminEmail, systemTestTargetAdminPassword);
	}

}
