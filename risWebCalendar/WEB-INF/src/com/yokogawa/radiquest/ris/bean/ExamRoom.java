package com.yokogawa.radiquest.ris.bean;

public class ExamRoom {
	// 検査室ID
	private String examRoomID = "";

	// 検査室名称
	private String examRoomName = "";

	// 検査室略称
	private String examRoomRyakuName = "";

	public ExamRoom() {
	}

	public ExamRoom(String examRoomID, String examRoomName,
			String examRoomRyakuName) {
		this.examRoomID = examRoomID;
		this.examRoomName = examRoomName;
		this.examRoomRyakuName = examRoomRyakuName;
	}

	public String getExamRoomID() {
		return examRoomID;
	}

	public void setExamRoomID(String examRoomID) {
		this.examRoomID = examRoomID;
	}

	public String getExamRoomName() {
		return examRoomName;
	}

	public void setExamRoomName(String examRoomName) {
		this.examRoomName = examRoomName;
	}

	public String getExamRoomRyakuName() {
		return examRoomRyakuName;
	}

	public void setExamRoomRyakuName(String examRoomRyakuName) {
		this.examRoomRyakuName = examRoomRyakuName;
	}

}
