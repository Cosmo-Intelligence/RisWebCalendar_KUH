package com.yokogawa.radiquest.ris.core;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class DBConnectionHandler {
	static private DBConnectionHandler INSTANCE = new DBConnectionHandler();

	private int maxConDef = 0;

	private Hashtable m_htblConnectionHolders_ = new Hashtable();

	private static final String HOLDER_TABLE_KEY = "nextas-db";

	private Map m_mapLocks_ = new HashMap();

	private static final String LOCK_TABLE_KEY = "nextas-db";

	private DBConnectionFactory m_connectionFactory_ = null;

	private DBConnectionHandler() {
	}

	public void setConnectionFactory(DBConnectionFactory conFac) {
		if (conFac != null) {
			m_connectionFactory_ = conFac;
		}
	}

	public void setMaximumConnection(int nMaxCon) {
		if (nMaxCon > 0) {
			this.maxConDef = nMaxCon;
		}
	}

	private void lock(String key) {
		Lock lock;
		synchronized (m_mapLocks_) {
			lock = (Lock) m_mapLocks_.get(key);
			if (lock == null) {
				lock = new Lock(false, 0);
				m_mapLocks_.put(key, lock);
			}

			synchronized (lock) {
				lock.m_nReferenceCounter++;
			}
		}
		synchronized (lock) {
			while (lock.m_boolIsLocked == true) {
				try {
					lock.wait();
				} catch (Exception e) {
				}
			}
			lock.m_boolIsLocked = true;
		}
	}

	private void unlock(String key) {
		synchronized (m_mapLocks_) {
			Lock lock = (Lock) m_mapLocks_.get(key);
			if (lock == null) {
				return;
			}

			synchronized (lock) {
				lock.m_boolIsLocked = false;
				lock.m_nReferenceCounter--;
				if (lock.m_nReferenceCounter <= 0) {
					m_mapLocks_.remove(key);
					return;
				}
				try {
					lock.notify();
				} catch (Exception e) {
				}
			}
		}
	}

	public Connection borrowObject() throws Exception {
		int nLoopCounter = 0;
		Connection con = null;
		DBConnectionHolder holder = null;

		while (con == null) {
			try {
				lock(LOCK_TABLE_KEY);
				if (hasConnection(HOLDER_TABLE_KEY) == true) {
					holder = getConnectionHolder(HOLDER_TABLE_KEY);
				} else {
					holder = createNewConnectionHolder(HOLDER_TABLE_KEY);
				}

				if (holder != null) {
					if (holder.hasIdleConnection() == true) {
						con = holder.getConnection();
					}

					if (con == null) {
						if (holder.isMaxCount(maxConDef) == false) {
							con = holder.addNewConnection(m_connectionFactory_
									.makeObject());
						}
					}

					returnConnectionHolder(HOLDER_TABLE_KEY, holder);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				unlock(LOCK_TABLE_KEY);
				nLoopCounter++;
			}

			if (con == null) {
				try {
					Thread.sleep(5);
				} catch (Exception e) {
				}
			}
			if (nLoopCounter >= 100) {
				throw new Exception("DBConnectionHandler#borrowObject() Timeout");
			}
		}
		return con;
	}

	public void returnObject(Connection con) throws Exception {
		DBConnectionHolder holder = null;
		try {
			lock(LOCK_TABLE_KEY);

			if (hasConnection(HOLDER_TABLE_KEY)) {
				holder = getConnectionHolder(HOLDER_TABLE_KEY);
			}

			if (holder != null) {
				holder.returnConnection(con);
				returnConnectionHolder(HOLDER_TABLE_KEY, holder);
			}
		} catch (Exception e) {
			throw new Exception();
		} finally {
			unlock(LOCK_TABLE_KEY);
		}
	}

	public synchronized void closeAllConnections() {
		Enumeration enumMsgTypes = null;
		enumMsgTypes = m_htblConnectionHolders_.keys();
		while (enumMsgTypes.hasMoreElements()) {
			String key = (String) enumMsgTypes.nextElement();

			try {
				lock(LOCK_TABLE_KEY);
				DBConnectionHolder holder = getConnectionHolder(key);

				if (holder != null) {
					while (holder.hasIdleConnection()) {
						Connection con = holder.getConnection();
						if (con != null) {
							m_connectionFactory_.destroyObject(con);
						}
					}
				}
			} catch (Exception e) {
			} finally {
				unlock(LOCK_TABLE_KEY);
			}
		}
	}

	private boolean hasConnection(String msgType) {
		boolean retVal = false;
		if (msgType != null) {
			retVal = m_htblConnectionHolders_.containsKey(msgType);
		}
		return retVal;
	}

	private DBConnectionHolder getConnectionHolder(String key) {
		DBConnectionHolder holder = null;

		if (key != null) {
			if (!m_htblConnectionHolders_.get(key).equals(new String(""))) {
				holder = (DBConnectionHolder) m_htblConnectionHolders_.get(key);
			}
		}
		if (holder != null) {
			m_htblConnectionHolders_.put(key, new String(""));
		}
		return holder;
	}

	private void returnConnectionHolder(String key, DBConnectionHolder holder) {
		if (key != null && holder != null) {
			m_htblConnectionHolders_.put(key, holder);
		}
	}

	private DBConnectionHolder createNewConnectionHolder(String key) {
		DBConnectionHolder holder = null;
		if (key != null) {
			if (hasConnection(key) != true) {
				holder = new DBConnectionHolder();
				m_htblConnectionHolders_.put(key, holder);
			}
		}
		return holder;
	}

	static public DBConnectionHandler getInstance() {
		return INSTANCE;
	}

	class Lock {
		public boolean m_boolIsLocked = false;

		public int m_nReferenceCounter = 0;

		Lock(boolean isLocked, int referenceCounter) {
			m_boolIsLocked = isLocked;
			m_nReferenceCounter = referenceCounter;
		}
	}
}
