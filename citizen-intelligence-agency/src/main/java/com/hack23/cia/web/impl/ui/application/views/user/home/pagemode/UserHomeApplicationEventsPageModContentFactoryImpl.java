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
package com.hack23.cia.web.impl.ui.application.views.user.home.pagemode;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import com.hack23.cia.model.internal.application.system.impl.ApplicationActionEvent;
import com.hack23.cia.model.internal.application.system.impl.ApplicationActionEvent_;
import com.hack23.cia.model.internal.application.system.impl.ApplicationEventGroup;
import com.hack23.cia.model.internal.application.user.impl.UserAccount;
import com.hack23.cia.model.internal.application.user.impl.UserAccount_;
import com.hack23.cia.service.api.DataContainer;
import com.hack23.cia.web.impl.ui.application.action.ViewAction;
import com.hack23.cia.web.impl.ui.application.util.UserContextUtil;
import com.hack23.cia.web.impl.ui.application.views.common.labelfactory.LabelFactory;
import com.hack23.cia.web.impl.ui.application.views.common.menufactory.api.UserHomeMenuItemFactory;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.AdminViews;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.CommonsViews;
import com.hack23.cia.web.impl.ui.application.views.common.viewnames.UserHomePageMode;
import com.hack23.cia.web.impl.ui.application.views.pageclicklistener.PageItemPropertyClickListener;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * The Class UserHomeApplicationEventsPageModContentFactoryImpl.
 */
@Component
public final class UserHomeApplicationEventsPageModContentFactoryImpl
		extends AbstractUserHomePageModContentFactoryImpl {

	private static final PageItemPropertyClickListener LISTENER = new PageItemPropertyClickListener(
			AdminViews.ADMIN_APPLICATIONS_EVENTS_VIEW_NAME, "hjid");

	private static final String[] HIDE_COLUMNS = new String[] { "hjid", "userId", "sessionId", "modelObjectId",
			"modelObjectVersion" };

	private static final String[] COLUMN_ORDER = new String[] { "hjid", "createdDate", "eventGroup",
			"applicationOperation", "actionName", "page", "pageMode", "elementId", "applicationMessage", "errorMessage",
			"modelObjectVersion" };

	private static final String APPLICATION_ACTION_EVENT = "ApplicationActionEvent";

	/** The Constant USERHOME. */
	private static final String USERHOME = "Userhome:";

	/** The Constant USER_EVENTS. */
	private static final String USER_EVENTS = "User Events";

	/** The user home menu item factory. */
	@Autowired
	private UserHomeMenuItemFactory userHomeMenuItemFactory;

	/**
	 * Instantiates a new user home security settings page mod content factory impl.
	 */
	public UserHomeApplicationEventsPageModContentFactoryImpl() {
		super();
	}

	@Override
	public boolean matches(final String page, final String parameters) {
		return NAME.equals(page) && parameters.contains(UserHomePageMode.USER_EVENTS.toString());
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@Override
	public Layout createContent(final String parameters, final MenuBar menuBar, final Panel panel) {
		final VerticalLayout panelContent = createPanelContent();

		final String pageId = getPageId(parameters);

		userHomeMenuItemFactory.createUserHomeMenuBar(menuBar, pageId);

		LabelFactory.createHeader2Label(panelContent, USER_EVENTS);

		final String userIdFromSecurityContext = UserContextUtil.getUserIdFromSecurityContext();

		if (userIdFromSecurityContext == null) {
			UI.getCurrent().getNavigator().navigateTo(CommonsViews.MAIN_VIEW_NAME);
		} else {

			final DataContainer<UserAccount, Long> dataContainer = getApplicationManager()
					.getDataContainer(UserAccount.class);
			final Optional<UserAccount> userAccount = dataContainer
					.getAllBy(UserAccount_.userId, userIdFromSecurityContext).stream().findFirst();
			final DataContainer<ApplicationActionEvent, Long> eventDataContainer = getApplicationManager()
					.getDataContainer(ApplicationActionEvent.class);

			if (userAccount.isPresent()) {
				getGridFactory().createBasicBeanItemGrid(panelContent, ApplicationActionEvent.class,
						eventDataContainer.findOrderedListByProperty(ApplicationActionEvent_.userId,
								userAccount.get().getUserId(), ApplicationActionEvent_.createdDate),
						APPLICATION_ACTION_EVENT, COLUMN_ORDER, HIDE_COLUMNS, LISTENER, null, null);
			}
		}

		panel.setCaption(NAME + "::" + USERHOME + USER_EVENTS);

		getPageActionEventHelper().createPageEvent(ViewAction.VISIT_USER_HOME_VIEW, ApplicationEventGroup.USER, NAME,
				parameters, pageId);

		return panelContent;

	}

}
