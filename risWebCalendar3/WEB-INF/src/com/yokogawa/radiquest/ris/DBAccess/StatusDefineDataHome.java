package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.yokogawa.radiquest.ris.bean.StatusDefine;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

public class StatusDefineDataHome extends AbstractDataHome {
	public ArrayList getStatusDefines()
			throws DBAccessException {
		StatusDefine statusDefine = null;

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList statusDefineList = new ArrayList();
		DBSessionManager manager = DBSessionManager.getInstance();
		String sql = "select StatusCode, Label, ShortLabel, Color"
				+ " from StatusDefine where StatusMode = 1";

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"StatusCode[" + rs.getString("StatusCode")
									+ "] Label[" + rs.getString("Label")
									+ "] ShortLabel["
									+ rs.getString("ShortLabel") + "]");
				}

				statusDefine = new StatusDefine();

				statusDefine.setStatusCode(rs.getInt("StatusCode"));
				statusDefine.setLabel(trimData(rs.getString("Label")));
				statusDefine
						.setShortLabel(trimData(rs.getString("ShortLabel")));
				statusDefine.setColor(rs.getString("Color"));

				statusDefineList.add(statusDefine);
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

		return statusDefineList;
	}
}
