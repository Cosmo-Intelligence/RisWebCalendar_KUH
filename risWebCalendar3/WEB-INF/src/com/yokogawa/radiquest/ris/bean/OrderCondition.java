package com.yokogawa.radiquest.ris.bean;

import java.util.ArrayList;

/**
 * 表示条件操作部 表示用データ
 * 
 */
public class OrderCondition {
	// 検査種別
	public ArrayList kensaTypeList = new ArrayList();

	// 検査室
	public ArrayList examRoomList = new ArrayList();

	public ArrayList getKensaTypeList() {
		return kensaTypeList;
	}

	public void setKensaTypeList(ArrayList kensaTypeList) {
		this.kensaTypeList = kensaTypeList;
	}

	public ArrayList getExamRoomList() {
		return examRoomList;
	}

	public void setExamRoomList(ArrayList examRoomList) {
		this.examRoomList = examRoomList;
	}

}
