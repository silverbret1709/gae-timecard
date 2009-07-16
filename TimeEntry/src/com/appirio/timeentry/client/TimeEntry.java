package com.appirio.timeentry.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.appirio.timeentry.client.TimeEntryData;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.DateTimeFormat;
import java.util.Vector;
import java.util.Date;

public class TimeEntry implements EntryPoint {

	private VerticalPanel mainPanel = new VerticalPanel();
	private AbsolutePanel totalPanel = new AbsolutePanel();
	private DockPanel navPanel = new DockPanel(); 
	private HorizontalPanel topPanel = new HorizontalPanel(); 
	
	private Label totalLabel = new Label("0.00");
	private FlexTable flexEntryTable = new FlexTable();
	private FlexTable flexCurrentTable = new FlexTable();
	private Image logo = new Image();
	private LoginInfo loginInfo = null;
	
	// tracks the current row and column in the grid
	private int currentRow = 0;
	private int currentColumn = 0;
	private Date startDate;
	
	// create the data service
	private final DataServiceAsync dataService = GWT.create(DataService.class);
	
	public void onModuleLoad() {
		
		logo.setUrl("images/appiriologo.png");
		
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
			}
		
			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if(loginInfo.isLoggedIn()) {
					loadMainUI();
				} else {
					loadLoginUI();
				}
			}
		});
		
	}
	
	private void loadLoginUI() {
		VerticalPanel loginPanel = new VerticalPanel();
		Anchor loginLink = new Anchor("Sign In");
		loginLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(logo);
		loginPanel.add(new Label("Please sign in with your Google Account to access the Time Entry application."));
		loginPanel.add(loginLink);
		RootPanel.get("timeentryUI").add(loginPanel);
	}
	
	private void loadMainUI() {

		DateBox dateBox = new DateBox();
		Button addRowButton = new Button("Add Row");
		Button saveButton = new Button("Save");
				
		HorizontalPanel userPanel = new HorizontalPanel();
		Anchor logOutLink = new Anchor("Sign Out");
		logOutLink.setHref(loginInfo.getLogoutUrl());
		Label separator = new Label("|");
		separator.setStyleName("separator"); 
		userPanel.add(new Label(loginInfo.getEmailAddress()));
		userPanel.add(separator);
		userPanel.add(logOutLink);
		
		topPanel.setWidth("1000px");
		topPanel.add(logo);
		topPanel.add(userPanel);
		topPanel.setCellHorizontalAlignment(userPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		
		// set up a horizontal panel to hold the date picker
		HorizontalPanel leftNav = new HorizontalPanel();
		leftNav.setSpacing(5);
		leftNav.add(new Label("Week Start Date"));
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
				saveEntries();
				removeAllRows();
				getCurrentEntries();
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
		flexEntryTable.setWidth("100%");

		// set the style for the table to be accessed in the css
		flexEntryTable.setStylePrimaryName("timeEntryTable");
		// add the columns and headers
		flexEntryTable.setText(0, 0, "Project");
		flexEntryTable.setText(0, 1, "Milestone");
		flexEntryTable.setText(0, 2, "Billable?");
		flexEntryTable.setText(0, 3, "Mon");
		flexEntryTable.setText(0, 4, "Tue");
		flexEntryTable.setText(0, 5, "Wed");
		flexEntryTable.setText(0, 6, "Thu");
		flexEntryTable.setText(0, 7, "Fri");
		flexEntryTable.setText(0, 8, "Sat");
		flexEntryTable.setText(0, 9, "Sun");
		flexEntryTable.setText(0, 10, "Total");
		
		// set the style for the table to be accessed in the css
		flexCurrentTable.setStylePrimaryName("existingEntryTable");
		// add the columns and headers
		flexCurrentTable.setText(0, 0, "Project");
		flexCurrentTable.setText(0, 1, "Milestone");
		flexCurrentTable.setText(0, 2, "Billable?");
		flexCurrentTable.setText(0, 3, "Date");
		flexCurrentTable.setText(0, 4, "Time");
		
		VerticalPanel tab1Content = new VerticalPanel();
		tab1Content.add(navPanel);
		tab1Content.add(flexEntryTable);
		tab1Content.add(totalPanel);
		
		DecoratedTabPanel tabPanel = new DecoratedTabPanel();
	    tabPanel.setWidth("100%");
	    tabPanel.setAnimationEnabled(true);
	    tabPanel.add(tab1Content, "Enter Time");
	    
	    tabPanel.add(flexCurrentTable,"Current Entries");
	    tabPanel.add(new HTML("<p>This sample application was built for the book, <i>Beginning Java on Google App Engine</i> by Kyle Roche and Jeff Douglas.</p><p>Please see [INSERT BOOK SITE URL] for more information.</p>"),"About");
	    tabPanel.selectTab(0);
	    
		// add the navpanel and flex table to the main panel
		mainPanel.add(topPanel);
	    mainPanel.add(tabPanel);
	    
	    // get the current entries for the user
		getCurrentEntries();
		
		// add a blank input row initially
		addRow();

	    // associate the main panel with the HTML host page.
	    RootPanel.get("timeentryUI").add(mainPanel);
		
	}
	
	private void renameColumns() {
		flexEntryTable.setText(0, 3, formatDate(startDate));
		flexEntryTable.setText(0, 4, formatDate(addDays(startDate,1)));
		flexEntryTable.setText(0, 5, formatDate(addDays(startDate,2)));
		flexEntryTable.setText(0, 6, formatDate(addDays(startDate,3)));
		flexEntryTable.setText(0, 7, formatDate(addDays(startDate,4)));
		flexEntryTable.setText(0, 8, formatDate(addDays(startDate,5)));
		flexEntryTable.setText(0, 9, formatDate(addDays(startDate,6)));
	}
	
	private void getCurrentEntries() {
		
		// get all of the milestones for the project
    	dataService.getEntries(new AsyncCallback<Vector<TimeEntryData>>() {

            public void onFailure(Throwable caught) {
            	handleError(caught);
            }

            public void onSuccess(Vector<TimeEntryData> entries) {
            	int row = flexEntryTable.getRowCount();
            	for (TimeEntryData ted : entries) {
            		row++;
            		flexCurrentTable.setText(row, 0, ted.getProject());
            		flexCurrentTable.setText(row, 1, ted.getMilestone());
            		flexCurrentTable.setText(row, 2, ted.getBillable() ? "Yes":"No");
            		flexCurrentTable.setText(row, 3, DateTimeFormat.getShortDateFormat().format(ted.getDate()));
            		flexCurrentTable.setText(row, 4, String.valueOf(NumberFormat.getFormat(".00").format(ted.getHours())));
            	}
            }
        });
		
	}
	
	private void addRow() {
		
		int row = flexEntryTable.getRowCount();
		
		final ListBox lbMilestones = new ListBox(false);
		final ListBox lbProjects = new ListBox(false);
		lbProjects.addItem("-- Select a Project --");
		
		lbProjects.addChangeHandler(new ChangeHandler () {
			public void onChange(ChangeEvent event) {
				
				// remove all of the current items in the milestone list			
				for (int i=lbMilestones.getItemCount()-1;i>=0;i--) 			
					lbMilestones.removeItem(i);
				
				// get all of the milestones for the project
		    	dataService.getMilestones(lbProjects.getItemText(lbProjects.getSelectedIndex()), new AsyncCallback<String[]>() {

		            public void onFailure(Throwable caught) {
		            	handleError(caught);
		            }

		            public void onSuccess(String[] results) {
		        		for (int i=0;i<results.length;i++) 
		        			lbMilestones.addItem(results[i]);
		            }
		        });
				
			}
		});
		
		// get all of the projects for the user
    	dataService.getProjects(new AsyncCallback<String[]>() {

            public void onFailure(Throwable caught) {
            	handleError(caught);
            }

            public void onSuccess(String[] results) {
        		for (int i=0;i<results.length;i++) 
        			lbProjects.addItem(results[i]);
            }
        });
		
		// create the time input fields for all 7 days
		TextBox day1 = new TextBox();
		day1.setValue("0");
		day1.setWidth("50px");
		TextBox day2 = new TextBox();
		day2.setValue("0");
		day2.setWidth("50px");
		TextBox day3 = new TextBox();
		day3.setValue("0");
		day3.setWidth("50px");
		TextBox day4 = new TextBox();
		day4.setValue("0");
		day4.setWidth("50px");
		TextBox day5 = new TextBox();
		day5.setValue("0");
		day5.setWidth("50px");
		TextBox day6 = new TextBox();
		day6.setValue("0");
		day6.setWidth("50px");
		TextBox day7 = new TextBox();
		day7.setValue("0");
		day7.setWidth("50px");
		
		// add all of the widgets to the flex table
		flexEntryTable.setWidget(row, 0, lbProjects);
		flexEntryTable.setWidget(row, 1, lbMilestones);
		flexEntryTable.setWidget(row, 2, new CheckBox());
		flexEntryTable.setWidget(row, 3, day1);
		flexEntryTable.setWidget(row, 4, day2);
		flexEntryTable.setWidget(row, 5, day3);
		flexEntryTable.setWidget(row, 6, day4);
		flexEntryTable.setWidget(row, 7, day5);
		flexEntryTable.setWidget(row, 8, day6);
		flexEntryTable.setWidget(row, 9, day7);
		flexEntryTable.setWidget(row, 10, new Label("0.00"));
		
		flexEntryTable.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				HTMLTable.Cell cellForEvent = flexEntryTable.getCellForEvent(event);
				currentRow = cellForEvent.getRowIndex();
				currentColumn = cellForEvent.getCellIndex();
			}
		});
		
		day1.addValueChangeHandler(timeChangeHandler);
		day2.addValueChangeHandler(timeChangeHandler);
		day3.addValueChangeHandler(timeChangeHandler);
		day4.addValueChangeHandler(timeChangeHandler);
		day5.addValueChangeHandler(timeChangeHandler);
		day6.addValueChangeHandler(timeChangeHandler);
		day7.addValueChangeHandler(timeChangeHandler);
		
	}
	
	private ValueChangeHandler<String> timeChangeHandler = new ValueChangeHandler<String>() {
		public void onValueChange(ValueChangeEvent<String> evt) {
			
			try {
				double t = Double.parseDouble(evt.getValue());
				if (t > 24) 
					Window.alert("You cannot work more than 24 hours a day.");
				totalRow();
			} catch (NumberFormatException e) {
				TextBox tb = (TextBox) flexEntryTable.getWidget(currentRow, currentColumn);
				tb.setValue("0");
				flexEntryTable.setWidget(currentRow, currentColumn, tb);
				Window.alert("Not a valid number.");
			}
			
		}
	};
	
	private void saveEntries() {
		
		Vector<TimeEntryData> entries = new Vector<TimeEntryData>(); 
		
		for (int row=1;row<flexEntryTable.getRowCount();row++) {

			ListBox projectWidget = (ListBox) flexEntryTable.getWidget(row, 0);
			ListBox milestoneWidget = (ListBox) flexEntryTable.getWidget(row, 1);
			CheckBox billableWidget = (CheckBox) flexEntryTable.getWidget(row, 2);
			
			for (int column=3;column<10;column++) {
				// get the current text box for the day
				TextBox textBox = (TextBox) flexEntryTable.getWidget(row, column);
				double hours = Double.parseDouble(textBox.getValue());
				if (hours > 0) {
					TimeEntryData ted = new TimeEntryData();
					ted.setHours(hours);
					ted.setMilestone(milestoneWidget.getItemText(milestoneWidget.getSelectedIndex()));
					ted.setProject(projectWidget.getItemText(projectWidget.getSelectedIndex()));
					ted.setBillable(billableWidget.getValue());
					ted.setDate(addDays(startDate,(column-3)));
					entries.add(ted);
				}
			}

		}
		
		if (!entries.isEmpty()) {
			
			// get all of the milestones for the project
	    	dataService.addEntries(entries, new AsyncCallback<String>() {

	            public void onFailure(Throwable caught) {
	            	handleError(caught);
	            }

	            public void onSuccess(String message) {
	            	Window.alert(message);
	            	getCurrentEntries();
	            }
	        });
	    	
		}
		
	}
	
	private void totalRow() {
		double rowTotal = 0.00;
		for (int cell = 3;cell<=9; cell++) {
			TextBox timeWidget = (TextBox) flexEntryTable.getWidget(currentRow, cell);
			double t = Double.parseDouble(timeWidget.getValue());
			rowTotal = rowTotal + t;
		}
		flexEntryTable.setWidget(currentRow, 10, new Label(NumberFormat.getFormat(".00").format(rowTotal)));
		totalGrid();
	}
	
	private void totalGrid() {
		double grandTotal = 0.00;
		for (int row=1;row<flexEntryTable.getRowCount();row++) {
			Label rowTotalWidget = (Label) flexEntryTable.getWidget(row, 10);
			double rowTotal = Double.parseDouble(rowTotalWidget.getText());
			grandTotal = grandTotal + rowTotal;
		}
		;
		totalLabel.setText(NumberFormat.getFormat(".00").format(grandTotal));
	}
	
	private void removeAllRows() {
		// remove all of the rows from the flex table
		for (int row=flexEntryTable.getRowCount()-1;row>0;row--)
			flexEntryTable.removeRow(row);
		
		// rest the total
		totalLabel.setText("0.00");
		// add a new blank row to the flex table
		addRow();
	}
	
	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
		if (error instanceof NotLoggedInException)
			Window.Location.replace(loginInfo.getLogoutUrl());
		
	}
	
	private Date addDays(Date d, int numberOfDays) {
		int day = d.getDate();
		int month = d.getMonth();
		int year = d.getYear();
		return new Date(year, month, day+numberOfDays);
	}
	
	private String formatDate(Date d) {
		return (d.getMonth()+1)+"/"+d.getDate()+" ("+d.toString().substring(0, 2)+")";
	}
	
}
