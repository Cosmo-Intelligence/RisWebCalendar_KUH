package com.yokogawa.radiquest.ris.bean;

import java.util.Date;

public class AccessInfo {
	private String id;

	private String appID;

	private String ipAddress;

	private Date entryTime;

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public Date getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getIPAddress() {
		return ipAddress;
	}

	public void setIPAddress(String address) {
		ipAddress = address;
	}
}
