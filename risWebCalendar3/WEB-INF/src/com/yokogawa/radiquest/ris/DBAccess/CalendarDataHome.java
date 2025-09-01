package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

public class CalendarDataHome extends AbstractDataHome {
	public ArrayList getKyusinbiList() throws DBAccessException {
		ArrayList kyusinbiList = new ArrayList();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		String sql = "SELECT HIZUKE FROM CALENDARMASTER";

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);

			rs = stmt.executeQuery();
			while (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("HIZUKE[" + rs.getString("HIZUKE") + "]");
				}

				String kyusinbi = rs.getString("HIZUKE");

				kyusinbiList.add(kyusinbi);
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

		return kyusinbiList;
	}
}