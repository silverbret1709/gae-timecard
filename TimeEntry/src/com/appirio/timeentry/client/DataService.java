package com.appirio.timeentry.client;

import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.appirio.timeentry.client.TimeEntryData;

@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
	String[] getProjects();
	String[] getMilestones(String project);
	String addEntries(Vector<TimeEntryData> entries) throws NotLoggedInException;
	Vector<TimeEntryData> getEntries() throws NotLoggedInException;
}