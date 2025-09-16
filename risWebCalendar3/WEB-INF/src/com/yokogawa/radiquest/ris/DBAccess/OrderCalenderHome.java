package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.yokogawa.radiquest.ris.action.DateAction;
import com.yokogawa.radiquest.ris.bean.OrderCalendarItem;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;
import com.yokogawa.radiquest.ris.servlet.Parameters;

public class OrderCalenderHome extends AbstractDataHome {
	public static final String ALL_EXAM_ROOM_ID = Parameters.VALUE_EXAM_ROOM_ALL;

	// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-003 入外情報追加
	private static final String NYUGAI_KBN_NYU = "2";
	private static final String NYUGAI_KBN_GAI = "1";
	private static final String NYUGAI_GAI = "外来";
	// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-003 入外情報追加

	// 2016.08.08 Add T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
	public static final String RI_ORDER_FLG_NEEDLE			= "1";	//RI注射オーダ
	public static final String RI_ORDER_FLG_INSPECT			= "2";	//RI検査オーダ
	public static final String RI_ORDER_FLG_FOLLOW			= "3";	//RI追跡オーダ
	public static final String RI_ORDER_FLG_NEEDLE_INSPECT	= "4";	//RI注検オーダ
	public static final String RI_ORDER_FLG_OTHER			= "0";	//その他検査
	// 2016.08.08 Add T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する

	/**
	 * 検査種別マスタより検査種別リストを取得する(検査室毎表示)
	 *
	 * @return orderList
	 */
	public List getOrderRoomItems(int kensaStartTime, int startDate,
			String[] kensaTypes, String[] examRooms, String[] riOrders,
			String[] kensaStatus, String[] section, String[] byoutou,
			// 2019.09.02 Add H.Taira@COSMO Start KUMA205-10-001
			String[] patientByoutou,
			// 2019.09.02 Add H.Taira@COSMO End   KUMA205-10-001
			String[] kanjaNyugai, String[] denpyouNyugai)
			throws DBAccessException {
		List orderCalendarItems = new ArrayList();
		if (kensaTypes == null || kensaTypes.length == 0)
			return orderCalendarItems;

		DateAction dateAction = new DateAction();
		// 2015.02.27 Del S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
		//int endDate = dateAction.afterDate(startDate, 6);
		// 2015.02.27 Del S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		StringBuilder sql = new StringBuilder("SELECT "
						+ "RS.RIS_ID, "
						+ "RS.KANJISIMEI, "
						+ "RS.KENSA_DATE_AGE, "
						+ "RS.KENSATYPE_RYAKUNAME, "
						+ "RS.KENSA_DATE, "
						+ "RS.KENSA_STARTTIME, "
						+ "RS.RI_ORDER_FLG, "
						+ "CC.VALUELABEL, "
						+ "BSV.BUI_RYAKUNAME, "
						+ "BSV.KENSAHOUHOU_RYAKUNAME, "
						+ "BSV.SAYUU_RYAKUNAME, "
						+ "BSV.HOUKOU_RYAKUNAME, "
						+ "RS.YOTEIKENSASITU_RYAKUNAME, "
						+ "RS.JISSIKENSASITU_RYAKUNAME, "
						+ "BM.BYOUTOU_RYAKUNAME, "
						+ "SM.SECTION_RYAKUNAME, "
						+ "RS.KENSATYPE_ID, "
						+ "RS.STATUS "
						// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
						+ ",RS.YOTEIKENSASITU_NAME "
						+ ",RS.JISSIKENSASITU_NAME "
						+ ",RS.YOTEIKENSASITU_ID "
						+ ",RS.JISSIKENSASITU_ID "
						+ ",RS.YOTEIKENSASITU_SHOWORDER "
						+ ",RS.JISSIKENSASITU_SHOWORDER "
						// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
						// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-003 入外情報追加
						+ ",RS.KANJA_NYUGAIKBN "
						// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-003 入外情報追加
						// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
						// 2017.02.28 Mod M.Shinbo@Cosmo Start KUMA205-3-CAL004(#1) 来院情報の条件修正
						+ ",RS.PATIENTRCNO "
						//+ ",PR.RCDATE "
						// 2017.02.28 Mod M.Shinbo@Cosmo End KUMA205-3-CAL004(#1) 来院情報の条件修正
						// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更
						// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
						+ ",RS.KENSATYPE_ID "
						// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
					+ "FROM "
						+ " ((( RISCALENDARSUMMARYVIEW RS "
							+ "LEFT JOIN SECTIONMASTER SM "
								+ "ON RS.IRAI_SECTION_ID = SM.SECTION_ID) "
							+ "LEFT JOIN BYOUTOUMASTER BM "
								+ "ON RS.BYOUTOU_ID = BM.BYOUTOU_ID) "
							+ "LEFT JOIN CODECONVERT CC "
								+ "ON RS.RI_ORDER_FLG = CC.ITEMVALUE AND CC.ITEMID ='RIORDER') "
							+ "LEFT JOIN BUISUMMARYVIEW BSV "
								+ "ON RS.RIS_ID = BSV.RIS_ID AND BSV.NO = 1 "
							// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
							// 2017.02.28 Del M.Shinbo@Cosmo Start KUMA205-3-CAL004(#1) 来院情報の条件修正
							//+ "LEFT JOIN PATIENTRCNO PR "
							//	+ "ON RS.KANJA_ID = PR.KANJA_ID "
							// 2017.02.28 Del M.Shinbo@Cosmo End KUMA205-3-CAL004(#1) 来院情報の条件修正
							// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更
							// 2015.02.27 Mod S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
							// 2019.09.02 Add H.Taira@COSMO Start KUMA205-10-001
							+ "LEFT JOIN PATIENTINFO  PI "
								+ "ON RS.KANJA_ID  = PI.KANJA_ID "
							// 2019.09.02 Add H.Taira@COSMO End   KUMA205-10-001
							+ "WHERE (RS.KENSA_DATE = ? ) "
							//+ "WHERE (RS.KENSA_DATE BETWEEN ? AND ? ) "
							// 2015.02.27 Mod S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
						+ "AND (RS.KENSA_STARTTIME >= ? "
						+ "OR RS.KENSA_STARTTIME = 999999) ");

		// 検査種別の条件付加
		for (int i = 0; i < kensaTypes.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.KENSATYPE_ID = ? ");
			if (i == kensaTypes.length - 1) {
				sql.append(") ");
			}
		}

		// 検査室の条件付加
		for (int i = 0; i < examRooms.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.KENSASITU_ID = ? ");
			if (i == examRooms.length - 1) {
				sql.append(") ");
			}
		}
		/*
		// RI区分の条件付加
		// 2014.05.07 Mod T.Koudate@COSMO Start #2681
		int maxNum =  Configuration.getInstance().getExtendRiOrderMaxLengh();
		boolean containsNone = false;
		if (0 < riOrders.length && riOrders.length < maxNum ) {
			// 全選択・全解除以外の時に、RI区分条件を付加する
			sql.append("AND (RS.RI_ORDER_FLG IN (");

			for (int i = 0; i < riOrders.length; i++) {
				if (i != 0){
					sql.append(",");
				}
				sql.append("?");
				if ("0".equals(riOrders[i]))
				{
					containsNone = true;
				}
			}
			sql.append(")");
			// なし(=0)が選ばれている時は、NULLも対象にする
			if (containsNone){
				sql.append(" OR RS.RI_ORDER_FLG IS NULL");
			}
			sql.append(")");

		}
		*/
		/*
		for (int i = 0; i < riOrders.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.RI_ORDER_FLG = ? ");
			if (i == riOrders.length - 1) {
				sql.append(") ");
			}
		}
		*/
		// 2014.05.07 Mod T.Koudate@COSMO End   #2681

		// 検査ステータスの条件付加
		for (int i = 0; i < kensaStatus.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.STATUS = ? ");
			if (i == kensaStatus.length - 1) {
				sql.append(") ");
			}
		}

		// 依頼科の条件付加
		for (int i = 0; i < section.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.IRAI_SECTION_ID = ? ");
			if (i == section.length - 1) {
				sql.append(") ");
			}
		}

		// 病棟の条件付加
		for (int i = 0; i < byoutou.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			// 2025.09.12 Add Y.Matsumoto@Cosmo Start 患者病棟_対象修正対応
			//sql.append("PI.BYOUTOU_ID = ? ");
			sql.append("RS.DENPYO_BYOUTOU_ID = ? ");
			// 2025.09.12 Add Y.Matsumoto@Cosmo End 患者病棟_対象修正対応
			if (i == byoutou.length - 1) {
				sql.append(") ");
			}
		}

		// 2019.09.02 Add H.Taira@COSMO Start KUMA205-10-001
		// 患者病棟の条件付加
		for (int i = 0; i < patientByoutou.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			// 2025.09.12 Add Y.Matsumoto@Cosmo Start 患者病棟_対象修正対応
			//sql.append("RS.DENPYO_BYOUTOU_ID = ? ");
			sql.append("PI.BYOUTOU_ID = ? ");
			// 2025.09.12 Add Y.Matsumoto@Cosmo End 患者病棟_対象修正対応

			if (i == patientByoutou.length - 1) {
				sql.append(") ");
			}
		}
		// 2019.09.02 Add H.Taira@COSMO End   KUMA205-10-001

		// 患者入外の条件付加
		for (int i = 0; i < kanjaNyugai.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.KANJA_NYUGAIKBN = ? ");
			if (i == kanjaNyugai.length - 1) {
				sql.append(") ");
			}
		}

		// 伝票入外の条件付加
		for (int i = 0; i < denpyouNyugai.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.DENPYO_NYUGAIKBN = ? ");
			if (i == denpyouNyugai.length - 1) {
				sql.append(") ");
			}
		}

		sql.append("ORDER BY RS.KENSA_STARTTIME, RS.RIS_ID");

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			stmt.setInt(1, startDate);
			// 2015.02.27 Mod S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
			stmt.setInt(2, kensaStartTime);
			int i = 3;
			//stmt.setInt(2, endDate);
			//stmt.setInt(3, kensaStartTime);
			//int i = 4;
			//2015.02.27 Mod S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更

			// 検査種別
			for (int j = 0; j < kensaTypes.length; j++) {
				stmt.setString(i, kensaTypes[j]);
				i++;
			}

			// 検査室
			for (int j = 0; j < examRooms.length; j++) {
				stmt.setString(i, examRooms[j]);
				i++;
			}

			// RI区分
			// 2014.05.07 Mod T.Koudate@COSMO Start #2681
			// 2016.08.08 Del T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
			// SQL組立時にパラメータ値も追加済み
			//if (0 < riOrders.length && riOrders.length < maxNum ) {
			//	for (int j = 0; j < riOrders.length; j++) {
			//		stmt.setString(i, riOrders[j]);
			//		i++;
			//	}
			//}
			// 2016.08.08 Del T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する
			/*
			/*
			for (int j = 0; j < riOrders.length; j++) {
				stmt.setString(i, riOrders[j]);
				i++;
			}
			*/
			// 2014.05.07 Mod T.Koudate@COSMO End   #2681

			// 検査ステータス
			for (int j = 0; j < kensaStatus.length; j++) {
				stmt.setString(i, kensaStatus[j]);
				i++;
			}

			// 依頼科
			for (int j = 0; j < section.length; j++) {
				stmt.setString(i, section[j]);
				i++;
			}

			// 病棟
			for (int j = 0; j < byoutou.length; j++) {
				stmt.setString(i, byoutou[j]);
				i++;
			}

			// 2019.09.02 Add H.Taira@COSMO Start KUMA205-10-001
			// 患者病棟
			for (int j = 0; j < patientByoutou.length; j++) {
				stmt.setString(i, patientByoutou[j]);
				i++;
			}
			// 2019.09.02 Add H.Taira@COSMO End   KUMA205-10-001

			// 患者入外
			for (int j = 0; j < kanjaNyugai.length; j++) {
				stmt.setString(i, kanjaNyugai[j]);
				i++;
			}

			// 伝票入外
			for (int j = 0; j < denpyouNyugai.length; j++) {
				stmt.setString(i, denpyouNyugai[j]);
				i++;
			}

			getLogger().debug(sql.toString());

			rs = stmt.executeQuery();

			while (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"RIS_ID[" + rs.getString("RIS_ID") + "] "
									+ "Kensa_Date[" + rs.getInt("KENSA_DATE") + "] "
									+ "Kensa_StartTime[" + rs.getInt("KENSA_STARTTIME") + "] "
									+ "Section_RyakuName[" + rs.getString("SECTION_RYAKUNAME") + "] "
									+ "Byoutou_RyakuName[" + rs.getString("BYOUTOU_RYAKUNAME") + "] "
									+ "Kanji_Simei[" + rs.getString("KANJISIMEI") + "] "
									+ "Kensa_Date_Age[" + rs.getString("KENSA_DATE_AGE") + "] "
									+ "KensaType_RyakuName[" + rs.getString("KENSATYPE_RYAKUNAME") + "] "
									+ "ValueLabel[" + rs.getString("VALUELABEL") + "] "
									+ "Bui_RyakuName[" + rs.getString("BUI_RYAKUNAME") + "] "
									+ "KensaHouhou_RyakuName[" + rs.getString("KENSAHOUHOU_RYAKUNAME") + "] "
									+ "Sayuu_RyakuName[" + rs.getString("SAYUU_RYAKUNAME") + "] "
									+ "Houkou_RyakuName[" + rs.getString("HOUKOU_RYAKUNAME") + "] "
									+ "YoteiKensaSitu_RyakuName[" + rs.getString("YOTEIKENSASITU_RYAKUNAME") + "] "
									+ "JissiKensaSitu_RyakuName[" + rs.getString("JISSIKENSASITU_RYAKUNAME") + "] "
									// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
									+ "YoteiKensaSitu_Name[" + rs.getString("YOTEIKENSASITU_NAME") + "] "
									+ "JissiKensaSitu_Name[" + rs.getString("JISSIKENSASITU_NAME") + "] "
									+ "JISSIKENSASITU_ID[" + rs.getString("JISSIKENSASITU_ID") + "] "
									+ "JISSIKENSASITU_SHOWORDER[" + rs.getInt("JISSIKENSASITU_SHOWORDER") + "] "
									+ "YOTEIKENSASITU_ID[" + rs.getString("YOTEIKENSASITU_ID") + "] "
									+ "YOTEIKENSASITU_SHOWORDER["+ rs.getInt("YOTEIKENSASITU_SHOWORDER") + "] "
									// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
									// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-003 入外情報追加
									+ "KANJA_NYUGAIKBN["+  rs.getString("KANJA_NYUGAIKBN") + "] "
									// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-003 入外情報追加
									// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
									// 2017.02.28 Mod M.Shinbo@Cosmo Start KUMA205-3-CAL004(#1) 来院情報の条件修正
									+ "PATIENTRCNO["+ rs.getString("PATIENTRCNO") + "] "
									//+ "RCDATE["+ rs.getString("RCDATE") + "] "
									// 2017.02.28 Mod M.Shinbo@Cosmo End KUMA205-3-CAL004(#1) 来院情報の条件修正
									// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更
									// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
									+ "KENSATYPE_ID["+ rs.getString("KENSATYPE_ID") + "] "
									// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
					);
				}

				OrderCalendarItem orderCalendarItem = new OrderCalendarItem();
				orderCalendarItem.setRisID(rs.getString("RIS_ID"));

				orderCalendarItem.setKensaDate(rs.getInt("KENSA_DATE"));
				orderCalendarItem.setKensaStartTime(rs.getInt("KENSA_STARTTIME"));

				if (rs.getString("SECTION_RYAKUNAME") != null) {
					orderCalendarItem.setSectionRyakuName(trimData(rs.getString("SECTION_RYAKUNAME")));
				}
				if (rs.getString("BYOUTOU_RYAKUNAME") != null) {
					orderCalendarItem.setBuiRyakuName(trimData(rs.getString("BYOUTOU_RYAKUNAME")));
				}
				if (rs.getString("KANJISIMEI") != null) {
					orderCalendarItem.setKanjiSimei(trimData(rs.getString("KANJISIMEI")));
				}
				if (rs.getString("KENSA_DATE_AGE") != null) {
					orderCalendarItem.setKensaDateAge(trimData(rs.getString("KENSA_DATE_AGE")));
				}
				if (rs.getString("KENSATYPE_RYAKUNAME") != null) {
					orderCalendarItem.setKensaTypeRyakuName(trimData(rs.getString("KENSATYPE_RYAKUNAME")));
				}
				if (rs.getString("RI_ORDER_FLG") != null) {
					orderCalendarItem.setRiOrderFlg(trimData(rs.getString("RI_ORDER_FLG")));
				}
				if (rs.getString("VALUELABEL") != null) {
					orderCalendarItem.setRiOrder(trimData(rs.getString("VALUELABEL")));
				}
				if (rs.getString("BUI_RYAKUNAME") != null) {
					orderCalendarItem.setBuiRyakuName(trimData(rs.getString("BUI_RYAKUNAME")));
				}
				if (rs.getString("KENSAHOUHOU_RYAKUNAME") != null) {
					orderCalendarItem.setKensaHouhouRyakuName(trimData(rs.getString("KENSAHOUHOU_RYAKUNAME")));
				}
				if (rs.getString("SAYUU_RYAKUNAME") != null) {
					orderCalendarItem.setSayuuRyakuName(trimData(rs.getString("SAYUU_RYAKUNAME")));
				}
				if (rs.getString("HOUKOU_RYAKUNAME") != null) {
					orderCalendarItem.setHoukouRyakuName(trimData(rs.getString("HOUKOU_RYAKUNAME")));
				}
				if (rs.getString("STATUS") != null && rs.getString("STATUS").equals("90")) {
					if (rs.getString("JISSIKENSASITU_RYAKUNAME") != null) {
						orderCalendarItem.setKensaSituName(trimData(rs.getString("JISSIKENSASITU_RYAKUNAME")));
					}
					// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
					if (rs.getString("JISSIKENSASITU_NAME") != null) {
						orderCalendarItem.setKensaSituTitle(trimData(rs.getString("JISSIKENSASITU_NAME")));
					}
					if (rs.getString("JISSIKENSASITU_ID") != null) {
						orderCalendarItem.setKensaSituID(trimData(rs.getString("JISSIKENSASITU_ID")));
					}
					if (rs.getString("JISSIKENSASITU_SHOWORDER") != null) {
						orderCalendarItem.setKensaSituShowOrder(rs.getInt("JISSIKENSASITU_SHOWORDER"));
					}
					// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
				}else{
					if (rs.getString("YOTEIKENSASITU_RYAKUNAME") != null) {
						orderCalendarItem.setKensaSituName(trimData(rs.getString("YOTEIKENSASITU_RYAKUNAME")));
					}
					// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
					if (rs.getString("YOTEIKENSASITU_NAME") != null) {
						orderCalendarItem.setKensaSituTitle(trimData(rs.getString("YOTEIKENSASITU_NAME")));
					}
					if (rs.getString("YOTEIKENSASITU_ID") != null) {
						orderCalendarItem.setKensaSituID(trimData(rs.getString("YOTEIKENSASITU_ID")));
					}
					if (rs.getString("YOTEIKENSASITU_SHOWORDER") != null) {
						orderCalendarItem.setKensaSituShowOrder(rs.getInt("YOTEIKENSASITU_SHOWORDER"));
					}
					// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
				}
				orderCalendarItem.setStatus(rs.getString("STATUS"));

				//2011.Dec 検査内容表示 >>>
				// 部位セット
				/*
				orderCalendarItem.setBuiSet(getBuiSet(
						orderCalendarItem.getRisID(),
						orderCalendarItem.getStatus()));
				*/
				String kensaNaiyo = getBuiSet(orderCalendarItem.getRisID());
				orderCalendarItem.setBuiSet(kensaNaiyo);
				//2011.Dec 検査内容表示 <<<

				// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-003 入外情報追加
				String nyuGaiKbn = rs.getString("KANJA_NYUGAIKBN");
				String nyuGaiInfo = "";
				if ((nyuGaiKbn != null) && (nyuGaiKbn.equals(NYUGAI_KBN_NYU))){
					if (rs.getString("BYOUTOU_RYAKUNAME") != null) {
						nyuGaiInfo = trimData(rs.getString("BYOUTOU_RYAKUNAME"));
					}
				}
				else if ((nyuGaiKbn != null) && (nyuGaiKbn.equals(NYUGAI_KBN_GAI))){
					nyuGaiInfo = NYUGAI_GAI;
				}
				orderCalendarItem.setNyuGaiInfo(nyuGaiInfo);
				// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-003 入外情報追加
				// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
				// 2017.02.28 Mod M.Shinbo@Cosmo Start KUMA205-3-CAL004(#1) 来院情報の条件修正
				if (rs.getString("PATIENTRCNO") != null) {
					orderCalendarItem.setRcDate(rs.getString("PATIENTRCNO"));
				}
				//if (rs.getString("RCDATE") != null) {
				//	orderCalendarItem.setRcDate(rs.getString("RCDATE"));
				//}
				// 2017.02.28 Mod M.Shinbo@Cosmo End KUMA205-3-CAL004(#1) 来院情報の条件修正
				// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更

				// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
				if (rs.getString("KENSATYPE_ID") != null) {
					orderCalendarItem.setKensatypeID(rs.getString("KENSATYPE_ID"));
				}
				// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更

				orderCalendarItems.add(orderCalendarItem);
			}
		} catch (SQLException e) {
			getLogger().error("", e);
			throw new DBAccessException(e);
		} catch (DBAccessException e) {
			getLogger().error("", e);
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				// NOP
			}
			manager.returnConnection(conn);
		}

		return orderCalendarItems;
	}

	//2011.Dec 検査内容表示>>>
	/*
	private String getBuiSet(String risID, String status) throws DBAccessException {
		String buiSet = "";

		int statusNum = 0;
		try {
			statusNum = Integer.parseInt(status);
		} catch (NumberFormatException e) {
			// NOP
		}

		List buiList = null;
		if (statusNum < 10) {
			// OrderBuiTableから取得する。
			OrderBuiTableDataHome orderBui = new OrderBuiTableDataHome();
			buiList = orderBui.getBuiList(risID);
		} else {
			// ExBuiTableから取得する。
			ExBuiTableDataHome orderBui = new ExBuiTableDataHome();
			buiList = orderBui.getBuiList(risID);
		}

		if (buiList != null && buiList.size() > 0) {
			Bui bui = (Bui) buiList.get(0);
			buiSet = bui.getBuiSetName();
		}

		return buiSet;
	}
	*/

	private String getBuiSet(String risID) throws DBAccessException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		// 2014.05.07 Mod T.Koudate@COSMO Start #2681
		// 固定値ではなく、CodeConvertを使用するよう変更
		String sql = "SELECT "
				+ "RS.KENSATYPE_RYAKUNAME, RS.KENSATYPE_NAME, "
				+ "RI_ORDER_FLG, CC.VALUELABEL RI_ORDER_FLG_NAME, "
				+ "BSV.BUI_NAME, " + "BSV.BUI_RYAKUNAME, "
				+ "BSV.HOUKOU_NAME, " + "BSV.HOUKOU_RYAKUNAME, "
				+ "BSV.KENSAHOUHOU_NAME, " + "BSV.KENSAHOUHOU_RYAKUNAME "
				+ "FROM "
				+ " ((RISSUMMARYVIEW RS "
				+ "LEFT JOIN STATUSDEFINE SD " + "ON STATUS = SD.STATUSCODE) "
				+ "LEFT JOIN BUISUMMARYVIEW BSV " + "ON RS.RIS_ID = BSV.RIS_ID) "
				+ "LEFT JOIN CODECONVERT CC ON RI_ORDER_FLG=CC.ITEMVALUE AND CC.ITEMID='RIORDER' "
				+ "WHERE RS.RIS_ID = ? ";
		/*
		String sql = "SELECT "
				+ "RS.KENSATYPE_RYAKUNAME, RS.KENSATYPE_NAME, "
				+ "RI_ORDER_FLG, "
				+ "BSV.BUI_NAME, " + "BSV.BUI_RYAKUNAME, "
				+ "BSV.HOUKOU_NAME, " + "BSV.HOUKOU_RYAKUNAME, "
				+ "BSV.KENSAHOUHOU_NAME, " + "BSV.KENSAHOUHOU_RYAKUNAME "
				+ "FROM "
				+ " ((RISSUMMARYVIEW RS "
				+ "LEFT JOIN STATUSDEFINE SD " + "ON STATUS = SD.STATUSCODE) "
				+ "LEFT JOIN BUISUMMARYVIEW BSV " + "ON RS.RIS_ID = BSV.RIS_ID) "
				+ "WHERE RS.RIS_ID = ? ";
		*/
		// 2014.05.07 Mod T.Koudate@COSMO End   #2681
		try {
			conn = manager.borrowConnection();
			//パラメータ設定
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, risID);
			//SQL実行
			rs = stmt.executeQuery();

			// 2012.03.06 Add Yk.Suzuki@CIJ Start SSGH-4-003
			// カレンダー区切り文字
			String calseparator = com.yokogawa.radiquest.ris.core.Configuration.getInstance().getCalseparator();
			// 2012.03.06 Add Yk.Suzuki@CIJ Start SSGH-4-003


			String rslt = "";
			while (rs.next()) {
				//データ取得
				if (rs.getString("RI_ORDER_FLG") != null) {
					String riOrder = rs.getString("RI_ORDER_FLG");
					String data = "";
					// 2014.05.07 Mod T.Koudate@COSMO Start #2681
					// 固定値ではなく、CodeConvertを使用するよう変更
					data = rs.getString("RI_ORDER_FLG_NAME");
					/*
					if (riOrder.equals("1")) {
						data = "注射";
					} else if (riOrder.equals("2")) {
						data = "検査";
					} else if (riOrder.equals("3")) {
						data = "追跡";
					}
					*/
					// 2014.05.07 Mod T.Koudate@COSMO End   #2681
					if (data != "")
					{
						// 2012.03.06 Mod Yk.Suzuki@CIJ Start SSGH-4-003
						rslt += calseparator + data;
						//rslt += " " + data;
						// 2012.03.06 Mod Yk.Suzuki@CIJ End   SSGH-4-003
					}
				}

				//
				//略称があれば優先して使用
				//
				//検査種別
				if (rs.getString("KENSATYPE_RYAKUNAME") != null) {
					// 2012.03.06 Mod Yk.Suzuki@CIJ Start SSGH-4-003
					rslt += calseparator + trimData(rs.getString("KENSATYPE_RYAKUNAME"));
					//rslt += " " + trimData(rs.getString("KENSATYPE_RYAKUNAME"));
					// 2012.03.06 Mod Yk.Suzuki@CIJ End   SSGH-4-003
				}
				else if (rs.getString("KENSATYPE_NAME") != null) {
					// 2012.03.06 Mod Yk.Suzuki@CIJ Start SSGH-4-003
					rslt += calseparator + trimData(rs.getString("KENSATYPE_NAME"));
					//rslt += " " + trimData(rs.getString("KENSATYPE_NAME"));
					// 2012.03.06 Mod Yk.Suzuki@CIJ End   SSGH-4-003
				}
				//部位
				if (rs.getString("BUI_RYAKUNAME") != null) {
					// 2012.03.06 Mod Yk.Suzuki@CIJ Start SSGH-4-003
					rslt += calseparator + trimData(rs.getString("BUI_RYAKUNAME"));
					//rslt += " " + trimData(rs.getString("BUI_RYAKUNAME"));
					// 2012.03.06 Mod Yk.Suzuki@CIJ End   SSGH-4-003
				}
				else if (rs.getString("BUI_NAME") != null) {
					// 2012.03.06 Mod Yk.Suzuki@CIJ Start SSGH-4-003
					rslt += calseparator + trimData(rs.getString("BUI_NAME"));
					//rslt += " " + trimData(rs.getString("BUI_NAME"));
					// 2012.03.06 Mod Yk.Suzuki@CIJ End   SSGH-4-003
				}
				//方向
				if (rs.getString("HOUKOU_RYAKUNAME") != null) {
					// 2012.03.06 Mod Yk.Suzuki@CIJ Start SSGH-4-003
					rslt += calseparator + trimData(rs.getString("HOUKOU_RYAKUNAME"));
					//rslt += " " + trimData(rs.getString("HOUKOU_RYAKUNAME"));
					// 2012.03.06 Mod Yk.Suzuki@CIJ End   SSGH-4-003

				}
				else if (rs.getString("HOUKOU_NAME") != null) {
					// 2012.03.06 Mod Yk.Suzuki@CIJ Start SSGH-4-003
					rslt += calseparator + trimData(rs.getString("HOUKOU_NAME"));
					//rslt += " " + trimData(rs.getString("HOUKOU_NAME"));
					// 2012.03.06 Mod Yk.Suzuki@CIJ End   SSGH-4-003
				}
				//方法
				if (rs.getString("KENSAHOUHOU_RYAKUNAME") != null) {
					// 2012.03.06 Mod Yk.Suzuki@CIJ Start SSGH-4-003
					rslt += calseparator + trimData(rs.getString("KENSAHOUHOU_RYAKUNAME"));
					//rslt += " " + trimData(rs.getString("KENSAHOUHOU_RYAKUNAME"));
					// 2012.03.06 Mod Yk.Suzuki@CIJ End   SSGH-4-003
				}
				else if (rs.getString("KENSAHOUHOU_NAME") != null) {
					// 2012.03.06 Mod Yk.Suzuki@CIJ Start SSGH-4-003
					rslt += calseparator + trimData(rs.getString("KENSAHOUHOU_NAME"));
					//rslt += " " + trimData(rs.getString("KENSAHOUHOU_NAME"));
					// 2012.03.06 Mod Yk.Suzuki@CIJ End   SSGH-4-003
				}
			}

			// 2012.03.06 Add Yk.Suzuki@CIJ Start SSGH-4-003
			// 先頭・末尾の連続する区切り文字を消す
			rslt = rslt.replaceAll("^" + calseparator + "+", "");
			rslt = rslt.replaceAll("" + calseparator + "+$", "");
			// 2012.03.06 Add Yk.Suzuki@CIJ End   SSGH-4-003

			return trimData(rslt);

		} catch (SQLException e) {
			getLogger().error("", e);
			throw new DBAccessException(e);
		} catch (DBAccessException e) {
			getLogger().error("", e);
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				// NOP
			}
			manager.returnConnection(conn);
		}
	}
	//2011.Dec 検査内容表示<<<

	/**
	 * 検査種別マスタより検査種別リストを取得する(カレンダー)
	 *
	 * @return orderList
	 */
	public List getOrderCalendarItems(int kensaStartTime, int startDate,
			String[] kensaTypes, String[] examRooms, String[] riOrders,
			String[] kensaStatus, String[] section, String[] byoutou,
			// 2019.09.02 Add H.Taira@COSMO Start KUMA205-10-001
			String[] patientByoutou,
			// 2019.09.02 Add H.Taira@COSMO End   KUMA205-10-001
			String[] kanjaNyugai, String[] denpyouNyugai)
			throws DBAccessException {
		List orderCalendarItems = new ArrayList();
		if (kensaTypes == null || kensaTypes.length == 0)
			return orderCalendarItems;

		DateAction dateAction = new DateAction();
		// 2015.02.27 Del S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
		//int endDate = dateAction.afterDate(startDate, 6);
		// 2015.02.27 Del S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		StringBuilder sql = new StringBuilder("SELECT "
						+ "RS.RIS_ID, "
						+ "RS.KANJISIMEI, "
						+ "RS.KENSA_DATE_AGE, "
						+ "RS.KENSATYPE_RYAKUNAME, "
						+ "RS.KENSA_DATE, "
						+ "RS.KENSA_STARTTIME, "
						+ "RS.RI_ORDER_FLG, "
						+ "CC.VALUELABEL, "
						+ "BSV.BUI_RYAKUNAME, "
						+ "BSV.KENSAHOUHOU_RYAKUNAME, "
						+ "BSV.SAYUU_RYAKUNAME, "
						+ "BSV.HOUKOU_RYAKUNAME, "
						+ "RS.YOTEIKENSASITU_RYAKUNAME, "
						+ "RS.JISSIKENSASITU_RYAKUNAME, "
						+ "BM.BYOUTOU_RYAKUNAME, "
						+ "SM.SECTION_RYAKUNAME, "
						+ "RS.KENSATYPE_ID, "
						+ "RS.STATUS "
						// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
						+ ",RS.YOTEIKENSASITU_NAME "
						+ ",RS.JISSIKENSASITU_NAME "
						+ ",RS.YOTEIKENSASITU_ID "
						+ ",RS.JISSIKENSASITU_ID "
						+ ",RS.YOTEIKENSASITU_SHOWORDER "
						+ ",RS.JISSIKENSASITU_SHOWORDER "
						// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
						// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-003 入外情報追加
						+ ",RS.KANJA_NYUGAIKBN "
						// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-003 入外情報追加
						// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
						// 2017.02.28 Mod M.Shinbo@Cosmo Start KUMA205-3-CAL004(#1) 来院情報の条件修正
						+ ",RS.PATIENTRCNO "
						//+ ",PR.RCDATE "
						// 2017.02.28 Mod M.Shinbo@Cosmo End KUMA205-3-CAL004(#1) 来院情報の条件修正
						// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更
						// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
						+ ",RS.KENSATYPE_ID "
						// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
					+ "FROM "
						+ " ((( RISCALENDARSUMMARYVIEW RS "
							+ "LEFT JOIN SECTIONMASTER SM "
								+ "ON RS.IRAI_SECTION_ID = SM.SECTION_ID) "
							+ "LEFT JOIN BYOUTOUMASTER BM "
								+ "ON RS.BYOUTOU_ID = BM.BYOUTOU_ID) "
							+ "LEFT JOIN CODECONVERT CC "
								+ "ON RS.RI_ORDER_FLG = CC.ITEMVALUE AND CC.ITEMID ='RIORDER') "
							+ "LEFT JOIN BUISUMMARYVIEW BSV "
								+ "ON RS.RIS_ID = BSV.RIS_ID AND BSV.NO = 1 "
							// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
							// 2017.02.28 Del M.Shinbo@Cosmo Start KUMA205-3-CAL004(#1) 来院情報の条件修正
							//+ "LEFT JOIN PATIENTRCNO PR "
							//	+ "ON RS.KANJA_ID = PR.KANJA_ID "
							// 2017.02.28 Del M.Shinbo@Cosmo End KUMA205-3-CAL004(#1) 来院情報の条件修正
							// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更
							// 2015.02.27 Mod S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変
							+ "WHERE (RS.KENSA_DATE = ? ) "
							//+ "WHERE (RS.KENSA_DATE BETWEEN ? AND ? ) "
							// 2015.02.27 Mod S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
						+ "AND (RS.KENSA_STARTTIME >= ? "
						+ "OR RS.KENSA_STARTTIME = 999999) ");

		// 検査種別の条件付加
		for (int i = 0; i < kensaTypes.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.KENSATYPE_ID = ? ");
			if (i == kensaTypes.length - 1) {
				sql.append(") ");
			}
		}

		// 検査室の条件付加
		for (int i = 0; i < examRooms.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.KENSASITU_ID = ? ");
			if (i == examRooms.length - 1) {
				sql.append(") ");
			}
		}
		/*
		// RI区分の条件付加
		// 2014.05.07 Mod T.Koudate@COSMO Start #2681
		int maxNum =  Configuration.getInstance().getExtendRiOrderMaxLengh();
		boolean containsNone = false;
		if (0 < riOrders.length && riOrders.length < maxNum ) {
			// 全選択・全解除以外の時に、RI区分条件を付加する
			sql.append("AND (RS.RI_ORDER_FLG IN (");

			for (int i = 0; i < riOrders.length; i++) {
				if (i != 0){
					sql.append(",");
				}
				sql.append("?");
				if ("0".equals(riOrders[i]))
				{
					containsNone = true;
				}
			}
			sql.append(")");
			// なし(=0)が選ばれている時は、NULLも対象にする
			if (containsNone){
				sql.append(" OR RS.RI_ORDER_FLG IS NULL");
			}
			sql.append(")");

		}
		*/
		/*
		for (int i = 0; i < riOrders.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.RI_ORDER_FLG = ? ");
			if (i == riOrders.length - 1) {
				sql.append(") ");
			}
		}
		*/
		// 2014.05.07 Mod T.Koudate@COSMO End   #2681

		// 検査ステータスの条件付加
		for (int i = 0; i < kensaStatus.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.STATUS = ? ");
			if (i == kensaStatus.length - 1) {
				sql.append(") ");
			}
		}

		// 依頼科の条件付加
		for (int i = 0; i < section.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.IRAI_SECTION_ID = ? ");
			if (i == section.length - 1) {
				sql.append(") ");
			}
		}

		// 病棟の条件付加
		for (int i = 0; i < byoutou.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			// 2025.09.12 Add Y.Matsumoto@Cosmo Start 患者病棟_対象修正対応
			//sql.append("PI.BYOUTOU_ID = ? ");
			sql.append("RS.DENPYO_BYOUTOU_ID = ? ");
			// 2025.09.12 Add Y.Matsumoto@Cosmo End 患者病棟_対象修正対応
			if (i == byoutou.length - 1) {
				sql.append(") ");
			}
		}

		// 2019.09.02 Add H.Taira@COSMO Start KUMA205-10-001
		// 病棟の条件付加
		for (int i = 0; i < patientByoutou.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			// 2025.09.12 Add Y.Matsumoto@Cosmo Start 患者病棟_対象修正対応
			//sql.append("RS.DENPYO_BYOUTOU_ID = ? ");
			sql.append("PI.BYOUTOU_ID = ? ");
			// 2025.09.12 Add Y.Matsumoto@Cosmo End 患者病棟_対象修正対応
			if (i == patientByoutou.length - 1) {
				sql.append(") ");
			}
		}
		// 2019.09.02 Add H.Taira@COSMO End   KUMA205-10-001

		// 患者入外の条件付加
		for (int i = 0; i < kanjaNyugai.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.KANJA_NYUGAIKBN = ? ");
			if (i == kanjaNyugai.length - 1) {
				sql.append(") ");
			}
		}

		// 伝票入外の条件付加
		for (int i = 0; i < denpyouNyugai.length; i++) {
			if (i == 0) {
				sql.append("AND (");
			} else {
				sql.append("OR ");
			}
			sql.append("RS.DENPYO_NYUGAIKBN = ? ");
			if (i == denpyouNyugai.length - 1) {
				sql.append(") ");
			}
		}

		sql.append("ORDER BY RS.KENSA_STARTTIME, RS.RIS_ID");

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			stmt.setInt(1, startDate);
			stmt.setInt(1, startDate);
			// 2015.02.27 Mod S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
			stmt.setInt(2, kensaStartTime);
			int i = 3;
			//stmt.setInt(2, endDate);
			//stmt.setInt(3, kensaStartTime);
			//int i = 4;
			//2015.02.27 Mod S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更

			// 検査種別
			for (int j = 0; j < kensaTypes.length; j++) {
				stmt.setString(i, kensaTypes[j]);
				i++;
			}

			// 検査室
			for (int j = 0; j < examRooms.length; j++) {
				stmt.setString(i, examRooms[j]);
				i++;
			}

			// RI区分
			// 2014.05.07 Mod T.Koudate@COSMO Start #2681
			// 2016.08.08 Del T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
			// SQL組立時にパラメータ値も追加済み
			//if (0 < riOrders.length && riOrders.length < maxNum ) {
			//	for (int j = 0; j < riOrders.length; j++) {
			//		stmt.setString(i, riOrders[j]);
			//		i++;
			//	}
			//}
			// 2016.08.08 Del T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する
			/*
			/*
			for (int j = 0; j < riOrders.length; j++) {
				stmt.setString(i, riOrders[j]);
				i++;
			}
			*/
			// 2014.05.07 Mod T.Koudate@COSMO End   #2681

			// 検査ステータス
			for (int j = 0; j < kensaStatus.length; j++) {
				stmt.setString(i, kensaStatus[j]);
				i++;
			}

			// 依頼科
			for (int j = 0; j < section.length; j++) {
				stmt.setString(i, section[j]);
				i++;
			}

			// 病棟
			for (int j = 0; j < byoutou.length; j++) {
				stmt.setString(i, byoutou[j]);
				i++;
			}

			// 2019.09.02 Add H.Taira@COSMO Start KUMA205-10-001
			// 病棟
			for (int j = 0; j < patientByoutou.length; j++) {
				stmt.setString(i, patientByoutou[j]);
				i++;
			}
			// 2019.09.02 Add H.Taira@COSMO End   KUMA205-10-001

			// 患者入外
			for (int j = 0; j < kanjaNyugai.length; j++) {
				stmt.setString(i, kanjaNyugai[j]);
				i++;
			}

			// 伝票入外
			for (int j = 0; j < denpyouNyugai.length; j++) {
				stmt.setString(i, denpyouNyugai[j]);
				i++;
			}

			getLogger().debug(sql.toString());

			rs = stmt.executeQuery();

			while (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"RIS_ID[" + rs.getString("RIS_ID") + "] "
									+ "Kensa_Date[" + rs.getInt("KENSA_DATE") + "] "
									+ "Kensa_StartTime[" + rs.getInt("KENSA_STARTTIME") + "] "
									+ "Section_RyakuName[" + rs.getString("SECTION_RYAKUNAME") + "] "
									+ "Byoutou_RyakuName[" + rs.getString("BYOUTOU_RYAKUNAME") + "] "
									+ "Kanji_Simei[" + rs.getString("KANJISIMEI") + "] "
									+ "Kensa_Date_Age[" + rs.getString("KENSA_DATE_AGE") + "] "
									+ "KensaType_RyakuName[" + rs.getString("KENSATYPE_RYAKUNAME") + "] "
									+ "ValueLabel[" + rs.getString("VALUELABEL") + "] "
									+ "Bui_RyakuName[" + rs.getString("BUI_RYAKUNAME") + "] "
									+ "KensaHouhou_RyakuName[" + rs.getString("KENSAHOUHOU_RYAKUNAME") + "] "
									+ "Sayuu_RyakuName[" + rs.getString("SAYUU_RYAKUNAME") + "] "
									+ "Houkou_RyakuName[" + rs.getString("HOUKOU_RYAKUNAME") + "] "
									+ "YoteiKensaSitu_RyakuName[" + rs.getString("YOTEIKENSASITU_RYAKUNAME") + "] "
									+ "JissiKensaSitu_RyakuName[" + rs.getString("JISSIKENSASITU_RYAKUNAME") + "] "
									// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
									+ "YoteiKensaSitu_Name[" + rs.getString("YOTEIKENSASITU_NAME") + "] "
									+ "JissiKensaSitu_Name[" + rs.getString("JISSIKENSASITU_NAME") + "] "
									+ "JISSIKENSASITU_ID[" + rs.getString("JISSIKENSASITU_ID") + "] "
									+ "JISSIKENSASITU_SHOWORDER[" + rs.getInt("JISSIKENSASITU_SHOWORDER") + "] "
									+ "YOTEIKENSASITU_ID[" + rs.getString("YOTEIKENSASITU_ID") + "] "
									+ "YOTEIKENSASITU_SHOWORDER["+ rs.getInt("YOTEIKENSASITU_SHOWORDER") + "] "
									// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
									// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-003 入外情報追加
									+ "KANJA_NYUGAIKBN["+  rs.getString("KANJA_NYUGAIKBN") + "] "
									// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-003 入外情報追加
									// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
									// 2017.02.28 Mod M.Shinbo@Cosmo Start KUMA205-3-CAL004(#1) 来院情報の条件修正
									+ "PATIENTRCNO["+ rs.getString("PATIENTRCNO") + "] "
									//+ "RCDATE["+ rs.getString("RCDATE") + "] "
									// 2017.02.28 Mod M.Shinbo@Cosmo End KUMA205-3-CAL004(#1) 来院情報の条件修正
									// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更
									// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
									+ "KENSATYPE_ID["+ rs.getString("KENSATYPE_ID") + "] "
									// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
					);
				}

				OrderCalendarItem orderCalendarItem = new OrderCalendarItem();
				orderCalendarItem.setRisID(rs.getString("RIS_ID"));

				orderCalendarItem.setKensaDate(rs.getInt("KENSA_DATE"));
				orderCalendarItem.setKensaStartTime(rs.getInt("KENSA_STARTTIME"));

				if (rs.getString("SECTION_RYAKUNAME") != null) {
					orderCalendarItem.setSectionRyakuName(trimData(rs.getString("SECTION_RYAKUNAME")));
				}
				if (rs.getString("BYOUTOU_RYAKUNAME") != null) {
					orderCalendarItem.setBuiRyakuName(trimData(rs.getString("BYOUTOU_RYAKUNAME")));
				}
				if (rs.getString("KANJISIMEI") != null) {
					orderCalendarItem.setKanjiSimei(trimData(rs.getString("KANJISIMEI")));
				}
				if (rs.getString("KENSA_DATE_AGE") != null) {
					orderCalendarItem.setKensaDateAge(trimData(rs.getString("KENSA_DATE_AGE")));
				}
				if (rs.getString("KENSATYPE_RYAKUNAME") != null) {
					orderCalendarItem.setKensaTypeRyakuName(trimData(rs.getString("KENSATYPE_RYAKUNAME")));
				}
				if (rs.getString("RI_ORDER_FLG") != null) {
					orderCalendarItem.setRiOrderFlg(trimData(rs.getString("RI_ORDER_FLG")));
				}
				if (rs.getString("VALUELABEL") != null) {
					orderCalendarItem.setRiOrder(trimData(rs.getString("VALUELABEL")));
				}
				if (rs.getString("BUI_RYAKUNAME") != null) {
					orderCalendarItem.setBuiRyakuName(trimData(rs.getString("BUI_RYAKUNAME")));
				}
				if (rs.getString("KENSAHOUHOU_RYAKUNAME") != null) {
					orderCalendarItem.setKensaHouhouRyakuName(trimData(rs.getString("KENSAHOUHOU_RYAKUNAME")));
				}
				if (rs.getString("SAYUU_RYAKUNAME") != null) {
					orderCalendarItem.setSayuuRyakuName(trimData(rs.getString("SAYUU_RYAKUNAME")));
				}
				if (rs.getString("HOUKOU_RYAKUNAME") != null) {
					orderCalendarItem.setHoukouRyakuName(trimData(rs.getString("HOUKOU_RYAKUNAME")));
				}
				if (rs.getString("STATUS") != null && rs.getString("STATUS").equals("90")) {
					if (rs.getString("JISSIKENSASITU_RYAKUNAME") != null) {
						orderCalendarItem.setKensaSituName(trimData(rs.getString("JISSIKENSASITU_RYAKUNAME")));
					}
					// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
					if (rs.getString("JISSIKENSASITU_NAME") != null) {
						orderCalendarItem.setKensaSituTitle(trimData(rs.getString("JISSIKENSASITU_NAME")));
					}
					if (rs.getString("JISSIKENSASITU_ID") != null) {
						orderCalendarItem.setKensaSituID(trimData(rs.getString("JISSIKENSASITU_ID")));
					}
					if (rs.getString("JISSIKENSASITU_SHOWORDER") != null) {
						orderCalendarItem.setKensaSituShowOrder(rs.getInt("JISSIKENSASITU_SHOWORDER"));
					}
					// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
				}else{
					if (rs.getString("YOTEIKENSASITU_RYAKUNAME") != null) {
						orderCalendarItem.setKensaSituName(trimData(rs.getString("YOTEIKENSASITU_RYAKUNAME")));
					}
					// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
					if (rs.getString("YOTEIKENSASITU_NAME") != null) {
						orderCalendarItem.setKensaSituTitle(trimData(rs.getString("YOTEIKENSASITU_NAME")));
					}
					if (rs.getString("YOTEIKENSASITU_ID") != null) {
						orderCalendarItem.setKensaSituID(trimData(rs.getString("YOTEIKENSASITU_ID")));
					}
					if (rs.getString("YOTEIKENSASITU_SHOWORDER") != null) {
						orderCalendarItem.setKensaSituShowOrder(rs.getInt("YOTEIKENSASITU_SHOWORDER"));
					}
					// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
				}
				orderCalendarItem.setStatus(rs.getString("STATUS"));

				//2011.Dec 検査内容表示 >>>
				// 部位セット
				/*
				orderCalendarItem.setBuiSet(getBuiSet(
						orderCalendarItem.getRisID(),
						orderCalendarItem.getStatus()));
				*/
				String kensaNaiyo = getBuiSet(orderCalendarItem.getRisID());
				orderCalendarItem.setBuiSet(kensaNaiyo);
				//2011.Dec 検査内容表示 <<<

				// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-003 入外情報追加
				String nyuGaiKbn = rs.getString("KANJA_NYUGAIKBN");
				String nyuGaiInfo = "";
				if ((nyuGaiKbn != null) && (nyuGaiKbn.equals(NYUGAI_KBN_NYU))){
					if (rs.getString("BYOUTOU_RYAKUNAME") != null) {
						nyuGaiInfo = trimData(rs.getString("BYOUTOU_RYAKUNAME"));
					}
				}
				else if ((nyuGaiKbn != null) && (nyuGaiKbn.equals(NYUGAI_KBN_GAI))){
					nyuGaiInfo = NYUGAI_GAI;
				}
				orderCalendarItem.setNyuGaiInfo(nyuGaiInfo);
				// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-003 入外情報追加

				// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
				// 2017.02.28 Mod M.Shinbo@Cosmo Start KUMA205-3-CAL004(#1) 来院情報の条件修正
				if (rs.getString("PATIENTRCNO") != null) {
					orderCalendarItem.setRcDate(rs.getString("PATIENTRCNO"));
				}
				//if (rs.getString("RCDATE") != null) {
				//	orderCalendarItem.setRcDate(rs.getString("RCDATE"));
				//}
				// 2017.02.28 Mod M.Shinbo@Cosmo End KUMA205-3-CAL004(#1) 来院情報の条件修正
				// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更

				// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
				if (rs.getString("KENSATYPE_ID") != null) {
					orderCalendarItem.setKensatypeID(rs.getString("KENSATYPE_ID"));
				}
				// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更

				orderCalendarItems.add(orderCalendarItem);
			}
		} catch (SQLException e) {
			getLogger().error("", e);
			throw new DBAccessException(e);
		} catch (DBAccessException e) {
			getLogger().error("", e);
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				// NOP
			}
			manager.returnConnection(conn);
		}

		return orderCalendarItems;
	}

	// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
	/**
	 * 該当オーダのステータスを取得する
	 *
	 * @return orderList
	 */
	public OrderCalendarItem getOrderStatus(String risid)
			throws DBAccessException {

		OrderCalendarItem orderCalendarItem = new OrderCalendarItem();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		StringBuilder sql = new StringBuilder("SELECT "
						+ "RIS_ID, "
						+ "STATUS "
					+ "FROM "
						+ "EXMAINTABLE "
					+ "where RIS_ID = ?" );

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());
			// パラメータセット
			stmt.setString(1, risid);
			getLogger().debug(sql.toString());
			rs = stmt.executeQuery();

			while (rs.next()) {
				if (rs.getString("STATUS") != null) {
					orderCalendarItem.setStatus(rs.getString("STATUS"));
				}
			}

		} catch (SQLException e) {
			getLogger().error("", e);
			throw new DBAccessException(e);
		} catch (DBAccessException e) {
			getLogger().error("", e);
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				// NOP
			}
			manager.returnConnection(conn);
		}

		return orderCalendarItem;
	}

	/**
	 * 該当オーダでORDERMAINTABLEの予定検査日付、予定検査室IDを更新する
	 *
	 * @return orderList
	 */
	public int updateOrdrmain(String risid, String kensastart, String kensasituId)
			throws DBAccessException {

		Connection conn = null;
		PreparedStatement stmt = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		int i = 0;

		StringBuilder sql = new StringBuilder("UPDATE ORDERMAINTABLE "
						+ "SET KENSA_STARTTIME = ?, KENSASITU_ID = ?, KENSAKIKI_ID = ?"
						+ "where RIS_ID = ?" );

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			// パラメータセット
			stmt.setInt(1, Integer.parseInt(kensastart.replaceAll(":","") + "00"));
			stmt.setString(2, kensasituId);
			stmt.setString(3, null);
			stmt.setString(4, risid);

			getLogger().debug(sql.toString());

			i = stmt.executeUpdate();

			if (i ==1) {
				//コミット
	            conn.commit();
			} else {
				conn.rollback();
			}

		} catch (SQLException e) {
			getLogger().error("", e);
			throw new DBAccessException(e);
		} catch (DBAccessException e) {
			getLogger().error("", e);
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				// NOP
			}
			manager.returnConnection(conn);
		}

		return i;
	}

	/**
	 * 該当オーダでORDERMAINTABLEの予定検査日付を更新する
	 *
	 * @return orderList
	 */
	public int updateOrdrmainStartTime(String risid, String kensastart)
			throws DBAccessException {

		Connection conn = null;
		PreparedStatement stmt = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		int i = 0;

		StringBuilder sql = new StringBuilder("UPDATE ORDERMAINTABLE "
						+ "SET KENSA_STARTTIME = ? "
						+ "where RIS_ID = ?" );

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			// パラメータセット
			stmt.setInt(1, Integer.parseInt(kensastart.replaceAll(":","") + "00"));
			stmt.setString(2, risid);

			getLogger().debug(sql.toString());

			i = stmt.executeUpdate();

			if (i ==1) {
				//コミット
	            conn.commit();
			} else {
				conn.rollback();
			}

		} catch (SQLException e) {
			getLogger().error("", e);
			throw new DBAccessException(e);
		} catch (DBAccessException e) {
			getLogger().error("", e);
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				// NOP
			}
			manager.returnConnection(conn);
		}

		return i;
	}

	/**
	 * 該当オーダでEXMAINTABLEの実績検査室IDを更新する
	 *
	 * @return orderList
	 * @throws ParseException
	 */
	public int updateExmain(String risid, String kensasituId)
			throws DBAccessException {

		Connection conn = null;
		PreparedStatement stmt = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		int i = 0;

		StringBuilder sql = new StringBuilder("UPDATE EXMAINTABLE "
						+ "SET KENSASITU_ID = ?, KENSAKIKI_ID = ? "
						+ "where RIS_ID = ?" );

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			stmt.setString(1, kensasituId);
			stmt.setString(2, null);
			stmt.setString(3, risid);

			 i = stmt.executeUpdate();

			if (i ==1) {
				//コミット
	            conn.commit();
			} else {
				conn.rollback();
			}

		} catch (SQLException e) {
			getLogger().error("", e);
			throw new DBAccessException(e);
		} catch (DBAccessException e) {
			getLogger().error("", e);
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				// NOP
			}
			manager.returnConnection(conn);
		}

		return i;
	}
	// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
}