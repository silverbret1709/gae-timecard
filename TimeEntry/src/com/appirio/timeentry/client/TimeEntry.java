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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.AbsolutePanel; 
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import java.util.Date;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TimeEntry implements EntryPoint {

	private VerticalPanel mainPanel = new VerticalPanel();
	private AbsolutePanel totalPanel = new AbsolutePanel();
	private DockPanel navPanel = new DockPanel(); 
	
	private Label weekStartLabel = new Label("Week Start Date");
	private Label totalLabel = new Label("0.00");
	private DateBox dateBox = new DateBox();
	private Button addRowButton = new Button("Add Row");
	private Button saveButton = new Button("Save");
	private FlexTable flexTable = new FlexTable();
	
	// tracks the current row and column in the grid
	private int currentRow = 0;
	private int currentColumn = 0;
	private Date startDate;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		// set up a horizontal panel to hold the date picker
		HorizontalPanel leftNav = new HorizontalPanel();
		leftNav.setSpacing(5);
		leftNav.add(weekStartLabel);
		leftNav.add(dateBox);
		
		// set up a horizontal panel to hold the add and save buttons
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setSpacing(5);
		buttonPanel.add(addRowButton);
		buttonPanel.add(saveButton);
		
		// set up another horizontal panel to all the button to dock right
		HorizontalPanel rightNav = new HorizontalPanel();
		rightNav.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		rightNav.setWidth("100%");
		rightNav.add(buttonPanel);
		
		// add all of the navigation panels to the dock panel
		navPanel.setWidth("1000px");
		navPanel.add(leftNav, DockPanel.WEST);
		navPanel.add(rightNav, DockPanel.EAST);
		
		// set up a horizontal panel to hold the grand total
		totalPanel.setSize("1000px","50px");
		totalPanel.add(new Label("Total:"), 900, 25);
		totalPanel.add(totalLabel, 950, 25);
		
		dateBox.setWidth("100px");
		dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("M/d/yyyy"))); 
		
	    // listen for mouse events on the save button
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.alert("Everything saved!!");
				removeAllRows();
			}
	    });
		
	    // listen for mouse events on the add new row button
		addRowButton.addClickHandler(new ClickHandler() {
	      public void onClick(ClickEvent event) {
	        addRow();
	      }
	    });
		
		// listen for the changes in the value of the date
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			public void onValueChange(ValueChangeEvent<Date> evt) {
				startDate = evt.getValue();
				renameColumns();
			}
		});
				
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
		mainPanel.add(totalPanel);

	    // associate the main panel with the HTML host page.
	    RootPanel.get("timeentryUI").add(mainPanel);
	    
	}
	
	private void renameColumns() {
		flexTable.setText(0, 3, formatDate(startDate));
		flexTable.setText(0, 4, formatDate(addDays(startDate,1)));
		flexTable.setText(0, 5, formatDate(addDays(startDate,2)));
		flexTable.setText(0, 6, formatDate(addDays(startDate,3)));
		flexTable.setText(0, 7, formatDate(addDays(startDate,4)));
		flexTable.setText(0, 8, formatDate(addDays(startDate,5)));
		flexTable.setText(0, 9, formatDate(addDays(startDate,6)));
	}
	
	private String formatDate(Date d) {
		return (d.getMonth()+1)+"/"+d.getDate()+" ("+d.toString().substring(0, 2)+")";
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
		mon.setValue("0");
		mon.setWidth("50px");
		TextBox tue = new TextBox();
		tue.setValue("0");
		tue.setWidth("50px");
		TextBox wed = new TextBox();
		wed.setValue("0");
		wed.setWidth("50px");
		TextBox thu = new TextBox();
		thu.setValue("0");
		thu.setWidth("50px");
		TextBox fri = new TextBox();
		fri.setValue("0");
		fri.setWidth("50px");
		TextBox sat = new TextBox();
		sat.setValue("0");
		sat.setWidth("50px");
		TextBox sun = new TextBox();
		sun.setValue("0");
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
		
		flexTable.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				HTMLTable.Cell cellForEvent = flexTable.getCellForEvent(event);
				currentRow = cellForEvent.getRowIndex();
				currentColumn = cellForEvent.getCellIndex();
			}
		});
		
		mon.addValueChangeHandler(timeChangeHandler);
		tue.addValueChangeHandler(timeChangeHandler);
		wed.addValueChangeHandler(timeChangeHandler);
		thu.addValueChangeHandler(timeChangeHandler);
		fri.addValueChangeHandler(timeChangeHandler);
		sat.addValueChangeHandler(timeChangeHandler);
		sun.addValueChangeHandler(timeChangeHandler);
		
		/**
		// Add a handler to handle drop box events
		lbProjects.addChangeHandler(new ChangeHandler() {
	      public void onChange(ChangeEvent event) {
	    	  showMilestones(lbProjects, lbProjects.getSelectedIndex());
	      }
	    });
	    **/
		
	}
	
	private Date addDays(Date d, int numberOfDays) {
	
		int day = d.getDate();
		int month = d.getMonth();
		int year = d.getYear();
		
		//System.out.println("day: "+day);
		//System.out.println("month: "+(month+1));
		//System.out.println("year: "+(year+1900));
		
		//Date d2 = ;
		//System.out.println(d2);
		
		
		return new Date(year, month, day+numberOfDays);
		
	}
	
	private ValueChangeHandler<String> timeChangeHandler = new ValueChangeHandler<String>() {
		public void onValueChange(ValueChangeEvent<String> evt) {
			
			try {
				double t = Double.parseDouble(evt.getValue());
				if (t > 24) 
					Window.alert("You cannot work more than 24 hours a day.");
				totalRow();
			} catch (NumberFormatException e) {
				TextBox tb = (TextBox) flexTable.getWidget(currentRow, currentColumn);
				tb.setValue("0");
				flexTable.setWidget(currentRow, currentColumn, tb);
				Window.alert("Not a valid number.");
			}
			
		}
	};
	
	private void totalRow() {
		double rowTotal = 0.00;
		for (int cell = 3;cell<=9; cell++) {
			TextBox timeWidget = (TextBox) flexTable.getWidget(currentRow, cell);
			double t = Double.parseDouble(timeWidget.getValue());
			rowTotal = rowTotal + t;
			//System.out.println("time: "+cell+": "+t);
		}
		flexTable.setWidget(currentRow, 10, new Label(String.valueOf(rowTotal)));
		totalGrid();
	}
	
	private void totalGrid() {
		double grandTotal = 0.00;
		for (int row=1;row<flexTable.getRowCount();row++) {
			Label rowTotalWidget = (Label) flexTable.getWidget(row, 10);
			double rowTotal = Double.parseDouble(rowTotalWidget.getText());
			grandTotal = grandTotal + rowTotal;
		}
		totalLabel.setText(String.valueOf(grandTotal));
	}
	
	private void removeAllRows() {
		// remove all of the rows from the flex table
		for (int row=flexTable.getRowCount()-1;row>0;row--)
			flexTable.removeRow(row);
		
		// rest the total
		totalLabel.setText("0.00");
		// add a new blank row to the flex table
		addRow();
	}
	
	private void showMilestones(ListBox listBox, int category) {
		// TODO - implement this
		
	}
	
}
