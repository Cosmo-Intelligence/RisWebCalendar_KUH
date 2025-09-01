package com.yokogawa.radiquest.ris.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;
import com.yokogawa.radiquest.ris.bean.ExamRoom;
import com.yokogawa.radiquest.ris.bean.OrderCalendarItem;
import com.yokogawa.radiquest.ris.core.Configuration;
import com.yokogawa.radiquest.ris.core.ReservationFormLoggerFactory;
import com.yokogawa.radiquest.ris.servlet.SimpleDateFormatType;

public class CalenderAction {

	// 2015.02.27 Add S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
	private static final int COL_MIN_COUNT = 4;
	// 2015.02.27 Add S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
	
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

				// 年月日のみを取得
				String yyyymmdd = SimpleDateFormatType.FORMAT_YYYYMMDD
						.format(startDateTime);
				// 設定された時間枠を足す。
				long milliTime = startDateTime.getTime();
				long timeFrameMilli = timeFrame*60*1000;
				long nextMilliTime  = milliTime+ timeFrameMilli;
				Date nextstartDateTime = new Date(nextMilliTime);

				// 時間枠を足した後の年月日のみを取得
				String yyyymmdd1 = SimpleDateFormatType.FORMAT_YYYYMMDD
						.format(nextstartDateTime);

				// 年月日が変わる。つまり時計が１周して年月日が変わったらループはおしまい
				if (Integer.valueOf(yyyymmdd).intValue() < Integer.valueOf(
						yyyymmdd1).intValue()) {
					break;
				}

				strKensaStartTime = SimpleDateFormatType.FORMAT_HHMM.format(nextstartDateTime);

				kensaStartTime = Integer.valueOf(strKensaStartTime).intValue();

				i++;
			}
			timeFrameList.add(new Integer(kensaEndTime * 100));
		} catch (ParseException e) {
			ReservationFormLoggerFactory.getInstance().getLogger().debug("ParseException " + e.getMessage());
			e.printStackTrace();
		}catch (NumberFormatException e) {
			ReservationFormLoggerFactory.getInstance().getLogger().debug("NumberFormatException " + e.getMessage());
			e.printStackTrace();
		}

		return timeFrameList;
	}

	// 2015.02.27 Mod S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更
	/**
	 * オーダ一覧から検査室の一覧を作成する。
	 *
	 * @param List オーダ一覧           
	 * @return 検査室IDの一覧(ShowOrder昇順でソート)
	 */
	public List<ExamRoom> createKensaSituList(List orderCalenderList) {

		// オーダから検査室の重複を取り除く
		List<OrderCalendarItem> tmpOrderList = new ArrayList<OrderCalendarItem>();		
		for (Iterator ite = orderCalenderList.iterator(); ite.hasNext();) {
			OrderCalendarItem orderItem = (OrderCalendarItem) ite.next();

			boolean isContain = false;
			for (Iterator<OrderCalendarItem> tmpIte = tmpOrderList.iterator(); tmpIte
					.hasNext();) {
				if (tmpIte.next().getKensaSituID()
						.equals(orderItem.getKensaSituID())) {
					isContain = true;
					break;
				}
			}

			if (isContain == false) {
				tmpOrderList.add(orderItem);
			}
		}
		// ソート
		Collections.sort(tmpOrderList, new KensaSituComparator());
		
		List<ExamRoom> roomList = new ArrayList<ExamRoom>();
		// 検査室の情報を抜き出す
		for (Iterator<OrderCalendarItem> ite = tmpOrderList.iterator(); ite
				.hasNext();) {
			OrderCalendarItem item = ite.next();
			// ヘッダは正式名称を使用する
			roomList.add(new ExamRoom(item.getKensaSituID(), 
					item.getKensaSituTitle(), ""));
		}
		for (int i = tmpOrderList.size(); i < COL_MIN_COUNT; i++) {
			//空列を表示する為に非表示の検査室追加
			roomList.add(new ExamRoom("-1", "", ""));
		}		
		return roomList;
	}
	
	/**
	 * 検査室ソート用クラス
	 */
	private class KensaSituComparator implements Comparator<OrderCalendarItem> {
		/**
		 * ソートロジック(検査室表示順 昇順)
	 */
		public int compare(OrderCalendarItem o1, OrderCalendarItem o2) {
			if (o1.getKensaSituShowOrder() > o2.getKensaSituShowOrder()) {
				return 1;

			} else if (o1.getKensaSituShowOrder() == o2.getKensaSituShowOrder()) {
				return 0;

			} else {
				return -1;
			}
		}
	}

	/**
	 * 検査室と時間ごとに検査をまとめたマップを返す。
	 *
	 * @param timeFrameList　時間枠のリスト           
	 * @param orderList　オーダのリスト
	 * @param roomList　検査室のリスト
	 * @return　時間枠毎、検査室毎にまとめられたマップ
	 */
	public HashMap<String, HashMap<ExamRoom, List<OrderCalendarItem>>> createOrderCalenderMap(
			List timeFrameList, List orderList, List<ExamRoom> roomList) {

		// 時間枠毎にオーダをまとめる
		HashMap<String, HashMap<ExamRoom, List<OrderCalendarItem>>> timeRoomOrderCalenderMap 
			= new HashMap<String, HashMap<ExamRoom, List<OrderCalendarItem>>>();
		for (int i = 0; i < timeFrameList.size() - 1; i++) {
			String timeFrame = timeFrameList.get(i).toString();
			// 範囲を決める為に次の時間枠を取得
			String nextTimeFrame = timeFrameList.get(i + 1).toString();

			// 検査室毎にオーダをまとめる
			HashMap<ExamRoom, List<OrderCalendarItem>> roomOrderMap = new HashMap<ExamRoom, List<OrderCalendarItem>>();
			for (Iterator<ExamRoom> roomIte = roomList.iterator(); roomIte
					.hasNext();) {
				// 検査室ID取得
				ExamRoom examRoom = roomIte.next();

				List<OrderCalendarItem> newOrderList = new ArrayList<OrderCalendarItem>();
				for (int k = 0; k < orderList.size(); k++) {
					OrderCalendarItem orderItem = (OrderCalendarItem) orderList
							.get(k);

					// 違う検査室
					if (examRoom.getExamRoomID().equals(orderItem.getKensaSituID()) == false) {
						continue;
					}
					int startTime = orderItem.getKensaStartTime();
					// FREEのオーダ
					if (timeFrame.equalsIgnoreCase("FREE")) {
						if (startTime == 999999) {
							newOrderList.add(orderItem);
						}
						continue;
					}
					// 時間範囲内のオーダ
					if (startTime >= Integer.parseInt(timeFrame)
							&& startTime < Integer.parseInt(nextTimeFrame)) {
						newOrderList.add(orderItem);
						continue;
					}
					}
				// key:検査室 value:keyと同じ検査室を持つオーダ
				roomOrderMap.put(examRoom, newOrderList);
				}
			// key:時間 value:HashMap<String, List<OrderCalenderItem>>
			// calender.jspの記述の仕方が時間行ごとに記述となっているので
			timeRoomOrderCalenderMap.put(timeFrame, roomOrderMap);
			}

		return timeRoomOrderCalenderMap;
	}

	/**
	 * 対象検査室の検査数を返す。
	 * @param timeRoomOrderMap カレンダーMAP情報
	 * @param roomBean 対象検査室
	 * @return 合計値
	 */
	public int getRoomSum(HashMap<String, HashMap<ExamRoom, List<OrderCalendarItem>>> timeRoomOrderMap, ExamRoom roomBean) {
		int sum = 0;
		
		// 時間枠ごとのループ
		for (Iterator<HashMap<ExamRoom, List<OrderCalendarItem>>> orderIte = timeRoomOrderMap.values().iterator(); orderIte.hasNext();){
			// 該当時間の時間情報を取得
			HashMap<ExamRoom, List<OrderCalendarItem>> roomOrderMap = orderIte.next();
			if (null == roomOrderMap){
				continue;
		}

			List<OrderCalendarItem> itemList = roomOrderMap.get(roomBean);
			if (null == itemList){
				continue;
	}

			for (Iterator<OrderCalendarItem> ite = itemList.iterator(); ite.hasNext();){
				OrderCalendarItem item = ite.next();
				int time = item.getKensaStartTime();
				// 非表示情報以外を数える
				if (-1 == time){
					continue;
}
				sum++;
			}
		}
		return sum;
	}
	
	// /**
	// * 検査開始日からの一週間分の日付リストを作成する。
	// *
	// * @param date
	// * @return
	// */
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
	 OrderCalendarItem orderCalenderItem = (OrderCalendarItem)
	 orderCalenderList
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


	/**
	 * 検査室と時間ごとに検査をまとめたマップを返す（検査室毎縦表示）
	 *
	 * @param timeFrameList　時間枠のリスト
	 * @param orderList　オーダのリスト
	 * @param roomList　検査室のリスト
	 * @return　時間枠毎、検査室毎にまとめられたマップ
	 */
	public HashMap<ExamRoom, HashMap<String, List<OrderCalendarItem>>> createOrderCalenderMap_h(
			List timeFrameList, List orderList, List<ExamRoom> roomList) {

		// 検査室毎にオーダをまとめる
		HashMap<ExamRoom, HashMap<String, List<OrderCalendarItem>>> timeRoomOrderCalenderMap
		= new HashMap<ExamRoom, HashMap<String, List<OrderCalendarItem>>>();

		for (Iterator<ExamRoom> roomIte = roomList.iterator(); roomIte.hasNext();) {
			// 検査室ID取得
			ExamRoom examRoom = roomIte.next();

			// 時間枠毎にオーダをまとめる
			HashMap<String, List<OrderCalendarItem>> roomOrderMap
			= new HashMap<String, List<OrderCalendarItem>>();

			for (int i = 0; i < timeFrameList.size() - 1; i++) {
				String timeFrame = timeFrameList.get(i).toString();
				// 範囲を決める為に次の時間枠を取得
				String nextTimeFrame = timeFrameList.get(i + 1).toString();

				List<OrderCalendarItem> newOrderList = new ArrayList<OrderCalendarItem>();
				for (int k = 0; k < orderList.size(); k++) {
					OrderCalendarItem orderItem = (OrderCalendarItem) orderList.get(k);

					// 違う検査室
					if (examRoom.getExamRoomID().equals(orderItem.getKensaSituID()) == false) {
						continue;
					}
					int startTime = orderItem.getKensaStartTime();
					// FREEのオーダ
					if (timeFrame.equalsIgnoreCase("FREE")) {
						if (startTime == 999999) {
							newOrderList.add(orderItem);
						}
						continue;
					}
					// 時間範囲内のオーダ
					if (startTime >= Integer.parseInt(timeFrame)
							&& startTime < Integer.parseInt(nextTimeFrame)) {
						newOrderList.add(orderItem);
						continue;
					}
				}
				// key:検査室 value:keyと同じ検査室を持つオーダ
				roomOrderMap.put(timeFrame, newOrderList);
			}
			// key:時間 value:HashMap<String, List<OrderCalenderItem>>
			// calender.jspの記述の仕方が時間行ごとに記述となっているので
			timeRoomOrderCalenderMap.put(examRoom, roomOrderMap);
		}

		return timeRoomOrderCalenderMap;
	}

	/**
	 * 対象検査室の検査数を返す（検査室毎縦表示）
	 * @param timeRoomOrderMap カレンダーMAP情報
	 * @param roomBean 対象検査室
	 * @return 合計値
	 */
	public int getRoomSum_h(HashMap<ExamRoom, HashMap<String, List<OrderCalendarItem>>> timeRoomOrderMap, ExamRoom roomBean) {
		int sum = 0;

		HashMap<String, List<OrderCalendarItem>> roomOrderMap = timeRoomOrderMap.get(roomBean);

		for(Iterator<List<OrderCalendarItem>> orderIte = roomOrderMap.values().iterator(); orderIte.hasNext();) {

			List<OrderCalendarItem> itemList = orderIte.next();

			//for (Iterator<OrderCalendarItem> ite = itemList.iterator(); ite.hasNext();){
			for (int j = 0; j < itemList.size(); j++){
				OrderCalendarItem item = itemList.get(j);
				int time = item.getKensaStartTime();
				// 非表示情報以外を数える
				if (-1 == time){
					continue;
				}
				sum++;
			}
		}
		return sum;
	}
}