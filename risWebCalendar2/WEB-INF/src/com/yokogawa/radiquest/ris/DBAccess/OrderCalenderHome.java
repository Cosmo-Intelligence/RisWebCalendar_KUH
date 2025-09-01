package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yokogawa.radiquest.ris.action.DateAction;
import com.yokogawa.radiquest.ris.bean.Bui;
import com.yokogawa.radiquest.ris.bean.OrderCalendarItem;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;
import com.yokogawa.radiquest.ris.servlet.Parameters;

public class OrderCalenderHome extends AbstractDataHome {
	public static final String ALL_EXAM_ROOM_ID = Parameters.VALUE_EXAM_ROOM_ALL;

	/**
	 * 検査種別マスタより検査種別リストを取得する
	 *
	 * @return orderList
	 */
	public List getOrderCalendarItems(int kensaStartTime, int startDate,
			String[] kensaTypes, String[] examRooms, String[] riOrders,
			String[] kensaStatus, String[] section, String[] byoutou,
			String[] kanjaNyugai, String[] denpyouNyugai)
			throws DBAccessException {
		List orderCalendarItems = new ArrayList();
		if (kensaTypes == null || kensaTypes.length == 0)
			return orderCalendarItems;

		DateAction dateAction = new DateAction();
		int endDate = dateAction.afterDate(startDate, 6);
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
					+ "WHERE (RS.KENSA_DATE BETWEEN ? AND ? ) "
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

		// RI区分の条件付加
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
			sql.append("RS.DENPYO_BYOUTOU_ID = ? ");
			if (i == byoutou.length - 1) {
				sql.append(") ");
			}
		}

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
			stmt.setInt(2, endDate);
			stmt.setInt(3, kensaStartTime);
			int i = 4;

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
			for (int j = 0; j < riOrders.length; j++) {
				stmt.setString(i, riOrders[j]);
				i++;
			}

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
				}else{
					if (rs.getString("YOTEIKENSASITU_RYAKUNAME") != null) {
						orderCalendarItem.setKensaSituName(trimData(rs.getString("YOTEIKENSASITU_RYAKUNAME")));
					}
				}
				orderCalendarItem.setStatus(rs.getString("STATUS"));

				// 部位セット
				orderCalendarItem.setBuiSet(getBuiSet(
						orderCalendarItem.getRisID(),
						orderCalendarItem.getStatus()));

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
}