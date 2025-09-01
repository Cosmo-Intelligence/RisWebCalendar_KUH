package com.yokogawa.radiquest.ris.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.yokogawa.radiquest.ris.servlet.SimpleDateFormatType;

public class DateAction {

	/**
	 * date(年月日) の after日後の年月日を返す。
	 * 
	 * @param date
	 * @param after
	 *            after日後
	 * @return
	 */
	public int afterDate(int date, int after) {
		// 月が1づつずれる。0 -> 1月
		date -= 100;
		String strDate = String.valueOf(date);
		while (strDate.length() < 8) {
			strDate = "0" + strDate;
		}
		String year = strDate.substring(0, 4);
		String month = strDate.substring(4, 6);
		String day = strDate.substring(6, 8);
		GregorianCalendar ct = new GregorianCalendar(Integer.valueOf(year)
				.intValue(), Integer.valueOf(month).intValue(), Integer
				.valueOf(day).intValue());

		// after日後にずらす。
		ct.add(Calendar.DATE, after);
		String afterDate = SimpleDateFormatType.FORMAT_YYYYMMDD.format(ct
				.getTime());
		return new Integer(afterDate).intValue();
	}
}
