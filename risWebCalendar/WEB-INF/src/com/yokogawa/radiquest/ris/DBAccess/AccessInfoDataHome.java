package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.yokogawa.radiquest.ris.bean.AccessInfo;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

//2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
public class AccessInfoDataHome extends AbstractDataHome {
	public AccessInfo selectAccessinfo(String risid)
			throws DBAccessException {

		AccessInfo accessInfo = new AccessInfo();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		StringBuilder sql = new StringBuilder("SELECT "
				+ "ID,APPID,IPADDRESS,ACCESSMODE,ENTRYTIME "
			+ "FROM "
				+ "ACCESSINFO "
			+ "where ID = ?" );

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			// パラメータセット
			stmt.setString(1, risid);
			getLogger().debug(sql.toString());
			rs = stmt.executeQuery();

			while (rs.next()) {
				if (rs.getString("ID") != null) {
					accessInfo.setID(rs.getString("ID"));
				}
				if (rs.getString("APPID") != null) {
					accessInfo.setAppID(rs.getString("ID"));
				}
				if (rs.getString("IPADDRESS") != null) {
					accessInfo.setIPAddress(rs.getString("IPADDRESS"));
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

		return accessInfo;
	}

	public int insertAccessinfo(String risid)
			throws DBAccessException {

		Connection conn = null;
		PreparedStatement stmt = null;
		DBSessionManager manager = DBSessionManager.getInstance();
		InetAddress addr = null;

		int i = 0;

		// ローカルIPアドレスを取得
		try {
		      addr = InetAddress.getLocalHost();
		    } catch (UnknownHostException e) {
		      e.printStackTrace();
	    }

        // 更新処理
        StringBuilder sql = new StringBuilder("INSERT INTO ACCESSINFO ( "
				+ "ID ,"
				+ "APPID,"
				+ "IPADDRESS,"
				+ "ACCESSMODE,"
				+ "ENTRYTIME "
				+ ") "
				+ "VALUES (?,?,?,?,SYSDATE)");

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			// パラメータセット
			stmt.setString(1, risid);
			stmt.setString(2, "W1");
			stmt.setString(3, addr.getHostAddress());
			stmt.setInt(4, 0);

			getLogger().debug(sql.toString());

			i = stmt.executeUpdate();

			if (i ==1) {
				//コミット
	            conn.commit();
			} else {
				conn.rollback();
			}

		} catch (SQLException e) {
			getLogger().error("ACCESSINFO", e);
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

	public int deleteAccessinfo(String risid)
			throws DBAccessException {

		Connection conn = null;
		PreparedStatement stmt = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		int i = 0;

        // 削除処理
		StringBuilder sql = new StringBuilder("DELETE "
			+ "FROM "
				+ "ACCESSINFO "
			+ "where ID = ?" );

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());

			// パラメータセット
			stmt.setString(1, risid);

			i = stmt.executeUpdate();

			if (i ==1) {
				//コミット
	            conn.commit();
			} else {
				conn.rollback();
			}

		} catch (SQLException e) {
			getLogger().error("ACCESSINFO", e);
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
}
//2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
