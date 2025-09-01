package com.yokogawa.radiquest.ris.core;

import java.sql.Connection;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DBSessionManager {
	static private DBSessionManager INSTANCE = new DBSessionManager();

	private int maxConDef;

	private DBConnectionHandler connectionHandler;

	private DBConnectionFactory connectionFactory;

	private DBSessionManager() {
	}

	public void init(Document doc) throws CannotInitializeException {
		XmlParser parser = new XmlParser();
		String connectionUrl = null;
		String username = null;
		String password = null;

		try {
			Node dbAccessNode = parser.getTargetElement(doc, "DBAccess");

			// Url
			Node n = parser.getTargetElement(dbAccessNode, "Connection");
			connectionUrl = parser.getTextNodeValue(n);

			// User
			n = parser.getTargetElement(dbAccessNode, "User");
			username = parser.getTextNodeValue(n);

			// Password
			n = parser.getTargetElement(dbAccessNode, "Password");
			password = parser.getTextNodeValue(n);

			if (connectionUrl == null || connectionUrl.length() == 0) {
				throw new CannotInitializeException();
			}
			if (username == null || username.length() == 0) {
				throw new CannotInitializeException();
			}
			if (password == null || password.length() == 0) {
				throw new CannotInitializeException();
			}

			Node maxActiveNode = parser.getTargetElement(dbAccessNode,
					"maxConnectionNumber");
			String maxActiveStr = parser.getTextNodeValue(maxActiveNode);
			maxConDef = Integer.parseInt(maxActiveStr);
			if (maxConDef < 1) {
				throw new CannotInitializeException();
			}
			connectionFactory = new DBConnectionFactory(connectionUrl,
					username, password);
			connectionHandler = DBConnectionHandler.getInstance();
			connectionHandler.setMaximumConnection(maxConDef);
			connectionHandler.setConnectionFactory(connectionFactory);
		} catch (NumberFormatException e) {
			throw new CannotInitializeException(e);
		}
	}

	synchronized public void closeConnection() throws Exception {
		connectionHandler.closeAllConnections();
	}

	public Connection borrowConnection() throws DBAccessException {
		try {
			return connectionHandler.borrowObject();
		} catch (Exception e) {
			throw new DBAccessException(e);
		}
	}

	public void returnConnection(Connection connection)
			throws DBAccessException {
		try {
			connectionHandler.returnObject(connection);
		} catch (Exception e) {
			throw new DBAccessException(e);
		}
	}

	static public DBSessionManager getInstance() {
		return INSTANCE;
	}
}
