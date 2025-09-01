package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yokogawa.radiquest.ris.bean.Bui;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

public class ExBuiTableDataHome extends AbstractDataHome {
	public List getBuiList(String risID) throws DBAccessException {
		List buiList = new ArrayList();

		String sql = "SELECT EBT.NO, EBT.BUISET_ID, BSM.BUISET_NAME FROM ExBuiTable EBT, BuiSetMaster BSM"
			+ " WHERE EBT.RIS_ID = ? AND EBT.BUISET_ID = BSM.BUISET_ID (+)"
			+ " AND EBT.SATUEISTATUS != 2"
			+ " ORDER BY EBT.NO";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		getLogger().debug(sql);
		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, risID);
			rs = stmt.executeQuery();

			while (rs.next()) {
				int no = rs.getInt("NO");
				String buiSetID = rs.getString("BUISET_ID");
				if (buiSetID == null)
					buiSetID = "";
				String buiSetName = rs.getString("BUISET_NAME");
				if (buiSetName == null)
					buiSetName = "";

				Bui bui = new Bui(no, buiSetID, buiSetName);

				buiList.add(bui);
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

		return buiList;
	}

}
