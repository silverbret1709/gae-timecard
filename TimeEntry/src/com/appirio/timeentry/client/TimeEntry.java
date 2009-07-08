package com.appirio.timeentry.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.AbsolutePanel; 
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TimeEntry implements EntryPoint {

	private VerticalPanel mainPanel = new VerticalPanel();
	private AbsolutePanel navPanel = new AbsolutePanel();
	private Label weekStartLabel = new Label("Week Start Date");
	private DateBox dateBox = new DateBox();
	private Button addRowButton = new Button("Add Row");
	private Button saveButton = new Button("Save");
	private FlexTable flexTable = new FlexTable();
	private DialogBox saveDialog = new DialogBox();
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
	    // Listen for mouse events on the Add button.
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveDialog.center();
				saveDialog.show();
			}
	    });
			
		Button okButton = new Button("Your records have been saved - NOT REALLY");
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveDialog.hide();
				removeAllRows();
			}
		});
		
		saveDialog.setWidget(okButton);
		
		navPanel.setSize("1000px","25px");
		navPanel.add(weekStartLabel, 0 ,0);
		navPanel.add(dateBox, 100, 0);
		navPanel.add(addRowButton, 850, 0);
		navPanel.add(saveButton, 925, 0);
				
		// set the width of the table to expand the size of the navPanel
		flexTable.setWidth("100%");
		// set the style for the table to be accessed in the css
		flexTable.setStylePrimaryName("timeEntryTable");
		// add the columns and headers
		flexTable.setText(0, 0, "Project");
		flexTable.setText(0, 1, "Milestone");
		flexTable.setText(0, 2, "Billable?");
		flexTable.setText(0, 3, "Mon");
		flexTable.setText(0, 4, "Tue");
		flexTable.setText(0, 5, "Wed");
		flexTable.setText(0, 6, "Thu");
		flexTable.setText(0, 7, "Fri");
		flexTable.setText(0, 8, "Sat");
		flexTable.setText(0, 9, "Sun");
		flexTable.setText(0, 10, "Total");
		
		// add a blank row initially
		addRow();
		
		// add the navpanel and flex table to the main panel
		mainPanel.add(navPanel);
		mainPanel.add(flexTable);

	    // Associate the Main panel with the HTML host page.
	    RootPanel.get("timeentryUI").add(mainPanel);
	    
	    // Listen for mouse events on the Add button.
		addRowButton.addClickHandler(new ClickHandler() {
	      public void onClick(ClickEvent event) {
	        addRow();
	      }
	    });
	    
	}
	
	private void addRow() {
		
		int row = flexTable.getRowCount();
		
		ListBox lbProjects = new ListBox(false);
		//lbProjects.addItem("-- Choose a Project --");
		lbProjects.addItem("Project 1");
		lbProjects.addItem("Project 2");
		lbProjects.addItem("Project 3");
		lbProjects.addItem("Project 4");
		
		ListBox lbMilestones = new ListBox(false);
		lbMilestones.addItem("Milestone a");
		lbMilestones.addItem("Milestone b");
		lbMilestones.addItem("Milestone c");
		lbMilestones.addItem("Milestone d");		
		
		// create the time input fields for all 7 days
		TextBox mon = new TextBox();
		mon.setWidth("50px");
		TextBox tue = new TextBox();
		tue.setWidth("50px");
		TextBox wed = new TextBox();
		wed.setWidth("50px");
		TextBox thu = new TextBox();
		thu.setWidth("50px");
		TextBox fri = new TextBox();
		fri.setWidth("50px");
		TextBox sat = new TextBox();
		sat.setWidth("50px");
		TextBox sun = new TextBox();
		sun.setWidth("50px");
		
		// add all of the widgets to the flex table
		flexTable.setWidget(row, 0, lbProjects);
		flexTable.setWidget(row, 1, lbMilestones);
		flexTable.setWidget(row, 2, new CheckBox());
		flexTable.setWidget(row, 3, mon);
		flexTable.setWidget(row, 4, tue);
		flexTable.setWidget(row, 5, wed);
		flexTable.setWidget(row, 6, thu);
		flexTable.setWidget(row, 7, fri);
		flexTable.setWidget(row, 8, sat);
		flexTable.setWidget(row, 9, sun);
		flexTable.setWidget(row, 10, new Label("0.00"));
		
		/**
		// Add a handler to handle drop box events
		lbProjects.addChangeHandler(new ChangeHandler() {
	      public void onChange(ChangeEvent event) {
	    	  showMilestones(lbProjects, lbProjects.getSelectedIndex());
	      }
	    });
	    **/
		
	}
	
	private void removeAllRows() {
		// remove all of the rows from the flex table
		for (int row=flexTable.getRowCount()-1;row>0;row--)
			flexTable.removeRow(row);
		
		// add a new blank row to the flex table
		addRow();
		
	}
	
	private void showMilestones(ListBox listBox, int category) {
		// TODO - implement this
		
	}
	
}
