package com.yokogawa.radiquest.ris.bean;

public class Bui {
	private int no;
	private String buiSetID = "";
	private String buiSetName = "";

	public Bui() {
	}

	public Bui(int no, String buiSetID, String buiSetName) {
		this.no = no;
		this.buiSetID = buiSetID;
		this.buiSetName = buiSetName;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getBuiSetID() {
		return buiSetID;
	}

	public void setBuiSetID(String buiSetID) {
		this.buiSetID = buiSetID;
	}

	public String getBuiSetName() {
		return buiSetName;
	}

	public void setBuiSetName(String buiSetName) {
		this.buiSetName = buiSetName;
	}
}
