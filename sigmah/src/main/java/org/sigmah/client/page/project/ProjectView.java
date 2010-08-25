/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.project;

import java.util.Arrays;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * Initializes the view elements of a project page.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectView extends LayoutContainer implements ProjectPresenter.View {

    private ContentPanel panelProjectBanner;
    private TabPanel tabPanelProject;

    private TabPanel tabPanelPhases;
    private ContentPanel panelProjectModel;
    private LayoutContainer panelSelectedPhase;
    private ContentPanel panelPhaseGuide;

    private Button buttonSavePhase;
    private Button buttonActivatePhase;

    private ContentPanel panelReminders;
    private ContentPanel panelWatchedPoints;
    private ContentPanel panelFinancialProjects;
    private ContentPanel panelLocalProjects;
    private Grid<FlexibleElementDTO> gridRequiredElements;

    private final static String[] MAIN_TABS = { I18N.CONSTANTS.projectTabDashboard(),
            I18N.CONSTANTS.projectTabLogFrame(), I18N.CONSTANTS.projectTabIndicators(),
            I18N.CONSTANTS.projectTabCalendar(), I18N.CONSTANTS.projectTabReports(),
            I18N.CONSTANTS.projectTabSecurityIncident() };

    /**
     * Initializes a new ProjectView object.
     */
    public ProjectView() {
        if (Log.isDebugEnabled()) {
            Log.debug("Initializing the ProjectView object.");
        }
        setLayout(new RowLayout(Orientation.VERTICAL));

        /* Project banner */
        panelProjectBanner = new ContentPanel();
        panelProjectBanner.setHeading(I18N.CONSTANTS.projectBannerHeader());
        panelProjectBanner.setBorders(true);
        panelProjectBanner.setBorders(false);
        panelProjectBanner.setHeight(80);
        panelProjectBanner.setLayout(new HBoxLayout());

        /* Project tab panel (main tab panel) */
        tabPanelProject = new TabPanel();
        tabPanelProject.setHeight("100%");
        tabPanelProject.setStyleName("sigmah-tabProject");

        for (String tabTitle : MAIN_TABS) {
            TabItem tabItem = new TabItem(tabTitle);
            tabItem.setLayout(new FitLayout());
            tabPanelProject.add(tabItem);
        }

        LayoutContainer tabProjectContainer = new LayoutContainer(new BorderLayout());
        tabProjectContainer.setHeight("100%");

        /* Center panel */
        ListStore<FlexibleElementDTO> storeRequiredElements = new ListStore<FlexibleElementDTO>();
        storeRequiredElements.setStoreSorter(new StoreSorter<FlexibleElementDTO>() {
            @Override
            public int compare(Store<FlexibleElementDTO> store, FlexibleElementDTO m1, FlexibleElementDTO m2,
                    String property) {
                if ("type".equals(property)) {
                    return FlexibleElementType.getFlexibleElementTypeName(m1).compareTo(
                            FlexibleElementType.getFlexibleElementTypeName(m2));
                } else {
                    return super.compare(store, m1, m2, property);
                }
            }
        });
        gridRequiredElements = new Grid<FlexibleElementDTO>(storeRequiredElements, getColumModel());
        gridRequiredElements.setStyleName(I18N.CONSTANTS.projectRequiredElementsGridStyle());
        gridRequiredElements.setAutoExpandColumn("label");
        gridRequiredElements.getView().setForceFit(true);

        // Phases tab panel
        tabPanelPhases = new TabPanel();
        tabPanelPhases.setStyleName("sigmah-tabPhases");
        tabPanelPhases.setHeight("100%");

        // Toolbar
        ToolBar toolBar = new ToolBar();
        toolBar.setAlignment(HorizontalAlignment.RIGHT);
        toolBar.setBorders(false);
        buttonSavePhase = new Button(I18N.CONSTANTS.projectSavePhaseButton());
        buttonSavePhase.setEnabled(false);
        buttonActivatePhase = new Button(I18N.CONSTANTS.projectActivatePhaseButton());
        buttonActivatePhase.setEnabled(false);
        toolBar.add(buttonSavePhase);
        toolBar.add(new SeparatorToolItem());
        toolBar.add(buttonActivatePhase);

        // Tab item main panel
        RowLayout layout = new RowLayout(Orientation.HORIZONTAL);
        panelProjectModel = new ContentPanel(layout);
        panelProjectModel.setHeaderVisible(false);
        panelProjectModel.setTopComponent(toolBar);
        panelProjectModel.setBorders(false);
        panelProjectModel.setBodyBorder(false);

        panelSelectedPhase = new LayoutContainer(new FitLayout());
        panelSelectedPhase.setScrollMode(Scroll.AUTO);
        panelPhaseGuide = new ContentPanel(new FitLayout());
        panelPhaseGuide.setHeading(I18N.CONSTANTS.projectPhaseGuideHeader());

        panelProjectModel.add(gridRequiredElements, new RowData(0.22, 1, new Margins(4)));
        panelProjectModel.add(panelSelectedPhase, new RowData(0.53, 1, new Margins(4)));
        panelProjectModel.add(panelPhaseGuide, new RowData(0.25, 1, new Margins(4)));

        /* West panel */
        VerticalPanel westPanel = new VerticalPanel();
        panelReminders = new ContentPanel();
        panelReminders.setHeading(I18N.CONSTANTS.projectRemindersHeader());
        panelReminders.addText("This panel displays the reminders.");
        panelReminders.setBorders(false);
        panelReminders.setCollapsible(true);
        panelReminders.setWidth(250);
        panelReminders.setStyleName("sigmah-panelReminders");

        panelWatchedPoints = new ContentPanel();
        panelWatchedPoints.setHeading(I18N.CONSTANTS.projectWatchedPointsHeader());
        panelWatchedPoints.addText("This panel displays the watched points.");
        panelWatchedPoints.setBorders(false);
        panelWatchedPoints.setCollapsible(true);
        panelWatchedPoints.setWidth(250);

        westPanel.add(panelReminders);
        westPanel.add(panelWatchedPoints);

        /* South panel */
        LayoutContainer southPanel = new LayoutContainer(new RowLayout(Orientation.VERTICAL));
        panelFinancialProjects = new ContentPanel();
        panelFinancialProjects.setBorders(false);
        panelFinancialProjects.setHeading(I18N.CONSTANTS.projectFinancialProjectsHeader());
        panelFinancialProjects.addText("This panel displays the financial projects.");

        panelLocalProjects = new ContentPanel();
        panelLocalProjects.setHeading(I18N.CONSTANTS.projectLocalPartnerProjectsHeader());
        panelLocalProjects.setBorders(false);
        panelLocalProjects.addText("This panel displays the local partner projects.");
        southPanel.add(panelFinancialProjects, new RowData(1, 0.5, new Margins(0, 0, 10, 0)));
        southPanel.add(panelLocalProjects, new RowData(1, 0.5));

        /* BorderLayoutData */
        BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 170);
        southData.setMargins(new Margins(5));
        BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 250);
        westData.setMargins(new Margins(5));
        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(5));

        tabProjectContainer.add(westPanel, westData);
        tabProjectContainer.add(tabPanelPhases, centerData);
        tabProjectContainer.add(southPanel, southData);

        tabPanelProject.setSelection(tabPanelProject.getItem(0));
        tabPanelProject.getSelectedItem().add(tabProjectContainer);

        add(panelProjectBanner, new RowData(1, -1, new Margins(0, 0, 10, 0)));
        add(tabPanelProject, new RowData(1, 1));
    }

    /**
     * Generates the {@link ColumnModel} for the required elements grid.
     * 
     * @return the {@link ColumnModel} for the required elements grid.
     */
    private ColumnModel getColumModel() {

        // Element's label.
        final ColumnConfig labelColumn = new ColumnConfig("label", I18N.CONSTANTS.projectRequiredElementsGridLabel(),
                150);

        // Element's completion.
        final CheckColumnConfig filledInColumn = new CheckColumnConfig("filledIn",
                I18N.CONSTANTS.projectRequiredElementsGridChecked(), 40);
        filledInColumn.setMenuDisabled(false);
        filledInColumn.setSortable(false);

        // Element's type.
        final ColumnConfig typeColumn = new ColumnConfig("type", I18N.CONSTANTS.projectRequiredElementsElementType(),
                75);
        typeColumn.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {
            @Override
            public Object render(FlexibleElementDTO model, String property, ColumnData config, int rowIndex,
                    int colIndex, ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {
                return FlexibleElementType.getFlexibleElementTypeName(model);
            }
        });

        return new ColumnModel(Arrays.asList(typeColumn, labelColumn, filledInColumn));
    }

    public LayoutContainer getPanelProjectModel() {
        return panelProjectModel;
    }

    public ContentPanel getPanelProjectBanner() {
        return panelProjectBanner;
    }

    public ContentPanel getPanelReminders() {
        return panelReminders;
    }

    public ContentPanel getPanelWatchedPoints() {
        return panelWatchedPoints;
    }

    public ContentPanel getPanelFinancialProjects() {
        return panelFinancialProjects;
    }

    public ContentPanel getPanelLocalProjects() {
        return panelLocalProjects;
    }

    public TabPanel getTabPanelPhases() {
        return tabPanelPhases;
    }

    public Grid<FlexibleElementDTO> getGridRequiredElements() {
        return gridRequiredElements;
    }

    public LayoutContainer getPanelSelectedPhase() {
        return panelSelectedPhase;
    }

    public ContentPanel getPanelPhaseGuide() {
        return panelPhaseGuide;
    }

    public Button getButtonSavePhase() {
        return buttonSavePhase;
    }

    public Button getButtonActivatePhase() {
        return buttonActivatePhase;
    }

    public TabPanel getTabPanelProject() {
        return tabPanelProject;
    }

}