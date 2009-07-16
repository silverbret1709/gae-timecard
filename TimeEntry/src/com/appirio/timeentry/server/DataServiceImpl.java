package com.appirio.timeentry.server;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.JDOHelper;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.appirio.timeentry.client.NotLoggedInException;

import com.appirio.timeentry.client.DataService;
import com.appirio.timeentry.client.TimeEntryData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DataServiceImpl extends RemoteServiceServlet implements
		DataService { 
	
	private static final Logger LOG = Logger.getLogger(DataServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF =JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	public String addEntries(Vector<TimeEntryData> entries) throws NotLoggedInException {
		
		// ensure the current user is logged in
		checkLoggedIn();

		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistentAll(toEntities(entries));
		} finally {
			pm.close();
		}
		LOG.log(Level.INFO, entries.size()+" entries added.");
		return entries.size()+" entries added.";
	}
	
	public Vector<TimeEntryData> getEntries() throws NotLoggedInException {
			
		// ensure the current user is logged in
		checkLoggedIn();
		
		Vector<TimeEntryData> entries = new Vector<TimeEntryData>();
		
		PersistenceManager pm = getPersistenceManager();
		try {
			//String query = "select from " + TimeEntryEntity.class.getName() + " where user == '"+ getUser().getEmail() +"' order by date desc";
			String query = "select from " + TimeEntryEntity.class.getName() + " order by date desc";
			List<TimeEntryEntity> entities = (List<TimeEntryEntity>) pm.newQuery(query).execute();	
			
			for (TimeEntryEntity entity : entities) {
				System.out.println("user: "+entity.getUser());
				TimeEntryData ted = new TimeEntryData();
				ted.setBillable(entity.getBillable());
				ted.setDate(entity.getDate());
				ted.setHours(entity.getHours());
				ted.setMilestone(entity.getMilestone());
				ted.setProject(entity.getProject());
				entries.add(ted);
			}
			
		} finally {
			pm.close();
		}
		
		return entries;		
	}
	
	public String[] getProjects() {
		
		String[] projects = new String[3];
		projects[0] = "Project 1";
		projects[1] = "Project 2";
		projects[2] = "Project 3";
		
		return projects;
	}
	
	public String[] getMilestones(String project) {
		
		String[] milestones = new String[3];
		
		if (project.equals("Project 1")) {
			milestones[0] = "Milestone 1-1";
			milestones[1] = "Milestone 1-2";
			milestones[2] = "Milestone 1-3";	
		} else if (project.equals("Project 2")) {
			milestones[0] = "Milestone 2-1";
			milestones[1] = "Milestone 2-2";
			milestones[2] = "Milestone 2-3";	
		} else {
			milestones[0] = "Milestone 3-1";
			milestones[1] = "Milestone 3-2";
			milestones[2] = "Milestone 3-3";	
		}
		
		return milestones;
	}
	
	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}	
	
	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
	
	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) 
			throw new NotLoggedInException("User not logged in. Please login with your Google Account credentials.");
	}
	
	private Vector<TimeEntryEntity> toEntities(Vector<TimeEntryData> entries) {
		// create a new vector of entities to return
		Vector<TimeEntryEntity> entities = new Vector<TimeEntryEntity>();
		for (int i=0;i<entries.size();i++) {
			TimeEntryData ted = (TimeEntryData) entries.get(i);
			TimeEntryEntity tee = new TimeEntryEntity();
			tee.setBillable(ted.getBillable());
			tee.setDate(ted.getDate());
			tee.setHours(ted.getHours());
			tee.setMilestone(ted.getMilestone());
			tee.setProject(ted.getProject());
			tee.setUser(getUser());
			entities.add(tee);
		}
		return entities;
	}
	
}
