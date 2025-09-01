package com.yokogawa.radiquest.ris.servlet;

/**
 * アクション定義クラス
 *
 * @author kuroyama
 */
public class ActionType {
	public static final String Parameter = "action";

	public static final String CALENDAR = "calendar";

	public static final String ORDER_DETAIL = "orderDetail";

	public static final String IRAI_DETAIL = "IraiDetail";

	public static final String CHECK_ORDER_EXIST = "checkOrderExist";

	public static final String PATIENT = "patient";

	public static final String KENSA_TYPE = "kensaType";

	public static final String KENSA_KIKI = "kensaKiki";

	public static final String PATTERN_ORDER = "patternOrder";

	public static final String DOCTOR = "doctor";

	public static final String EXAM_ROOM = "examRoom";

	public static final String PATTERN_BUNRUI = "patternBunrui";

	public static final String PATTERN_ORDER_BUNRUI = "patternOrderBunrui";

	public static final String PATTERN = "pattern";

	// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL003 検査室毎画面　縦表示
	public static final String KENSA_SITU_V = "kensasitu_v";

	public static final String KENSA_SITU_H = "kensasitu_h";

	public static final String SHOW = "_show";

	public static final String KENSA_STATUS = "status";

	public static final String ACCESS_INFO = "accessinfo";


	// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL003 検査室毎画面　縦表示
}