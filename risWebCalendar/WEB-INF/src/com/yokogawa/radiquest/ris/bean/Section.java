package com.yokogawa.radiquest.ris.bean;

public class Section {
	private String sectionID = "";
	private String sectionName = "";
	private String sectionRyakuName = "";

	public Section() {
	}

	public Section(String sectionID, String sectionName, String sectionRyakuName) {
		this.sectionID = sectionID;
		this.sectionName = sectionName;
		this.sectionRyakuName = sectionRyakuName;
	}

	public String getSectionID() {
		return sectionID;
	}

	public void setSectionID(String sectionID) {
		this.sectionID = sectionID;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getSectionRyakuName() {
		return sectionRyakuName;
	}

	public void setSectionRyakuName(String sectionRyakuName) {
		this.sectionRyakuName = sectionRyakuName;
	}

	@Override
	public String toString() {
		return "Section [sectionID=" + sectionID + ", sectionName="
				+ sectionName + ", sectionRyakuName=" + sectionRyakuName + "]";
	}
}
