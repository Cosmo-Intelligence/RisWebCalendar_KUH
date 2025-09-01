package com.yokogawa.radiquest.ris.bean;

public class OrderCalendarItem {
	private String risID = "";

	private String kanjiSimei = "";

	private String kensaDateAge = "";

	private String kensaTypeRyakuName = "";

	private int kensaDate;

	private int kensaStartTime = -1;

	private String riOrderFlg = "";

	private String riOrder = "";

	private String buiRyakuName = "";

	private String kensaHouhouRyakuName = "";

	private String sayuuRyakuName = "";

	private String houkouRyakuName = "";

	private String kensaSituName = "";

	private String sectionRyakuName = "";

	private String byoutouRyakuName = "";

	private String status = "";

	private String buiSet = "";

	// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
	private String kensaSituTitle = "";
	private String kensaSituID = "";
	private int kensaSituShowOrder = -1;
	// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
	// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-003 入外情報追加
	private String nyuGaiInfo = "";
	// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-003 入外情報追加
	// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
	private String rcDate = "";
	// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更

	// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
	private String kensatypeID = "";
	// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更





	public String getRisID() {
		return risID;
	}

	public void setRisID(String risID) {
		this.risID = risID;
	}

	public String getKanjiSimei() {
		return kanjiSimei;
	}

	public void setKanjiSimei(String kanjiSimei) {
		this.kanjiSimei = kanjiSimei;
	}

	public String getKensaDateAge() {
		return kensaDateAge;
	}

	public void setKensaDateAge(String kensaDateAge) {
		this.kensaDateAge = kensaDateAge;
	}

	public String getKensaTypeRyakuName() {
		return kensaTypeRyakuName;
	}

	public void setKensaTypeRyakuName(String kensaTypeRyakuName) {
		this.kensaTypeRyakuName = kensaTypeRyakuName;
	}

	public int getKensaDate() {
		return kensaDate;
	}

	public void setKensaDate(int kensaDate) {
		this.kensaDate = kensaDate;
	}

	public int getKensaStartTime() {
		return kensaStartTime;
	}

	public void setKensaStartTime(int kensaStartTime) {
		this.kensaStartTime = kensaStartTime;
	}

	public String getRiOrderFlg() {
		return riOrderFlg;
	}

	public void setRiOrderFlg(String riOrderFlg) {
		this.riOrderFlg = riOrderFlg;
	}

	public String getRiOrder() {
		return riOrder;
	}

	public void setRiOrder(String riOrder) {
		this.riOrder = riOrder;
	}

	public String getBuiRyakuName() {
		return buiRyakuName;
	}

	public void setBuiRyakuName(String buiRyakuName) {
		this.buiRyakuName = buiRyakuName;
	}

	public String getKensaHouhouRyakuName() {
		return kensaHouhouRyakuName;
	}

	public void setKensaHouhouRyakuName(String kensaHouhouRyakuName) {
		this.kensaHouhouRyakuName = kensaHouhouRyakuName;
	}

	public String getSayuuRyakuName() {
		return sayuuRyakuName;
	}

	public void setSayuuRyakuName(String sayuuRyakuName) {
		this.sayuuRyakuName = sayuuRyakuName;
	}

	public String getHoukouRyakuName() {
		return houkouRyakuName;
	}

	public void setHoukouRyakuName(String houkouRyakuName) {
		this.houkouRyakuName = houkouRyakuName;
	}

	public String getKensaSituName() {
		return kensaSituName;
	}

	public void setKensaSituName(String kensaSituName) {
		this.kensaSituName = kensaSituName;
	}

	public String getSectionRyakuName() {
		return sectionRyakuName;
	}

	public void setSectionRyakuName(String sectionRyakuName) {
		this.sectionRyakuName = sectionRyakuName;
	}

	public String getByoutouRyakuName() {
		return byoutouRyakuName;
	}

	public void setByoutouRyakuName(String byoutouRyakuName) {
		this.byoutouRyakuName = byoutouRyakuName;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public String getBuiSet() {
		return buiSet;
	}

	public void setBuiSet(String buiSet) {
		this.buiSet = buiSet;
	}

	// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
	public String getKensaSituTitle() {
		return kensaSituTitle;
	}
	public void setKensaSituTitle(String kensaSituTitle) {
		this.kensaSituTitle = kensaSituTitle;
	}

	public String getKensaSituID() {
		return kensaSituID;
	}
	public void setKensaSituID(String kensaSituID) {
		this.kensaSituID = kensaSituID;
	}

	public int getKensaSituShowOrder() {
		return kensaSituShowOrder;
	}
	public void setKensaSituShowOrder(int kensaSituShowOrder) {
		this.kensaSituShowOrder = kensaSituShowOrder;
	}
	// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更

	// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-003 入外情報追加
	public String getNyuGaiInfo() {
		return nyuGaiInfo;
	}

	public void setNyuGaiInfo(String nyuGaiInfo) {
		this.nyuGaiInfo = nyuGaiInfo;
	}
	// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-003 入外情報追加

	// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
	public String getRcDate() {
		return rcDate;
	}
	public void setRcDate(String rcDate) {
		this.rcDate = rcDate;
	}
	// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更

	// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
	public String getKensatypeID() {
		return kensatypeID;
	}
	public void setKensatypeID(String kensatypeID) {
		this.kensatypeID = kensatypeID;
	}
	// 2016.12.20 Add T.onoda@CosmoEnd KUMA205-3-CAL004 表示項目の変更
}
