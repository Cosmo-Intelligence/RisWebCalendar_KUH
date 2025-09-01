package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yokogawa.radiquest.ris.bean.DayClassification;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

public class DayClassificationTableDataHome extends AbstractDataHome {
	public List<DayClassification> getDayClassifications() throws DBAccessException {
		List<DayClassification> list = new ArrayList<DayClassification>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();
		String sql = "select * from DayClassificationTable";

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				DayClassification dayClassification = new DayClassification();

				dayClassification.setDayOfWeek(rs.getInt("DAYOFWEEK"));
				dayClassification.setWeek01(rs.getInt("WEEK01"));
				dayClassification.setWeek02(rs.getInt("WEEK02"));
				dayClassification.setWeek03(rs.getInt("WEEK03"));
				dayClassification.setWeek04(rs.getInt("WEEK04"));
				dayClassification.setWeek05(rs.getInt("WEEK05"));
				if (rs.getMetaData().getColumnCount() >= 7)
					dayClassification.setWeek06(rs.getInt("WEEK06"));
				else
					dayClassification.setWeek06(9);

				list.add(dayClassification);
			}
		} catch (SQLException e) {
			getLogger().error("DayClassificationTable", e);
			throw new DBAccessException(e);
		} catch (DBAccessException e) {
			getLogger().error("DayClassificationTable", e);
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

		return list;
	}
}
