/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.wires.client;

import static org.uberfire.workbench.model.menu.MenuFactory.newTopLevelMenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.JSWorkbenchScreenActivity;
import org.uberfire.client.UberFirePreferences;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * GWT's Entry-point for Uberfire-showcase
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    public Identity identity;

   

    @AfterInitialization
    public void startApp() {
        UberFirePreferences.setProperty("org.uberfire.client.workbench.widgets.listbar.context.disable", true);
        setupMenu();
        hideLoadingPopup();
    }

    private void setupMenu() {
        final AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
        final Menus menus = newTopLevelMenu("Wires").respondsWith(new Command() {
            @Override
            public void execute() {
                if (defaultPerspective != null) {
                    placeManager.goTo(new DefaultPlaceRequest(defaultPerspective.getIdentifier()));
                } else {
                    Window.alert("Default perspective not found.");
                }
            }
        }).endMenu().
            newTopLevelMenu("Bayesian Networks").respondsWith(new Command() {
            @Override
            public void execute() {
                if (defaultPerspective != null) {
                    placeManager.goTo(new DefaultPlaceRequest("WiresBayesianPerspective"));
                } else {
                    Window.alert("Default perspective not found.");
                }
            }
        }).endMenu().     
       newTopLevelMenu("Logout").position(MenuPosition.RIGHT).respondsWith(new Command() {
            @Override
            public void execute() {
                redirect(GWT.getModuleBaseURL() + "uf_logout");
            }
        }).endMenu().build();

        menubar.addMenus(menus);
    }

    private AbstractWorkbenchPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager
                .lookupBeans(AbstractWorkbenchPerspectiveActivity.class);
        final Iterator<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop: while (perspectivesIterator.hasNext()) {
            final IOCBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if (instance.isDefault()) {
                defaultPerspective = instance;
                break outer_loop;
            } else {
                iocManager.destroyBean(instance);
            }
        }
        return defaultPerspective;
    }

    private List<MenuItem> getScreens() {
        final List<MenuItem> screens = new ArrayList<MenuItem>();
        final List<String> names = new ArrayList<String>();

        for (final IOCBeanDef<WorkbenchScreenActivity> _menuItem : IOC.getBeanManager().lookupBeans(
                WorkbenchScreenActivity.class)) {
            final String name;
            if (_menuItem.getBeanClass().equals(JSWorkbenchScreenActivity.class)) {
                name = _menuItem.getName();
            } else {
                name = IOC.getBeanManager().lookupBean(_menuItem.getBeanClass()).getName();
            }

           
        }

        Collections.sort(names);

        for (final String name : names) {
            final MenuItem item = MenuFactory.newSimpleItem(name).respondsWith(new Command() {
                @Override
                public void execute() {
                    placeManager.goTo(new DefaultPlaceRequest(name));
                }
            }).endMenu().build().getItems().get(0);
            screens.add(item);
        }

        return screens;
    }

    private List<MenuItem> getPerspectives() {
        final List<MenuItem> perspectives = new ArrayList<MenuItem>();
        for (final PerspectiveActivity perspective : getPerspectiveActivities()) {
            final String name = perspective.getPerspective().getName();
            final Command cmd = new Command() {

                @Override
                public void execute() {
                    placeManager.goTo(new DefaultPlaceRequest(perspective.getIdentifier()));
                }

            };
            final MenuItem item = MenuFactory.newSimpleItem(name).respondsWith(cmd).endMenu().build().getItems().get(0);
            perspectives.add(item);
        }

        return perspectives;
    }

    private List<AbstractWorkbenchPerspectiveActivity> getPerspectiveActivities() {

        // Get Perspective Providers
        final Set<AbstractWorkbenchPerspectiveActivity> activities = activityManager
                .getActivities(AbstractWorkbenchPerspectiveActivity.class);

        // Sort Perspective Providers so they're always in the same sequence!
        List<AbstractWorkbenchPerspectiveActivity> sortedActivities = new ArrayList<AbstractWorkbenchPerspectiveActivity>(
                activities);
        Collections.sort(sortedActivities, new Comparator<AbstractWorkbenchPerspectiveActivity>() {
            @Override
            public int compare(AbstractWorkbenchPerspectiveActivity o1, AbstractWorkbenchPerspectiveActivity o2) {
                return o1.getPerspective().getName().compareTo(o2.getPerspective().getName());
            }
        });

        return sortedActivities;
    }

    private Collection<WorkbenchScreenActivity> getScreenActivities() {

        // Get Perspective Providers
        return activityManager.getActivities(WorkbenchScreenActivity.class);
    }

    // Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get("loading").getElement();

        new Animation() {

            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }
        }.run(500);
    }

    public static native void redirect(String url)/*-{
                                                  $wnd.location = url;
                                                  }-*/;

}