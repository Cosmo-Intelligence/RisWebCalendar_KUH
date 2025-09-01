package com.yokogawa.radiquest.ris.core;

import java.sql.Connection;
import java.util.ArrayList;

public class DBConnectionHolder {
	private int curConnectionCount = 0;

	private ArrayList connectionList = new ArrayList();

	public DBConnectionHolder() {
	}

	public boolean isMaxCount(int maxDef) {
		boolean retVal = true;
		if (curConnectionCount < maxDef) {
			retVal = false;
		}
		return retVal;
	}

	public boolean hasIdleConnection() {
		boolean retVal = false;
		if (connectionList.size() > 0) {
			retVal = true;
		}
		return retVal;
	}

	public Connection addNewConnection(Connection con) {
		if (con != null) {
			connectionList.add(con);
			curConnectionCount++;
		}

		return getConnection();
	}

	public Connection getConnection() {
		Connection con = null;
		if (hasIdleConnection()) {
			con = (Connection) connectionList.remove(0);
		}
		return con;
	}

	public void returnConnection(Connection con) {
		if (con != null) {
			connectionList.add(con);
		}
	}
}
