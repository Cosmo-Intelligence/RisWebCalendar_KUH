package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.yokogawa.radiquest.ris.bean.KensaType;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

public class KensaTypeDataHome extends AbstractDataHome {
	/**
	 * 検査種別マスタより検査種別リストを取得する
	 * 
	 * @return kensaTypeList
	 */
	public ArrayList getKensaTypeList() throws DBAccessException {
		ArrayList kensaTypeList = new ArrayList();

		String kensaTypeID = "";
		String kensaTypeName = "";
		String kensaTypeNameShort = "";

		StringBuffer sql = new StringBuffer(
				"SELECT KENSATYPE_ID, KENSATYPE_NAME, KENSATYPE_RYAKUNAME FROM KENSATYPEMASTER WHERE USEFLAG = 1 ORDER BY SHOWORDER");
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		getLogger().debug(sql.toString());
		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());
			rs = stmt.executeQuery();

			while (rs.next()) {
				kensaTypeID = rs.getString("KENSATYPE_ID");
				kensaTypeName = trimData(rs.getString("KENSATYPE_NAME"));
				kensaTypeNameShort = trimData(rs
						.getString("KENSATYPE_RYAKUNAME"));

				KensaType ktBean = new KensaType(kensaTypeID, kensaTypeName,
						kensaTypeNameShort);
				kensaTypeList.add(ktBean);
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

		return kensaTypeList;
	}
}