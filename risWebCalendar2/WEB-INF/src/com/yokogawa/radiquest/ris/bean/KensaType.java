package com.yokogawa.radiquest.ris.bean;

public class KensaType {
	// 検査種別ID
	private String kensaTypeID = "";

	// 検査種別名称
	private String kensaTypeName = "";

	// 検査種別略称
	private String kensaTypeRyakuName = "";

	public KensaType() {
	}

	public KensaType(String kensaTypeID, String kensaTypeName,
			String kensaTypeRyakuName) {
		this.kensaTypeID = kensaTypeID;
		this.kensaTypeName = kensaTypeName;
		this.kensaTypeRyakuName = kensaTypeRyakuName;
	}

	public String getKensaTypeID() {
		return kensaTypeID;
	}

	public void setKensaTypeID(String kensaTypeID) {
		this.kensaTypeID = kensaTypeID;
	}

	public String getKensaTypeName() {
		return kensaTypeName;
	}

	public void setKensaTypeName(String kensaTypeName) {
		this.kensaTypeName = kensaTypeName;
	}

	public String getKensaTypeRyakuName() {
		return kensaTypeRyakuName;
	}

	public void setKensaTypeRyakuName(String kensaTypeRyakuName) {
		this.kensaTypeRyakuName = kensaTypeRyakuName;
	}
}
