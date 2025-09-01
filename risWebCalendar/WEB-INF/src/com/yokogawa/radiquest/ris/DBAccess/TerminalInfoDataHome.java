package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.yokogawa.radiquest.ris.bean.TerminalInfo;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

//2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
public class TerminalInfoDataHome extends AbstractDataHome {
	public TerminalInfo selectTerminalInfo(String Ipaddress)
			throws DBAccessException {

		TerminalInfo terminalInfo = new TerminalInfo();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		StringBuilder sql = new StringBuilder("SELECT "
				+ "TERMINALID,TERMINALNAME,TERMINALADDRESS,ENTRYDATE,EXPLANATION,SHOWORDER,KAKUHO_ROOM "
			+ "FROM "
				+ "TERMINALINFO "
			+ "where TERMINALADDRESS = ?" );

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			// パラメータセット
			stmt.setString(1, Ipaddress);

			getLogger().debug(sql.toString());
			rs = stmt.executeQuery();

			while (rs.next()) {
				if (rs.getString("TERMINALID") != null) {
					terminalInfo.setterminalID(rs.getInt("TERMINALID"));
				}
				if (rs.getString("TERMINALNAME") != null) {
					terminalInfo.setterminalName(rs.getString("TERMINALNAME"));
				}
				if (rs.getString("TERMINALADDRESS") != null) {
					terminalInfo.setterminalipAddress(rs.getString("TERMINALADDRESS"));
				}
				if (rs.getString("ENTRYDATE") != null) {
					terminalInfo.setentryDate(rs.getDate("ENTRYDATE"));
				}
				if (rs.getString("EXPLANATION") != null) {
					terminalInfo.setexPlanation(rs.getString("EXPLANATION"));
				}
				if (rs.getString("SHOWORDER") != null) {
					terminalInfo.setshowOrder(rs.getInt("SHOWORDER"));
				}
				if (rs.getString("KAKUHO_ROOM") != null) {
					terminalInfo.setkakuhoRoom(rs.getString("KAKUHO_ROOM"));
				}
			}

		} catch (SQLException e) {
			getLogger().error("StatusDefine", e);
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

		return terminalInfo;
	}

}
//2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
