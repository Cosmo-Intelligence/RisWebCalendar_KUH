package com.yokogawa.radiquest.ris.bean;

public class DayClassification {
	private int dayOfWeek;
	private int week01 = 9;
	private int week02 = 9;
	private int week03 = 9;
	private int week04 = 9;
	private int week05 = 9;
	private int week06 = 9;

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getWeek01() {
		return week01;
	}

	public void setWeek01(int week01) {
		this.week01 = week01;
	}

	public int getWeek02() {
		return week02;
	}

	public void setWeek02(int week02) {
		this.week02 = week02;
	}

	public int getWeek03() {
		return week03;
	}

	public void setWeek03(int week03) {
		this.week03 = week03;
	}

	public int getWeek04() {
		return week04;
	}

	public void setWeek04(int week04) {
		this.week04 = week04;
	}

	public int getWeek05() {
		return week05;
	}

	public void setWeek05(int week05) {
		this.week05 = week05;
	}

	public int getWeek06() {
		return week06;
	}

	public void setWeek06(int week06) {
		this.week06 = week06;
	}

	@Override
	public String toString() {
		return "DayClassification [dayOfWeek=" + dayOfWeek + ", week01="
				+ week01 + ", week02=" + week02 + ", week03=" + week03
				+ ", week04=" + week04 + ", week05=" + week05 + ", week06="
				+ week06 + "]";
	}
}
