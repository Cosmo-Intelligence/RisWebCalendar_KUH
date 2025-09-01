package com.yokogawa.radiquest.ris.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionFactory {
	private String url;

	private String username;

	private String password;

	DBConnectionFactory(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public Connection makeObject() throws DBAccessException {
		Connection con = null;
		try {
			con = createConnection();
		} catch (java.sql.SQLException e) {
			throw new DBAccessException(e);
		}
		return con;
	}

	public void destroyObject(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
		}
	}

	private Connection createConnection() throws SQLException {
		Connection con = null;
		Class jdbcDriver = null;
		try {
			jdbcDriver = Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
		}
		return con;
	}
}
