package com.yokogawa.radiquest.ris.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 和暦変換クラス
 * 
 * @author endo
 * 
 */
public class JapaneseEraConverter {
	/* 明治 */
	private static final int MEIJI_y = 1868;
	private static final int MEIJI_YMD = 18680908;

	/* 大正 */
	private static final int TAISHOU_Y = 1912;
	private static final int TAISHOU_YMD = 19120730;

	/* 昭和 */
	private static final int SHOUWA_y = 1926;
	private static final int SHOUWA_YMD = 19261225;

	/* 平成 */
	private static final int HEISEI_y = 1989;
	private static final int HEISEI_YMD = 19890108;

	private String gengou;

	private String japaneseEra;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yy年MM月dd日");
	private SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyMMdd");
	private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(
			"yyy年MM月dd日");
	private SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyMMdd");
	private SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat(
			"yyyy年MM月dd日");
	private SimpleDateFormat simpleDateFormat5 = new SimpleDateFormat(
			"yyyyMMdd");

	public String getJapaneseEra() {
		return japaneseEra;
	}

	public String getGengou() {
		return gengou;
	}

	public void changeToJapaneseEra(Date date) {
		String yyyymmdd = simpleDateFormat5.format(date);
		String strYear = yyyymmdd.substring(0, 4);
		String mmdd = yyyymmdd.substring(4, yyyymmdd.length());
		int ymd = Integer.valueOf(yyyymmdd).intValue();
		int year = Integer.valueOf(strYear).intValue();
		int warekiToshi = 0;
		String strWareki = "";
		if (MEIJI_YMD <= ymd && ymd < TAISHOU_YMD) {
			gengou = "明治";
			warekiToshi = year - MEIJI_y + 1;
			strWareki = String.valueOf(warekiToshi);
		}
		if (TAISHOU_YMD <= ymd && ymd < SHOUWA_YMD) {
			gengou = "大正";
			warekiToshi = year - TAISHOU_Y + 1;
			strWareki = String.valueOf(warekiToshi);
		}
		if (SHOUWA_YMD <= ymd && ymd < HEISEI_YMD) {
			gengou = "昭和";
			warekiToshi = year - SHOUWA_y + 1;
			strWareki = String.valueOf(warekiToshi);
		}
		if (HEISEI_YMD <= ymd) {
			gengou = "平成";
			warekiToshi = year - HEISEI_y + 1;
			strWareki = String.valueOf(warekiToshi);
		}
		if (ymd < MEIJI_YMD) {
			// 明治以前の場合は西暦のまま
			japaneseEra = yyyymmdd;
			return;
		}
		StringBuffer stringBuffer = new StringBuffer(strWareki);
		stringBuffer.append(mmdd);
		japaneseEra = stringBuffer.toString();
		if (japaneseEra.length() == 5) {
			stringBuffer.insert(0, "0");
			japaneseEra = stringBuffer.toString();
		}
	}

	public String getJapaneseEraBirthDay(Date birthDay) {
		changeToJapaneseEra(birthDay);
		String strWareki = japaneseEra;
		Date dateWareki = null;
		try {
			if (strWareki.length() <= 6) {
				dateWareki = simpleDateFormat1.parse(strWareki);
				strWareki = simpleDateFormat.format(dateWareki);
			} else if (strWareki.length() == 7) {
				dateWareki = simpleDateFormat3.parse(strWareki);
				strWareki = simpleDateFormat2.format(dateWareki);
			} else if (strWareki.length() == 8) {
				dateWareki = simpleDateFormat5.parse(strWareki);
				strWareki = simpleDateFormat4.format(dateWareki);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String japaneseEraBirthDay = strWareki;
		if (gengou != null && gengou.length() != 0) {
			StringBuffer warekiBf = new StringBuffer(gengou);
			warekiBf.append(strWareki);
			japaneseEraBirthDay = warekiBf.toString();
		}
		return japaneseEraBirthDay;
	}
}
