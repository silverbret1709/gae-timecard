package com.appirio.timeentry.client;

import java.io.Serializable;
import java.util.Date;

public class TimeEntryData implements Serializable {

	private String project;
	private String milestone;
	private Boolean billable;
	private Date date;
	private double hours;
	
	/**
	 * @return the project
	 */
	public String getProject() {
		return project;
	}
	/**
	 * @param project the project to set
	 */
	public void setProject(String project) {
		this.project = project;
	}
	/**
	 * @return the milestone
	 */
	public String getMilestone() {
		return milestone;
	}
	/**
	 * @param milestone the milestone to set
	 */
	public void setMilestone(String milestone) {
		this.milestone = milestone;
	}
	/**
	 * @return the billable
	 */
	public Boolean getBillable() {
		return billable;
	}
	/**
	 * @param billable the billable to set
	 */
	public void setBillable(Boolean billable) {
		this.billable = billable;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the hours
	 */
	public double getHours() {
		return hours;
	}
	/**
	 * @param hours the hours to set
	 */
	public void setHours(double hours) {
		this.hours = hours;
	}
	
}
