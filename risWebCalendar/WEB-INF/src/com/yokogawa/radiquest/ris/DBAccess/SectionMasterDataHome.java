package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yokogawa.radiquest.ris.bean.Section;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

public class SectionMasterDataHome extends AbstractDataHome {
	public List getSectionList() throws DBAccessException {
		List sectionList = new ArrayList();

		String sql = "SELECT SECTION_ID, SECTION_NAME, SECTION_RYAKUNAME FROM SECTIONMASTER WHERE USEFLAG = 1 ORDER BY SHOWORDER";

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
				String sectionID = rs.getString("SECTION_ID");
				String sectionName = trimData(rs.getString("SECTION_NAME"));
				String sectionRyakuName = trimData(rs.getString("SECTION_RYAKUNAME"));

				Section section = new Section(sectionID, sectionName, sectionRyakuName);

				sectionList.add(section);
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

		return sectionList;
	}
}
