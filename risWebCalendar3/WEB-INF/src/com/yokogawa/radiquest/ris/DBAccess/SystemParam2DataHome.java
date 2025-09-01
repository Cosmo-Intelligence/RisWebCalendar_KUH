package com.yokogawa.radiquest.ris.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DBSessionManager;

/**
 * SystemParam2 アクセスクラス
 *
 * @author T.Koudate@Cosmo
 * @version 1.1.19004
 * @since 2013.10.29 #1694
 *
 */
public class SystemParam2DataHome extends AbstractDataHome {

	// 2016.08.08 Mod T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
	public static final String EXTEND_RI_ORDER_FLG_NEEDLE_INSPECT	= "1";		// +注検
	public static final String EXTEND_RI_ORDER_FLG_FOLLOW			= "2";		// +追跡
	public static final String EXTEND_RI_ORDER_FLG_NI_FO			= "3";		// +注検+追跡
	//// 2014.05.07 Add T.Koudate@COSMO Start #2681
	//public static final String EXTEND_RI_ORDER_FLG_FOLLOW			= "1";		// +追跡
	//public static final String EXTEND_RI_ORDER_FLG_NEEDLE_INSPECT	= "2";		// +追跡+注検
	//// 2014.05.07 Add T.Koudate@COSMO End   #2681
	// 2016.08.08 Mod T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する
	// 2014.02.25 Add T.Koudate@Cosmo Start #2223
	public static final String HOLIDAY_MODE_COUNT	= "1";		// 回数判定
	public static final String HOLIDAY_MODE_WEEK	= "2";		// 週判定(V2)

	// 2014.05.07 Add T.Koudate@COSMO Start #2681
	/**
	 * RI区分の項目拡張.
	 * (VALUE1：拡張項目 1：追跡 2：追跡、注検 else：拡張しない)
	 * @return RI区分の項目拡張フラグ
	 * @throws DBAccessException
	 */
	public String getExtendRiOrderFlg() throws DBAccessException {
		String extendRiOrderFlg = "";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		StringBuffer sql = new StringBuffer("select VALUE1 from SYSTEMPARAM2"
				+ " where MAINID='SYSTEM' AND SUBID='EXTEND_RI_ORDER_FLG'");
		try{
			getLogger().debug(sql.toString());

			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());
			rs = stmt.executeQuery();

			if (rs.next())
			{
				extendRiOrderFlg = rs.getString("VALUE1");
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
		return extendRiOrderFlg;
	}
	// 2014.05.07 Add T.Koudate@COSMO End   #2681

	// 2014.02.25 Add T.Koudate@Cosmo Start #2223
	/**
	 * 休診日モード
	 * （Value1=1：回数判定(V1)　2：週判定(V2)
	 * @return 休診日モード
	 * @throws DBAccessException
	 */
	public String getHolidayMode() throws DBAccessException {
		String holidayMode = "";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBSessionManager manager = DBSessionManager.getInstance();

		StringBuffer sql = new StringBuffer("select VALUE1 from SYSTEMPARAM2"
				+ " where MAINID='SYSTEM' AND SUBID='HOLIDAY_MODE'");
		try{
			getLogger().debug(sql.toString());

			conn = manager.borrowConnection();
			stmt = conn.prepareStatement(sql.toString());
			rs = stmt.executeQuery();

			if (rs.next())
			{
				holidayMode = rs.getString("VALUE1");
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
		return holidayMode;
	}
	// 2014.02.25 Add T.Koudate@Cosmo End #2223
}
