package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yokogawa.radiquest.ris.bean.KensaType;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

public class SystemParamDataHome extends AbstractDataHome {

	/**
	 * SYSTEMPARAMマスタより患者ID桁数を取得する
	 * 
	 * @return kanjaIDDigit
	 */
	public String getKanjaIDDigit() throws DBAccessException {

		String kanjaIDDigit = "";
		String sql = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		sql = "SELECT VALUE1 FROM SYSTEMPARAM WHERE SUBID = 'KANJAID'";

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				kanjaIDDigit = rs.getString("VALUE1");
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

		return kanjaIDDigit;
	}
}
