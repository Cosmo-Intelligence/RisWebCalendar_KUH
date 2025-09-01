package com.yokogawa.radiquest.ris.bean;

public class Byoutou {
	private String byoutouID = "";
	private String byoutouName = "";
	private String byoutouRyakuName = "";

	public Byoutou() {
	}

	public Byoutou(String byoutouID, String byoutouName, String byoutouRyakuName) {
		this.byoutouID = byoutouID;
		this.byoutouName = byoutouName;
		this.byoutouRyakuName = byoutouRyakuName;
	}

	public String getByoutouID() {
		return byoutouID;
	}

	public void setByoutouID(String byoutouID) {
		this.byoutouID = byoutouID;
	}

	public String getByoutouName() {
		return byoutouName;
	}

	public void setByoutouName(String byoutouName) {
		this.byoutouName = byoutouName;
	}

	public String getByoutouRyakuName() {
		return byoutouRyakuName;
	}

	public void setByoutouRyakuName(String byoutouRyakuName) {
		this.byoutouRyakuName = byoutouRyakuName;
	}

	@Override
	public String toString() {
		return "Byoutou [byoutouID=" + byoutouID + ", byoutouName="
				+ byoutouName + ", byoutouRyakuName=" + byoutouRyakuName + "]";
	}
}
