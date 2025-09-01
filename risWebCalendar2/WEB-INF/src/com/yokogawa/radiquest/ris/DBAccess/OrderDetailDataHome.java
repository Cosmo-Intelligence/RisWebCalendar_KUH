package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.yokogawa.radiquest.ris.bean.OrderDetail;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;
import com.yokogawa.radiquest.ris.servlet.SimpleDateFormatType;

public class OrderDetailDataHome extends AbstractDataHome {
	private static final String STUDY_INSTANCE_UID_PACKAGE_CODE = "1.2.392.200045.6960.4.7.";

	public ArrayList getOrderDetails(String[] risIDs) throws DBAccessException {
		ArrayList orderDetailList = new ArrayList();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();
		String sql = "SELECT " + "RS.RIS_ID, " + "RS.KENSA_DATE, "
				+ "SD.SHORTLABEL, " + "RS.RECEIPTNUMBER, " + "RS.KENSA_STARTTIME, "
				+ "RS.RECEIPTDATE, " + "RS.KENSA_DATE_AGE, " + "RS.SEX, "
				+ "RS.TRANSPORTTYPE, " + "RS.KANJA_ID, " + "RS.KANASIMEI, "
				+ "RS.KANJISIMEI, " + "RS.ROMASIMEI, " + "RS.KANJA_NYUGAIKBN, "
				+ "RS.IRAI_SECTION, " + "RS.IRAI_SECTIONRYAKU, "
				+ "RS.BYOUTOU_RYAKUNAME, " + "RS.DENPYO_BYOUTOU_NAME, "
				+ "RS.IRAI_DOCTOR_NAME, " + "RS.KENSATYPE_NAME, "
				+ "BSV.BUI_NAME, " + "BSV.BUI_RYAKUNAME, "
				+ "BSV.KENSAHOUHOU_NAME, " + "BSV.KENSAHOUHOU_RYAKUNAME, "
				+ "RS.KENSATYPE_RYAKUNAME, " + "BSV.SAYUU_NAME, "
				+ "BSV.SAYUU_RYAKUNAME, " + "BSV.HOUKOU_NAME, "
				+ "BSV.HOUKOU_RYAKUNAME, " + "RS.DENPYO_NYUGAIKBN, "
				+ "RS.KANJYA_SECTION, " + "RS.YOTEIKENSAROOM, "
				+ "RS.KENSAROOM, " + "RS.INPUTDATE, " + "RS.INPUTTIME, "
				+ "RS.KENSA_GISI_NAME, " + "RS.UKETUKE_TANTOU_NAME, " + "RI_ORDER_FLG, "
				+ "RS.BYOUSITU, " + "RS.EXAMSTARTDATE, "
				+ "RS.EXAMENDDATE, " + "RS.EXAMROOM_NAME, " + "RS.ORDER_DATE, "
				+ "RS.YOBIDASI_DATE, "
				+ "RS.STATUS " + "FROM " + " ((RISSUMMARYVIEW RS "
				+ "LEFT JOIN STATUSDEFINE SD " + "ON STATUS = SD.STATUSCODE) "
				+ "LEFT JOIN BUISUMMARYVIEW BSV "
				+ "ON RS.RIS_ID = BSV.RIS_ID) " + "WHERE ";

		for (int i = 0; i < risIDs.length; i++) {
			sql += "RS.RIS_ID = ? ";
			if (risIDs.length - 1 != i) {
				sql += "or ";
			}
		}
		sql += " ORDER BY RS.RIS_ID";
		getLogger().debug(sql);

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < risIDs.length; i++) {
				stmt.setString(i + 1, risIDs[i]);
			}
			rs = stmt.executeQuery();

			while (rs.next()) {
				OrderDetail orderDetail = createOrderDetail(rs);
				orderDetailList.add(orderDetail);
			}
		} catch (SQLException e) {
			getLogger().error("", e);
			throw new DBAccessException(e);
		} catch (DBAccessException e) {
			getLogger().error("", e);
			throw e;
		} catch (ParseException e) {
			getLogger().error("", e);
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

		return orderDetailList;
	}

	private OrderDetail createOrderDetail(ResultSet rs) throws SQLException,
			ParseException {
		if (getLogger().isDebugEnabled()) {
			// getLogger().debug(
			// "RIS_ID[" + rs.getString("RIS_ID") + "] Kensa_Date["
			// + rs.getInt("Kensa_Date") + "] Kensa_StartTime["
			// + rs.getInt("Kensa_StartTime") + "] Kanja_ID["
			// + rs.getString("Kanja_ID") + "] ExamRoom_ID["
			// + rs.getString("KensaSitu_ID") + "] Status["
			// + rs.getInt("Status") + "]");
		}

		OrderDetail orderDetail = new OrderDetail();

		String risID = rs.getString("RIS_ID");

		orderDetail.setRisID(risID);

		if (rs.getString("KENSA_DATE") != null) {
			Date kensaDate = SimpleDateFormatType.FORMAT_YYYYMMDD.parse(rs
					.getString("KENSA_DATE"));
			orderDetail.setKensaDate(kensaDate);
		}
		if (rs.getString("SHORTLABEL") != null) {
			orderDetail.setKensaStatus(rs.getString("SHORTLABEL"));
		}
		if (rs.getString("RECEIPTNUMBER") != null) {
			orderDetail.setReceiptNumber(rs.getString("RECEIPTNUMBER"));
		}
		if (rs.getString("KENSA_STARTTIME") != null) {
			// ソート用にセット
			orderDetail.setKensaStartTime_NoDisp(rs
					.getString("KENSA_STARTTIME"));

			Date kensaStartTime = SimpleDateFormatType.FORMAT_HHMMSS.parse(rs
					.getString("KENSA_STARTTIME"));
			orderDetail.setKensaStartTime(kensaStartTime);
		}
		if (rs.getString("RECEIPTDATE") != null) {
			Date receiptDate = SimpleDateFormatType.FORMAT_YYYYMMDDHHMM_hi.parse(rs
					.getString("RECEIPTDATE"));
			orderDetail.setReceiptDate(receiptDate);
		}
		if (rs.getString("KENSA_DATE_AGE") != null) {
			orderDetail.setKensaDate_Age(rs.getString("KENSA_DATE_AGE"));
		}
		if (rs.getString("SEX") != null) {
			orderDetail.setSex(rs.getString("SEX"));
		}
		if (rs.getString("TRANSPORTTYPE") != null) {
			orderDetail.setTransporttype(rs.getString("TRANSPORTTYPE"));
		}
		if (rs.getString("KANJA_ID") != null) {
			orderDetail.setKanjaID(rs.getString("KANJA_ID"));
		}
		if (rs.getString("KANASIMEI") != null) {
			orderDetail.setKanaSimei(rs.getString("KANASIMEI"));
		}
		if (rs.getString("KANJISIMEI") != null) {
			orderDetail.setKanjiSimei(rs.getString("KANJISIMEI"));
		}
		if (rs.getString("ROMASIMEI") != null) {
			orderDetail.setRomaSimei(rs.getString("ROMASIMEI"));
		}
		if (rs.getString("KANJA_NYUGAIKBN") != null) {
			orderDetail.setKanjaNyugaiKbn(rs.getString("KANJA_NYUGAIKBN"));
		}
		if (rs.getString("IRAI_SECTION") != null) {
			orderDetail.setIraiSection(rs.getString("IRAI_SECTION"));
		}
		if (rs.getString("IRAI_SECTIONRYAKU") != null) {
			orderDetail.setIraiSectionRyaku(rs.getString("IRAI_SECTIONRYAKU"));
		}
		if (rs.getString("BYOUTOU_RYAKUNAME") != null) {
			orderDetail.setByoutouRyaku(rs.getString("BYOUTOU_RYAKUNAME"));
		}
		if (rs.getString("DENPYO_BYOUTOU_NAME") != null) {
			orderDetail.setDenpyoByoutou(rs.getString("DENPYO_BYOUTOU_NAME"));
		}
		if (rs.getString("IRAI_DOCTOR_NAME") != null) {
			orderDetail.setIraiDoctorName(rs.getString("IRAI_DOCTOR_NAME"));
		}
		if (rs.getString("KENSATYPE_NAME") != null) {
			orderDetail.setKensaTypeName(rs.getString("KENSATYPE_NAME"));
		}
		if (rs.getString("BUI_NAME") != null) {
			orderDetail.setBuiName(rs.getString("BUI_NAME"));
		}
		if (rs.getString("BUI_RYAKUNAME") != null) {
			orderDetail.setBuiRyaku(rs.getString("BUI_RYAKUNAME"));
		}
		if (rs.getString("KENSAHOUHOU_NAME") != null) {
			orderDetail.setHouhou(rs.getString("KENSAHOUHOU_NAME"));
		}
		if (rs.getString("KENSAHOUHOU_RYAKUNAME") != null) {
			orderDetail.setHouhouRyaku(rs.getString("KENSAHOUHOU_RYAKUNAME"));
		}
		if (rs.getString("SAYUU_NAME") != null) {
			orderDetail.setSayuu(rs.getString("SAYUU_NAME"));
		}
		if (rs.getString("SAYUU_RYAKUNAME") != null) {
			orderDetail.setSayuuRyaku(rs.getString("SAYUU_RYAKUNAME"));
		}
		if (rs.getString("HOUKOU_NAME") != null) {
			orderDetail.setHoukou(rs.getString("HOUKOU_NAME"));
		}
		if (rs.getString("HOUKOU_RYAKUNAME") != null) {
			orderDetail.setHoukouRyaku(rs.getString("HOUKOU_RYAKUNAME"));
		}
		if (rs.getString("DENPYO_NYUGAIKBN") != null) {
			orderDetail.setDenPyoNyugaiKbn(rs.getString("DENPYO_NYUGAIKBN"));
		}
		if (rs.getString("KANJYA_SECTION") != null) {
			orderDetail.setKanjaSection(rs.getString("KANJYA_SECTION"));
		}
		if (rs.getString("YOTEIKENSAROOM") != null) {
			orderDetail.setYoteiKensaKiki(rs.getString("YOTEIKENSAROOM"));
		}
		if (rs.getString("KENSAROOM") != null) {
			orderDetail.setKensaKiki(rs.getString("KENSAROOM"));
		}
		if (rs.getString("INPUTDATE") != null) {
			Date inputDate = SimpleDateFormatType.FORMAT_YYYYMMDDHHMM_hi.parse(rs
					.getString("INPUTDATE"));
			orderDetail.setInputDate(inputDate);
		}
		if (rs.getString("INPUTTIME") != null) {
			Date inputTime = SimpleDateFormatType.FORMAT_YYYYMMDDHHMM_hi.parse(rs
					.getString("INPUTTIME"));
			orderDetail.setInputTime(inputTime);
		}
		if (rs.getString("KENSA_GISI_NAME") != null) {
			orderDetail.setKensaGisiName(rs.getString("KENSA_GISI_NAME"));
		}
		if (rs.getString("UKETUKE_TANTOU_NAME") != null) {
			orderDetail.setUketukeTantouName(rs
					.getString("UKETUKE_TANTOU_NAME"));
		}
		if (rs.getString("RI_ORDER_FLG") != null) {
			orderDetail.setRiOrder(rs.getString("RI_ORDER_FLG"));
		}
		if (rs.getString("BYOUSITU") != null) {
			orderDetail.setByousitu(rs.getString("BYOUSITU"));
		}
		if (rs.getString("EXAMSTARTDATE") != null) {
			Date examStartDate = SimpleDateFormatType.FORMAT_YYYYMMDDHHMM_hi.parse(rs
					.getString("EXAMSTARTDATE"));
			orderDetail.setKensaStartDate(examStartDate);
		}
		if (rs.getString("EXAMENDDATE") != null) {
			Date examEndDate = SimpleDateFormatType.FORMAT_YYYYMMDDHHMM_hi.parse(rs
					.getString("EXAMENDDATE"));
			orderDetail.setKensaEndDate(examEndDate);
		}
		if (rs.getString("YOTEIKENSAROOM") != null) {
			orderDetail.setYoteiKensaRoom(rs.getString("YOTEIKENSAROOM"));
		}
		if (rs.getString("EXAMROOM_NAME") != null) {
			orderDetail.setKensaRoom(rs.getString("EXAMROOM_NAME"));
		}
		if (rs.getString("ORDER_DATE") != null) {
			Date orderDate = SimpleDateFormatType.FORMAT_YYYYMMDD.parse(rs
					.getString("ORDER_DATE"));
			orderDetail.setOrderDate(orderDate);
		}
		if (rs.getString("YOBIDASI_DATE") != null) {
			Date yobidasiDate = SimpleDateFormatType.FORMAT_YYYYMMDDHHMM_hi.parse(rs
					.getString("YOBIDASI_DATE"));
			orderDetail.setYobidasiDate(yobidasiDate);
		}

		if (rs.getString("STATUS") != null) {
			orderDetail.setStatus(rs.getString("STATUS"));
		}

		return orderDetail;
	}

	/**
	 * 既存の検査データがないかチェックする
	 *
	 * @param int kensaDate
	 * @param int checkStartTime
	 * @param int checkEndTime
	 * @param String
	 *            kensaType
	 * @param String
	 *            examRoom
	 * @return HashMap kanjaIDMapo
	 * @throws DBAccessException
	 */
	public ArrayList checkOrderExist(String risID, int kensaDate,
			int checkStartTime, int checkEndTime, String kensaType,
			String examRoom) throws DBAccessException {
		ArrayList kanjaIDList = new ArrayList();

		String sql = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		sql = "SELECT KANJA_ID" + " " + "FROM ORDERMAINTABLE" + " "
				+ "WHERE KENSA_DATE = ?" + " "
				+ "AND ( KENSA_STARTTIME BETWEEN ? AND ? )" + " "
				+ "AND KENSATYPE_ID = ?" + " " + "AND RIS_ID != ?" + " "
				+ "AND STATUS != -9" + " ";
		if (examRoom != null && examRoom.length() > 0)
			sql += "AND KENSASITU_ID = ?";

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setInt(1, kensaDate);
			stmt.setInt(2, checkStartTime);
			stmt.setInt(3, checkEndTime);
			stmt.setString(4, kensaType);
			stmt.setString(5, risID);
			if (examRoom != null && examRoom.length() > 0)
				stmt.setString(6, examRoom);

			rs = stmt.executeQuery();

			while (rs.next()) {
				String kanjaID = "";
				if (rs.getString("KANJA_ID") != null) {
					kanjaID = trimData(rs.getString("KANJA_ID"));
				}
				kanjaIDList.add(kanjaID);
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
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

		return kanjaIDList;
	}

	/**
	 * RIS識別IDを発番します
	 *
	 * @param
	 * @return RIS_ID
	 * @throws DBAccessException
	 */
	private String getRisID() throws DBAccessException {
		String RisID = "";
		String IdSeq = "";
		String sysDate = "";

		String sql = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		// RIS_ID取得SQL
		sql = "SELECT " + "RISIDSEQUENCE.NEXTVAL" + ", "
				+ "TO_CHAR (SYSDATE,'YYYYMMDD')" + " " + "FROM " + "DUAL";

		getLogger().debug(sql);

		try {
			conn = manager.borrowConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				IdSeq = rs.getString(1);
				sysDate = rs.getString(2);
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

		RisID = sysDate + IdSeq;

		return RisID;
	}

	/**
	 * StudyInstanceUIDを発番します
	 *
	 * @param RIS_ID
	 * @return StudyInstanceUID
	 * @throws DBAccessException
	 * @throws DBAccessException
	 */

	private String getStudyInstanceUID(String risID) throws DBAccessException {
		String siUID = "";

		String licenseNo = "";
		String sql = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		// SYSTEMDEFINEテーブルからLICENSENOを取得する
		sql = "SELECT LICENSENO FROM SYSTEMDEFINE";

		getLogger().debug(sql);

		try {
			conn = manager.borrowConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				licenseNo = rs.getString("LICENSENO");
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

		siUID = STUDY_INSTANCE_UID_PACKAGE_CODE + licenseNo + "." + risID;

		return siUID;
	}

	/**
	 * RIS_ORDER_SEQUENCEを採番します
	 *
	 * @param
	 * @return RIS_ORDER_SEQUENCE
	 * @throws DBAccessException
	 */
	private String getRisOrderSeq() throws DBAccessException {
		String RisOrderSeq = "";

		String sql = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		// RIS_ID取得SQL
		sql = "SELECT " + "RISORDERSEQUENCE.NEXTVAL" + " " + "FROM " + "DUAL";

		getLogger().debug(sql);
		try {
			conn = manager.borrowConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				RisOrderSeq = rs.getString(1);
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

		return RisOrderSeq;
	}

	public void deleteOrder(String risID) throws DBAccessException {
		Connection conn = null;
		PreparedStatement stmt = null;
		DBSessionManager manager = DBSessionManager.getInstance();
		String sql = "update OrderMainTable set Status = -9 where RIS_ID = ?";

		if (getLogger().isDebugEnabled()) {
			getLogger().debug(sql);
			getLogger().debug("  P1 : [" + risID + "]");
		}

		try {
			conn = manager.borrowConnection();

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, risID);

			stmt.executeUpdate();
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
	}
}
