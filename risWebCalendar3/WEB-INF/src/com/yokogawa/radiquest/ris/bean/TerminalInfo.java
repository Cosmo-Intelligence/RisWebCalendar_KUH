package com.yokogawa.radiquest.ris.bean;

import java.util.Date;
//2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
public class TerminalInfo {


	private int terminalID;

	private String terminalName;

	private String terminalipAddress;

	private Date entryDate;

	private String exPlanation;

	private int attriBute;

	private int showOrder;

	private String kakuhoRoom;


	public int getterminalID() {
		return terminalID;
	}

	public void setterminalID(int terminalID) {
		this.terminalID = terminalID;
	}

	public String getterminalName() {
		return terminalName;
	}

	public void setterminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	public String geterminalipAddress() {
		return terminalipAddress;
	}

	public void setterminalipAddress(String terminalipAddress) {
		this.terminalipAddress = terminalipAddress;
	}

	public Date getentryDate() {
		return entryDate;
	}

	public void setentryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String geexPlanation() {
		return exPlanation;
	}

	public void setexPlanation(String exPlanation) {
		this.exPlanation = exPlanation;
	}

	public int getattriBute() {
		return attriBute;
	}

	public void setattriBute(int attriBute) {
		this.attriBute = attriBute;
	}

	public int getshowOrder() {
		return showOrder;
	}

	public void setshowOrder(int showOrder) {
		this.showOrder = showOrder;
	}

	public String getkakuhoRoom() {
		return kakuhoRoom;
	}

	public void setkakuhoRoom(String kakuhoRoom) {
		this.kakuhoRoom = kakuhoRoom;
	}
}
//2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
