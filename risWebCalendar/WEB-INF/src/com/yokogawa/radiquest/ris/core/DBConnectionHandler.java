package com.yokogawa.radiquest.ris.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		// 2012.06.14 Mod Yk.Suzuki@CIJ Start コネクションプーリング無しモード追加
		if (nMaxCon >= 0) {
		//if (nMaxCon > 0) {
		// 2012.06.14 Mod Yk.Suzuki@CIJ End   コネクションプーリング無しモード追加
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
						// 2012.06.14 Add Yk.Suzuki@CIJ Start コネクションプーリング無しモード追加
						if (0 == maxConDef){
							// 新規作成
							con = m_connectionFactory_.makeObject();
						}
						else
						// 2012.06.14 Add Yk.Suzuki@CIJ End   コネクションプーリング無しモード追加
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

			// 2012.06.14 Add Yk.Suzuki@CIJ Start コネクション接続チェック
			if (null != con) {
				if (false == isDbConnection(con)) {
					// 無効の場合は破棄
					m_connectionFactory_.destroyObject(con);

					// 接続を初期化し、次の取得ループへ
					con = null;
				}
			}
			// 2012.06.14 Add Yk.Suzuki@CIJ End   コネクション接続チェック

			if (con == null) {
				try {
					// 2012.06.14 Add Yk.Suzuki@CIJ Start 接続再取得間隔を調整
					Thread.sleep(80);
					//Thread.sleep(5);
					// 2012.06.14 Add Yk.Suzuki@CIJ End   接続再取得間隔を調整
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
				// 2012.06.14 Mod Yk.Suzuki@CIJ Start コネクションプーリング無しモード追加
				if (0 == maxConDef) {
					// プーリングなしモードの時は、即時解放する
					try{
						m_connectionFactory_.destroyObject(con);
					}
					catch(Exception e2){
					}
				}
				else{
					// プーリング領域に戻す
					holder.returnConnection(con);
				}
				//holder.returnConnection(con);
				// 2012.06.14 Mod Yk.Suzuki@CIJ End   コネクションプーリング無しモード追加

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

	// 2012.06.14 Add Yk.Suzuki@CIJ Start コネクション接続チェック
	/**
	 * DB接続可能検査
	 * @param con DB接続
	 * @return true-DB接続可能, false-DB接続不可
	 */
	private boolean isDbConnection(Connection con)
	{
		boolean retBool = false;
		String sqlStr = "SELECT SYSDATE FROM DUAL";

		PreparedStatement stmt = null;
		ResultSet rs = null;
		if (null != con){
			// 試しにSQLを実行する
			try{
				stmt = con.prepareStatement(sqlStr);
				rs = stmt.executeQuery();

				if (rs.next()) {
					// SQL取得可能
					retBool = true;
				}
			}
			catch(Exception ex) {
			}
			finally {
				try {
					if (null != rs) {
						rs.close();
					}
					if (null != stmt) {
						stmt.close();
					}
				} catch (Exception ex) {
				}
			}
		}

		// 接続結果を返す
		return retBool;
	}
	// 2012.06.14 Add Yk.Suzuki@CIJ End   コネクション接続チェック

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
