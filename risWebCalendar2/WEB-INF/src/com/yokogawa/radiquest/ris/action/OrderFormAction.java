package com.yokogawa.radiquest.ris.action;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yokogawa.radiquest.ris.core.Configuration;
import com.yokogawa.radiquest.ris.servlet.Parameters;
import com.yokogawa.radiquest.ris.servlet.SimpleDateFormatType;

public class OrderFormAction extends FormAction {
	public OrderFormAction(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}

	public int getKensaDate() {
		int kensaDate = parseIntParam(Parameters.KENSA_DATE);
		return kensaDate;
	}

	public int getKensaStartTime() {
		int kensaStartTime = parseIntParam(Parameters.KENSA_START_TIME);
		return kensaStartTime;
	}

	public int getCheckStartTime() {
		int checkStartTime = -1;
		Date dConfStartTime = null;
		Date dKensaStartTime = null;
		Date dCheckStartTime = null;
		int timeFrame = parseIntParam(Parameters.TIMEFRAME);
		Configuration config = Configuration.getInstance();

		/* 設定ファイルの検査開始時間 */
		String strConfStartTime = String.valueOf(config.getStartTime());
		/* 設定ファイルの検査開始時間 */
		String strKensaStartTime = parseStringParam(Parameters.KENSA_START_TIME);
		// 4桁以下なら0padding
		for (int i = 0; i < 2; i++) {
			String str = "";
			StringBuffer sbf = null;
			switch (i) {
			case 0 :
				str = strConfStartTime;
				break;
			case 1 :
				str = strKensaStartTime;
				break;
			default :
				break;
			}
			while (str.length() < 4) {
				sbf = new StringBuffer(str);
				sbf = sbf.insert(0, "0");
				str = sbf.toString();
			}
			if (i == 0) {
				strConfStartTime = str;
			}
			else if (i == 1) {
				strKensaStartTime = str;
			}
		}

		// Date変換
		try {
			dConfStartTime = SimpleDateFormatType.FORMAT_HHMM.parse(strConfStartTime);
			dKensaStartTime = SimpleDateFormatType.FORMAT_HHMM.parse(strKensaStartTime);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		// 対象時間割り出し
		dCheckStartTime = dConfStartTime;

		while (dCheckStartTime.compareTo(dKensaStartTime) <= 0){
			Calendar cal = Calendar.getInstance();
			cal.setTime(dCheckStartTime);
			cal.add(Calendar.MINUTE, timeFrame);
			dCheckStartTime = cal.getTime();
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dCheckStartTime);
		// while文を抜けた時は1回多くループを回っているので、差し引く
		cal.add(Calendar.MINUTE, -(timeFrame));
		checkStartTime = cal.get(Calendar.HOUR_OF_DAY) * 10000 + cal.get(Calendar.MINUTE) * 100 + cal.get(Calendar.SECOND);

		return checkStartTime;
	}

	public int getCheckEndTime(int checkStartTime) {
		int checkEndTime = 0;
		String strCheckStartTime = "";
		Date dCheckStartTime = null;
		int timeFrame = parseIntParam(Parameters.TIMEFRAME);

		// hourの単位が下がる事を考慮して、Date型換算して取得する

		// 0padding
		strCheckStartTime = String.valueOf(checkStartTime);
		while (strCheckStartTime.length() < 6) {
			StringBuffer sbCheckStartTime = new StringBuffer(strCheckStartTime);
			sbCheckStartTime = sbCheckStartTime.insert(0, "0");
			strCheckStartTime = sbCheckStartTime.toString();
		}

		try {
			dCheckStartTime = SimpleDateFormatType.FORMAT_HHMMSS.parse(strCheckStartTime);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(dCheckStartTime);
		cal.add(Calendar.MINUTE, (timeFrame - 1) );
		checkEndTime = cal.get(Calendar.HOUR_OF_DAY) * 10000 + cal.get(Calendar.MINUTE) * 100 + cal.get(Calendar.SECOND);

		return checkEndTime;
	}
}
