package com.yokogawa.radiquest.ris.servlet;

import java.text.SimpleDateFormat;

/**
 * SimpleDateFormat一覧クラス
 *
 * @author endo
 *
 */
public class SimpleDateFormatType {
	public static final SimpleDateFormat FORMAT_YYYYMMDDHHMM = new SimpleDateFormat(
			"yyyyMMddHHmm");
	public static final SimpleDateFormat FORMAT_YYYYMMDD = new SimpleDateFormat(
			"yyyyMMdd");
	public static final SimpleDateFormat FORMAT_HHMM = new SimpleDateFormat(
			"HHmm");
	public static final SimpleDateFormat FORMAT_HHMM_COLON = new SimpleDateFormat(
			"HH:mm");
	public static final SimpleDateFormat FORMAT_HHMMSS = new SimpleDateFormat(
			"HHmmss");

	public static final SimpleDateFormat FORMAT_YYYYMMDD_SLA = new SimpleDateFormat(
			"yyyy/MM/dd");
	public static final SimpleDateFormat FORMAT_YYYYMMDDE = new SimpleDateFormat(
			"yyyy/MM/dd(E)");

	public static final SimpleDateFormat FORMAT_YYYYMMDDHHMM_hi = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
}
