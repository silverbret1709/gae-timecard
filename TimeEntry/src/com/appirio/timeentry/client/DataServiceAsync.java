package com.appirio.timeentry.client;

import java.util.Vector;

import com.appirio.timeentry.client.TimeEntryData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataServiceAsync {
	void getProjects(AsyncCallback<String[]> callback);
	void getMilestones(String project, AsyncCallback<String[]> callback);
	void addEntries(Vector<TimeEntryData> entries, AsyncCallback<String> callback);
	void getEntries(AsyncCallback<Vector<TimeEntryData>> callback);
}