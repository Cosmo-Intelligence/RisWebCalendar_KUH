package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

//2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
public class ExamOperationHistoryDataHome extends AbstractDataHome {

	public int insertExamOperationHistory(String risid, String terminalname)
			throws DBAccessException {

		Connection conn = null;
		PreparedStatement stmt = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		int i = 0;

		// LOG_IDの取得
		String logId = selectLogId();

        // 更新処理
        StringBuilder sql = new StringBuilder("INSERT INTO EXAMOPERATIONHISTORY ( "
				+ "LOG_ID, "
				+ "RIS_ID, "
				+ "OPERATIONTYPE, "
				+ "OPERATOR, "
				+ "OPERATIONTERMINAL, "
				+ "OPERATIONTIME"
				+ " ) "
				+ "VALUES (?,?,?,?,?,SYSDATE)");

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			// パラメータセット
			stmt.setString(1, logId);
			stmt.setString(2, risid);
			stmt.setString(3, "114");
			stmt.setString(4, "risWebCalender");
			stmt.setString(5, terminalname);

			getLogger().debug(sql.toString());

			i = stmt.executeUpdate();

			if (i ==1) {
				//コミット
	            conn.commit();
			} else {
				conn.rollback();
			}

		} catch (SQLException e) {
			getLogger().error("EXAMOPERATIONHISTORY", e);
			throw new DBAccessException(e);
		} catch (DBAccessException e) {
			getLogger().error("", e);
			throw e;
		} finally {
			//クローズ処理
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

	private String selectLogId()
			throws DBAccessException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		String logId = null;

		StringBuilder sql = new StringBuilder("SELECT "
				+ "LOGSEQUENCE.NEXTVAL as NEWUID "
			+ "FROM "
				+ "dual ");

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			getLogger().debug(sql.toString());
			rs = stmt.executeQuery();

			while (rs.next()) {

				if (rs.getString("NEWUID") != null) {
					logId = rs.getString("NEWUID");
				}
			}

		} catch (SQLException e) {
			getLogger().error("ACCESSINFO", e);
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

		return logId;
	}
}
//2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
