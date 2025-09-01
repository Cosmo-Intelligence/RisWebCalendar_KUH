package com.yokogawa.radiquest.ris.core;

import java.util.Collections;
import java.util.List;

/**
 * システム全体で利用される設定値を保持します。
 *
 * @author kuroyama
 */
public class Configuration {
	private static Configuration INSTANCE = new Configuration();

	/**
	 * 対象とする検査種別
	 */
	private List targetKensaTypes;

	/**
	 * オーダ詳細情報表示項目リスト
	 */
	private List orderDetailItems;

	/**
	 * 休診日を含める・含めないの設定
	 */
	private boolean displayNonconsultationDay;

	/**
	 * オーダ一覧の検査ステータスによる文字色変更の有無の設定
	 */
	private boolean orderDetailStatusColor;

	/**
	 * カレンダー枠の表示切替ON/OFF
	 */
	private boolean calendarDisplayChange;

	/**
	 * オーダ詳細情報表示項目
	 */
	public class OrderDetailItem{
		private int no = 0;
		private int showOrder = 0;
		private int width = 0;

		public int getNo() {
			return no;
		}
		public void setNo(int no) {
			this.no = no;
		}
		public int getShowOrder() {
			return showOrder;
		}
		public void setShowOrder(int showOrder) {
			this.showOrder = showOrder;
		}
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
	}

	/**
	 * カレンダー表示開始時刻
	 */
	private int startTime = 0;

	/**
	 * カレンダー表示終了時刻
	 */
	private int endTime = 0;

	/**
	 * カレンダー表示終了時刻
	 */
	private int dispPattern = 0;

	private String htmlPath = "";


	public String getHtmlPath() {
		return htmlPath;
	}

	public void setHtmlPath(String htmlPath) {
		this.htmlPath = htmlPath;
	}

	public int getDispPattern() {
		return dispPattern;
	}

	public void setDispPattern(int dispPattern) {
		this.dispPattern = dispPattern;
	}

	/**
	 * コンストラクタ
	 */
	private Configuration() {
	}

	/**
	 * 唯一のConfigurationオブジェクトを取得する。
	 *
	 * @return Configurationオブジェクト
	 */
	public static Configuration getInstance() {
		return INSTANCE;
	}

	/**
	 * オーダ詳細情報表示項目を取得する
	 *
	 * @return
	 */
	public List getOrderDetailItems() {
		if (orderDetailItems == null)
			return Collections.EMPTY_LIST;
		return orderDetailItems;
	}

	/**
	 * オーダ詳細情報表示項目を設定する
	 *
	 * @return orderDetailItems
	 */
	public void setOrderDetailItems(List orderDetailItems) {
		this.orderDetailItems = orderDetailItems;
	}

	/**
	 * カレンダー表示終了時刻を取得する。
	 *
	 * @return
	 */
	public int getEndTime() {
		return endTime;
	}

	/**
	 * カレンダー表示終了時刻を設定する。
	 *
	 * @param endTime
	 */
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	/**
	 * カレンダー表示開始時刻を取得する。
	 *
	 * @return
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * カレンダー表示開始時刻を設定する。
	 *
	 * @param startTime
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	/**
	 * 休診日を含める・含めないを取得する。
	 *
	 * @return trueで含める、falseで含めない。
	 */
	public boolean isDisplayNonconsultationDay() {
		return displayNonconsultationDay;
	}

	/**
	 * 休診日を含める・含めないを設定する。
	 *
	 * @param displayNonconsultationDay trueで含める、falseで含めない。
	 */
	public void setDisplayNonconsultationDay(boolean displayNonconsultationDay) {
		this.displayNonconsultationDay = displayNonconsultationDay;
	}

	/**
	 * カレンダー枠の表示切替ON/OFFを取得する。
	 *
	 * @return true:ON, false:OFF
	 */
	public boolean isCalendarDisplayChange() {
		return calendarDisplayChange;
	}

	/**
	 * カレンダー枠の表示切替ON/OFFを設定する。
	 *
	 * @param calendarDisplayChange true:ON, false:OFF
	 */
	public void setCalendarDisplayChange(boolean calendarDisplayChange) {
		this.calendarDisplayChange = calendarDisplayChange;
	}

	/**
	 * オーダ一覧の検査ステータスによる文字色変更の有無を取得する。
	 *
	 * @return true:ON, false:OFF
	 */
	public boolean isOrderDetailStatusColor() {
		return orderDetailStatusColor;
	}

	/**
	 * オーダ一覧の検査ステータスによる文字色変更の有無を設定する。
	 *
	 * @param orderDetailStatsuColor true:ON, false:OFF
	 */
	public void setOrderDetailStatusColor(boolean orderDetailStatsuColor) {
		this.orderDetailStatusColor = orderDetailStatsuColor;
	}
}
