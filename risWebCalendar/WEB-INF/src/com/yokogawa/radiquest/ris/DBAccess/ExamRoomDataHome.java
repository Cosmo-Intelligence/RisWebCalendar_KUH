package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.yokogawa.radiquest.ris.bean.ExamRoom;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;
import com.yokogawa.radiquest.ris.core.ReservationFormLoggerFactory;

public class ExamRoomDataHome extends AbstractDataHome {
	/**
	 * 検査室マスタより検査室リストを取得する
	 *
	 * @return examRoomList
	 */
	public ArrayList getExamRoomList(String[] kensaTypes)
			throws DBAccessException {
		ArrayList examRoomList = new ArrayList();

		String examRoomID = "";
		String examRoomName = "";
		String examRoomRyakuName = "";

		String sql = "";
		String sqlwhere1 = "";
		String sqlwhere2 = "";
		String sqlwhere3 = "";
		String sqlwhere4 = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		sql = "SELECT " + "EXAMROOM_ID" + ", " + "EXAMROOM_NAME" + ", "
				+ "EXAMROOM_RYAKUNAME" + " " + "FROM " + "EXAMROOMMASTER" + " "
				+ "WHERE " + "USEFLAG = 1";

		for (int i = 0; i < kensaTypes.length; i++) {
			if (i == 0) {
				sql += "AND (";
			} else {
				sql += "OR";
			}
			sql += " KENSATYPE_ID = ?" + " OR KENSATYPE_ID like ?"
					+ " OR KENSATYPE_ID like ?" + " OR KENSATYPE_ID like ?";

			if (i == kensaTypes.length - 1) {
				sql += ")";
			}
		}
		sql += " ORDER BY SHOWORDER";


		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);

			int cnt = 1;
			for (int i = 0; i < kensaTypes.length; i++) {
				sqlwhere1 = kensaTypes[i];
				sqlwhere2 = kensaTypes[i] + ",%";
				sqlwhere3 = "%," + kensaTypes[i] + ",%";
				sqlwhere4 = "%," + kensaTypes[i];
				stmt.setString(cnt, sqlwhere1);
				cnt++;
				stmt.setString(cnt, sqlwhere2);
				cnt++;
				stmt.setString(cnt, sqlwhere3);
				cnt++;
				stmt.setString(cnt, sqlwhere4);
				cnt++;

				if (getLogger().isDebugEnabled()) {
					getLogger().debug("  P1 : [" + sqlwhere1 + "]");
					getLogger().debug("  P2 : [" + sqlwhere2 + "]");
					getLogger().debug("  P3 : [" + sqlwhere3 + "]");
					getLogger().debug("  P4 : [" + sqlwhere4 + "]");
				}
			}
			getLogger().debug(sql);

			rs = stmt.executeQuery();

			while (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"EXAMROOM_ID[" + rs.getString("EXAMROOM_ID")
									+ "] EXAMROOM_NAME["
									+ rs.getString("EXAMROOM_NAME")
									+ "] EXAMROOM_RYAKUNAME["
									+ rs.getString("EXAMROOM_RYAKUNAME") + "]");
				}

				examRoomID = rs.getString("EXAMROOM_ID");
				examRoomName = trimData(rs.getString("EXAMROOM_NAME"));
				examRoomRyakuName = trimData(rs.getString("EXAMROOM_RYAKUNAME"));

				ExamRoom erBean = new ExamRoom(examRoomID, examRoomName,
						examRoomRyakuName);
				examRoomList.add(erBean);
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

		return examRoomList;
	}

	/**
	 * 検査室マスタより検査タイプIDを取得する カンマ区切りで複数指定されている可能性があるので、分割したものをリストにして返す
	 *
	 * @return kensaTypeIDList
	 * @throws DBAccessException
	 */
	public ArrayList getkensaTypeIDList() throws DBAccessException {
		ArrayList kensaTypeIDList = new ArrayList();
		String kensaTypeID = "";

		String sql = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		sql = "SELECT " + "KENSATYPE_ID" + "FROM " + "EXAMROOMMASTER" + " "
				+ "WHERE " + "EXAMROOM_ID = ?";

		getLogger().debug(sql);

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);

			rs = stmt.executeQuery();

			if (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("KENSATYPE_ID[" + rs.getString(1) + "]");
				}

				kensaTypeID = rs.getString(1);
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
				e.printStackTrace();
				Logger logger = ReservationFormLoggerFactory.getInstance()
						.getLogger();
				logger.debug("検査項目の取得に失敗！！");
			}
			manager.returnConnection(conn);
		}
		kensaTypeIDList = stringToken(kensaTypeID);
		return kensaTypeIDList;
	}

	/**
	 * 検査室マスタより検査機器IDを取得する カンマ区切りで複数指定されている可能性があるので、分割したものをリストにして返す
	 *
	 * @return kensaKikiIDList
	 * @throws DBAccessException
	 */
	public ArrayList getkensaKikiIDList(String examRoomID)
			throws DBAccessException {
		ArrayList kensaKikiIDList = new ArrayList();
		String kensaKikiID = "";

		String sql = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		sql = "SELECT " + "KENSAKIKI_ID " + "FROM " + "EXAMROOMMASTER" + " "
				+ "WHERE " + "EXAMROOM_ID = ?";

		getLogger().debug(sql);
		getLogger().debug("  P1 : [" + examRoomID + "]");

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, examRoomID);
			rs = stmt.executeQuery();

			if (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("KENSAKIKI_ID[" + rs.getString(1) + "]");
				}

				kensaKikiID = rs.getString(1);
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
				e.printStackTrace();
				Logger logger = ReservationFormLoggerFactory.getInstance()
						.getLogger();
				logger.debug("検査項目の取得に失敗！！");
			}
			manager.returnConnection(conn);
		}

		if (kensaKikiID != null)
			kensaKikiIDList = stringToken(kensaKikiID);

		return kensaKikiIDList;
	}

	/**
	 * 文字列を「,」で分割してリストにして返す
	 */
	protected ArrayList stringToken(String data) {
		ArrayList list = new ArrayList();
		String divideData = null;

		// 「,」をデリミタとして分割
		StringTokenizer st = new StringTokenizer(data, ",");

		// トークンし値を取得
		while (st.hasMoreTokens()) {
			divideData = st.nextToken();
			list.add(divideData);
		}
		return list;

	}

	public String getExamRoomRyakuName(String examRoomID)
			throws DBAccessException {
		String examRoomRyakuName = "";

		String sql = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		sql = "SELECT " + "EXAMROOM_RYAKUNAME " + "FROM " + "EXAMROOMMASTER"
				+ " " + "WHERE " + "EXAMROOM_ID = ?";

		getLogger().debug(sql);

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, examRoomID);
			rs = stmt.executeQuery();

			if (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"EXAMROOM_RYAKUNAME[" + rs.getString(1) + "]");
				}

				examRoomRyakuName = trimData(rs.getString(1));
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
				e.printStackTrace();
				Logger logger = ReservationFormLoggerFactory.getInstance()
						.getLogger();
				logger.debug("検査室略名の取得に失敗！！");
			}
			manager.returnConnection(conn);
		}
		return examRoomRyakuName;
	}
}
