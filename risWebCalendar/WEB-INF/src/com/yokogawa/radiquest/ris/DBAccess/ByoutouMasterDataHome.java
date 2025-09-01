package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yokogawa.radiquest.ris.bean.Byoutou;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

public class ByoutouMasterDataHome extends AbstractDataHome {
	public List getByoutouList() throws DBAccessException {
		List byoutouList = new ArrayList();

		String sql = "SELECT BYOUTOU_ID, BYOUTOU_NAME, BYOUTOU_RYAKUNAME FROM BYOUTOUMASTER WHERE USEFLAG = 1 ORDER BY SHOWORDER";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		getLogger().debug(sql);
		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				String byoutouID = rs.getString("BYOUTOU_ID");
				String byoutouName = trimData(rs.getString("BYOUTOU_NAME"));
				String byoutouRyakuName = trimData(rs.getString("BYOUTOU_RYAKUNAME"));

				Byoutou section = new Byoutou(byoutouID, byoutouName, byoutouRyakuName);

				byoutouList.add(section);
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

		return byoutouList;
	}
}
