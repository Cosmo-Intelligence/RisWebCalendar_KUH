package com.yokogawa.radiquest.ris.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.yokogawa.radiquest.ris.bean.OrderCalendarItem;
import com.yokogawa.radiquest.ris.servlet.SimpleDateFormatType;

public class CalenderAction {

	/**
	 * 時間枠リストを作成する。
	 *
	 * @param kensaStartTime
	 * @param kensaEndTime
	 * @param timeFrame
	 * @return
	 */
	public List createTimeFrameList(int kensaStartTime, int kensaEndTime,
			int timeFrame) {
		List timeFrameList = new ArrayList();
		try {
			int i = 0;
			timeFrameList.add("FREE");
			while (kensaStartTime < kensaEndTime) {
				// kensaStartTimeの値は時分。DBから取得される時間の値は時分秒なので秒00を加える意味で100をかける。
				timeFrameList.add(new Integer(kensaStartTime * 100));
				String strKensaStartTime = String.valueOf(kensaStartTime);
				StringBuffer stringBuffer = new StringBuffer(strKensaStartTime);
				while (strKensaStartTime.length() < 4) {
					// 800, や30等の4桁以下のkensaStartTimeが送られてきたら、頭に0をつけ4桁にする。
					stringBuffer = stringBuffer.insert(0, "0");
					strKensaStartTime = stringBuffer.toString();
				}
				Date startDateTime;

				startDateTime = SimpleDateFormatType.FORMAT_HHMM
						.parse(strKensaStartTime);

				Calendar cal = Calendar.getInstance();
				cal.setTime(startDateTime);

				// 年月日のみを取得
				String yyyymmdd = SimpleDateFormatType.FORMAT_YYYYMMDD
						.format(cal.getTime());
				// 設定された時間枠を足す。
				cal.add(Calendar.MINUTE, timeFrame);
				// 時間枠を足した後の年月日のみを取得
				String yyyymmdd1 = SimpleDateFormatType.FORMAT_YYYYMMDD
						.format(cal.getTime());

				// 年月日が変わる。つまり時計が１周して年月日が変わったらループはおしまい
				if (Integer.valueOf(yyyymmdd).intValue() < Integer.valueOf(
						yyyymmdd1).intValue()) {
					break;
				}
				strKensaStartTime = SimpleDateFormatType.FORMAT_HHMM.format(cal
						.getTime());
				kensaStartTime = Integer.valueOf(strKensaStartTime).intValue();
				i++;
			}
			timeFrameList.add(new Integer(kensaEndTime * 100));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return timeFrameList;
	}

	/**
	 * 検査開始日からの一週間分の日付リストを作成する。
	 *
	 * @param date
	 * @return
	 */
	public List createDateList(int date) {
		DateAction dateAction = new DateAction();
		List dateList = new ArrayList();
		for (int i = 0; i < 7; i++) {
			dateList.add(new Integer(dateAction.afterDate(date, i)));
		}
		return dateList;
	}

	/**
	 * 日付と時間ごとに検査をまとめたマップを返す。
	 *
	 * @param dateList
	 * @param orderCalenderList
	 * @return
	 */
	public HashMap createTimeDateOrderCalenderMap(List timeFrameList,
			List dateList, List orderCalenderList) {
		HashMap timeDateOrderCalenderMap = new HashMap();
		// 時間枠リスト
		for (int i = 0; i < timeFrameList.size() - 1; i++) {
			String timeFrame = timeFrameList.get(i).toString();
			// 範囲を決める為に次の時間枠を取得
			String nextTimeFrame = timeFrameList.get(i + 1).toString();

			HashMap dateOrderCalenderMap = new HashMap();
			// 日付リスト
			for (int j = 0; j < dateList.size(); j++) {
				int date = ((Integer) dateList.get(j)).intValue();
				List newOrderCalenderList = new ArrayList();
				for (int k = 0; k < orderCalenderList.size(); k++) {
					OrderCalendarItem orderCalenderItem = (OrderCalendarItem) orderCalenderList
							.get(k);
					int startTime = orderCalenderItem.getKensaStartTime();
					int kensadate = orderCalenderItem.getKensaDate();
					if (timeFrame.equalsIgnoreCase("FREE")) {
						if ((startTime == 999999) && (date == kensadate)) {
							newOrderCalenderList.add(orderCalenderItem);
						}
						continue;
					}
					// 時間範囲内かつで日付が同じ検査を追加
					if (startTime >= Integer.parseInt(timeFrame)
							&& startTime < Integer.parseInt(nextTimeFrame)
							&& date == kensadate) {
						newOrderCalenderList.add(orderCalenderItem);
						continue;
					}
					// 時間範囲内かつで日付が同じ検査が無かったらその日その時間は検査なし。
					if (k == orderCalenderList.size() - 1) {
						newOrderCalenderList.add(new OrderCalendarItem());
					}
				}
				// key:日にち value:keyと同じ検査日を持つ検査
				dateOrderCalenderMap.put(new Integer(date),
						newOrderCalenderList);
			}
			// key:時間 value:HashMap<Integer, List<OrderCalenderItem>>
			// calender.jspの記述の仕方が時間行ごとに記述となっているので
			timeDateOrderCalenderMap.put(timeFrame, dateOrderCalenderMap);
		}
		return timeDateOrderCalenderMap;
	}

}
