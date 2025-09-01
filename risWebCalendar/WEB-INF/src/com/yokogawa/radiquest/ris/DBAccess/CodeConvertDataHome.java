package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.yokogawa.radiquest.ris.bean.CodeConvert;
import com.yokogawa.radiquest.ris.core.Configuration;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

public class CodeConvertDataHome extends AbstractDataHome {

	// 2014.05.07 Add T.Koudate@COSMO Start #2681
	/**
	 * RI区分フラグ：「なし」
	 */
	private static final String RI_ORDER_FLG_NONE = "0";
	// 2014.05.07 Add T.Koudate@COSMO End   #2681

	/**
	 * RI区分一覧を取得する
	 *
	 * @param sectionID
	 * @return
	 * @throws DBAccessException
	 */
	public ArrayList getRIOrder() throws DBAccessException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();
		ArrayList codeConvertList = new ArrayList();
		// 2014.05.07 Mod T.Koudate@COSMO Start #2681
		// ITEMVALUE=0(なし)も含め、ITEMVALUE順に並べる
		String sqlFormat = "SELECT ITEMVALUE, VALUELABEL FROM CODECONVERT WHERE ITEMID ='RIORDER' AND ITEMVALUE IN (%s) ORDER BY ITEMVALUE ASC";
		//StringBuffer sql = new StringBuffer(
		//		"SELECT ITEMVALUE, VALUELABEL FROM CODECONVERT WHERE ITEMID ='RIORDER' AND ITEMVALUE != 0");
		// 2014.05.07 Mod T.Koudate@COSMO Start #2681

		// 2014.05.07 Add T.Koudate@COSMO Start #2681
		// 2016.08.08 Del T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
		//int maxNum =  Configuration.getInstance().getExtendRiOrderMaxLengh();
		//String[] paramKey = new String[maxNum];
		//for (int i=0; i<maxNum; i++) {
		//	paramKey[i] = "?";
		//}
		// 2016.08.08 Del T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する
		
		// 2016.08.08 Add T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
		String[] riOrderFlgList = Configuration.getInstance().getRiOrderFlgList();
		if (null == riOrderFlgList){
			return codeConvertList;
		}

		String[] paramKey = new String[riOrderFlgList.length];
		for (int i=0; i<riOrderFlgList.length; i++) {
			paramKey[i] = "?";
		}
		// 2016.08.08 Add T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する

		// SQL置換
		String sql = String.format(sqlFormat, StringUtils.join(paramKey, ','));
		// 2014.05.07 Add T.Koudate@COSMO End   #2681

		getLogger().debug(sql.toString());

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());
			// 2016.08.08 Mod T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
			// パラメータ設定
			for(int i=0; i<riOrderFlgList.length; i++){
				stmt.setString(i+1, riOrderFlgList[i]);
			}
			//// 2014.05.07 Add T.Koudate@COSMO Start #2681
			//// パラメータ設定
			//for(int i=0; i<maxNum; i++){
			//	stmt.setInt(i+1, i);
			//}
			//// 2014.05.07 Add T.Koudate@COSMO End   #2681
			// 2016.08.08 Mod T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する
			rs = stmt.executeQuery();

			while (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"VALUELABEL[" + rs.getString("VALUELABEL")
									+ "] ITEMVALUE["
									+ rs.getString("ITEMVALUE"));
				}

				CodeConvert codeConvert = new CodeConvert();
				codeConvert.setRiOrder(rs.getString("VALUELABEL"));
				codeConvert.setItemValue(trimData(rs.getString("ITEMVALUE")));

				codeConvertList.add(codeConvert);
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
		return codeConvertList;
	}

	public String getRIOrder(String value) throws DBAccessException {
		if (value == null || value.length() == 0)
			return "";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();
		String riOrder = "";
		StringBuffer sql = new StringBuffer(
				"SELECT VALUELABEL FROM CODECONVERT WHERE ITEMID ='RIORDER' AND ITEMVALUE = ?");

		getLogger().debug(sql.toString());

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, value);
			rs = stmt.executeQuery();

			while (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"VALUELABEL[" + rs.getString("VALUELABEL") + "] ");
				}

				riOrder = rs.getString("VALUELABEL");
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
		return riOrder;
	}

	public String getSex(String value) throws DBAccessException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();
		String sex = "";
		StringBuffer sql = new StringBuffer(
				"SELECT VALUELABEL FROM CODECONVERT WHERE ITEMID ='SEX' and ITEMVALUE = ? ");

		getLogger().debug(sql.toString());

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, value);

			rs = stmt.executeQuery();

			while (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"VALUELABEL[" + rs.getString("VALUELABEL") + "]");
				}

				sex = rs.getString("VALUELABEL");
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
		return sex;
	}

	public String getTransPortType(String transport) throws DBAccessException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();
		String transPort = "";
		StringBuffer sql = new StringBuffer(
				"SELECT VALUELABEL FROM CODECONVERT WHERE ITEMID ='TRANSPORT' and ITEMVALUE = ? ");

		getLogger().debug(sql.toString());

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, transport);

			rs = stmt.executeQuery();

			while (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"VALUELABEL[" + rs.getString("VALUELABEL") + "]");
				}

				transPort = rs.getString("VALUELABEL");
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
		return transPort;
	}

	public String getNyugai(String nyugaiCode) throws DBAccessException {
		if (nyugaiCode == null || nyugaiCode.length() == 0)
			return "";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();
		String nyugai = "";
		StringBuffer sql = new StringBuffer(
				"SELECT VALUELABEL FROM CODECONVERT WHERE ITEMID ='INOUT' and ITEMVALUE = ? ");

		getLogger().debug(sql.toString());

		try {
			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, nyugaiCode);

			rs = stmt.executeQuery();

			while (rs.next()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"VALUELABEL[" + rs.getString("VALUELABEL") + "]");
				}

				nyugai = rs.getString("VALUELABEL");
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
		return nyugai;
	}
}
