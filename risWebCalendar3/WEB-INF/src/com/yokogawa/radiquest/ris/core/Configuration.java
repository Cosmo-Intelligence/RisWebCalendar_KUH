package com.yokogawa.radiquest.ris.core;

import java.util.Collections;
import java.util.List;
import com.yokogawa.radiquest.ris.DBAccess.SystemParam2DataHome;

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
	
	// 2014.05.07 Add T.Koudate@COSMO Start #2681
	/**
	 * RI区分拡張フラグ
	 */
	private String extendRiOrderFlg = "0";
	// 2014.05.07 Add T.Koudate@COSMO End   #2681

	// 2016.08.08 Add T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
	/**
	 * RIオーダフラグ一覧
	 * RI区分拡張フラグを元に設定する。
	 */
	private String[] riOrderFlgList = new String[]{""};
	// 2016.08.08 Add T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する

	// 2013.10.29 Add T.Koudate@Cosmo Start #1694
	/**
	 * 休診日モード
	 */
	private String holidayMode = "0";
	// 2013.10.29 Add T.Koudate@Cosmo End   #1694

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
	// 2012.03.06 Add Yk.Suzuki@CIJ Start SSGH-4-003

	// 2013.10.29 Add T.Koudate@Cosmo Start #1694
	/**
	 * 休診日モード 参照
	 * @return 休診日モード
	 */
	public String getHolidayMode() {
		return holidayMode;
	}

	/**
	 * 休診日モード 設定
	 * @param holidayMode 休診日モード
	 */
	public void setHolidayMode(String holidayMode) {
		this.holidayMode = holidayMode;
	}
	// 2013.10.29 Add T.Koudate@Cosmo End   #1694


	/**
	 * カレンダー区切り文字
	 */
	private String calseparator = "";

	public String getCalseparator()
	{
		return calseparator;
	}
	public void setCalseparator(String calseparator)
	{
		this.calseparator=calseparator;
	}
	// 2012.03.06 Add Yk.Suzuki@CIJ End   SSGH-4-003

	// 2013.11.19 Add Yk.Suzuki@CIJ Start NCC_C-2-TKC-R005
	/**
	 * オーダ件数 単位文字
	 */
	private String orderCountUnit = "";
	/**
	 * オーダ件数 単位文字 設定
	 * @return オーダ件数 単位文字
	 */
	public String getOrderCountUnit()
	{
		return orderCountUnit;
	}
	/**
	 * オーダ件数 単位文字 参照
	 * @param orderCountUnit オーダ件数 単位文字
	 */
	public void setOrderCountUnit(String orderCountUnit)
	{
		this.orderCountUnit=orderCountUnit;
	}
	// 2013.11.19 Add Yk.Suzuki@CIJ End   NCC_C-2-TKC-R005

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

	// 2014.05.07 Add T.Koudate@COSMO Start #2681
	/**
	 * RI区分拡張フラグ 参照
	 * @return RI区分拡張フラグ
	 */
	public String getExtendRiOrderFlg() {
		return extendRiOrderFlg;
	}

	/**
	 * RI区分拡張フラグ 設定
	 * @param extendRiOrderFlg RI区分拡張フラグ
	 */
	public void setExtendRiOrderFlg(String extendRiOrderFlg) {
		this.extendRiOrderFlg = extendRiOrderFlg;
	}

	// 2016.08.08 Del T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
//	/**
//	 * RI区分最大数.
//	 * SystemParam2の設定に従い、RI区分の設定可能最大数を返す
//	 */
//	public int getExtendRiOrderMaxLengh(){
//		String extendRiOrderFlg = Configuration.getInstance().getExtendRiOrderFlg();
//		int maxNum = 3;
//		if (SystemParam2DataHome.EXTEND_RI_ORDER_FLG_FOLLOW.equals(extendRiOrderFlg)){
//			maxNum = 4;
//		}
//		else if (SystemParam2DataHome.EXTEND_RI_ORDER_FLG_NEEDLE_INSPECT.equals(extendRiOrderFlg)) {
//			maxNum = 5;
//		}
//		return maxNum;
//	}
	// 2016.08.08 Del T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する
	// 2014.05.07 Add T.Koudate@COSMO End   #2681

	// 2016.08.08 Add T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
	/**
	 * RIオーダフラグ一覧 参照
	 * @return RIオーダフラグ一覧
	 */
	public String[] getRiOrderFlgList() {
		return riOrderFlgList;
	}

	/**
	 * RIオーダフラグ一覧 設定
	 * @param riOrderFlgList RIオーダフラグ一覧
	 */
	public void setRiOrderFlgList(String[] riOrderFlgList) {
		this.riOrderFlgList = riOrderFlgList;
	}
	// 2016.08.08 Add T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する
}
