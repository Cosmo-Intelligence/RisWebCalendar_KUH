package com.yokogawa.radiquest.ris.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.yokogawa.radiquest.ris.DBAccess.CalendarDataHome;
import com.yokogawa.radiquest.ris.DBAccess.CodeConvertDataHome;
import com.yokogawa.radiquest.ris.DBAccess.DayClassificationTableDataHome;
import com.yokogawa.radiquest.ris.DBAccess.OrderCalenderHome;
import com.yokogawa.radiquest.ris.DBAccess.OrderDetailDataHome;
import com.yokogawa.radiquest.ris.DBAccess.SystemParamDataHome;
import com.yokogawa.radiquest.ris.action.CalenderAction;
import com.yokogawa.radiquest.ris.action.OrderConditionAction;
import com.yokogawa.radiquest.ris.action.OrderFormAction;
import com.yokogawa.radiquest.ris.bean.DateBean;
import com.yokogawa.radiquest.ris.bean.DayClassification;
import com.yokogawa.radiquest.ris.bean.OrderDetail;
import com.yokogawa.radiquest.ris.core.Configuration;
import com.yokogawa.radiquest.ris.core.DBAccessException;
import com.yokogawa.radiquest.ris.core.DataSort;
import com.yokogawa.radiquest.ris.core.ReservationFormLoggerFactory;

/**
 * RIS AG検査予約カレンダーシステムサーブレット
 *
 * @author Shogo TANIAI
 */
public class ReservationServlet extends HttpServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final int ENDTIME = 240000;

	private Set<Integer> kyusinbiMap;

	private List<DayClassification> dayClassifications;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		perform(request, response);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		perform(request, response);
	}

	private void perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		if (getLogger().isDebugEnabled()) {
			getLogger().debug(
					"[" + request.getRemoteAddr() + "] perform() start");
		}

		OrderConditionAction ocAction = new OrderConditionAction(request,
				response);
		OrderFormAction ofAction = new OrderFormAction(request, response);

		String action = (String) request.getParameter(ActionType.Parameter);

		if (getLogger().isInfoEnabled()) {
			getLogger().info(
					"[" + request.getRemoteAddr() + "] action : ["
							+ (action != null ? action : "") + "]");
		}

		// 処理
		if (action == null || action.length() == 0) {
			// 検査予約メインページ
			showMainPage(request, response);
		} else if (action.equals(ActionType.CALENDAR)) {
			// 検査予約カレンダー表示
			showCalendar(request, response);
		} else if (action.equals(ActionType.ORDER_DETAIL)) {
			// オーダ詳細表示
			showOrderDetail(request, response);
		} else if (action.equals(ActionType.EXAM_ROOM)) {
			// 検査室一覧の取得
			logParameter(request, Parameters.KENSA_TYPE);
			ocAction.getExamRoomList();
			showExamRoom(request, response);
		}
		if (getLogger().isDebugEnabled()) {
			getLogger()
					.debug("[" + request.getRemoteAddr() + "] perform() end");
		}
	}

	private void showMainPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		try {
			// 検査種別
			OrderConditionAction ocAction = new OrderConditionAction(request,
					response);
			ocAction.getKensaTypeList();

			// RI区分
			ocAction.getRIOrderList();

			// 検査ステータス
			ocAction.getKensaStatusList();

			// 依頼科
			ocAction.getSectionList();

			// 病棟
			ocAction.getByoutouList();

			// 表示範囲の日時
			Calendar calToday = Calendar.getInstance();
			String today = SimpleDateFormatType.FORMAT_YYYYMMDDE
					.format(calToday.getTime());
			calToday.add(Calendar.DATE, 6);
			String next = SimpleDateFormatType.FORMAT_YYYYMMDDE.format(calToday
					.getTime());
			request.setAttribute(Parameters.START_DATE, today);
			request.setAttribute(Parameters.END_DATE, next);

			SystemParamDataHome systemParamDataHome = new SystemParamDataHome();
			// 患者IDの桁数を取得
			String kanjaIDDigit = systemParamDataHome.getKanjaIDDigit();
			request.setAttribute(Parameters.KANJA_ID_DIGIT, kanjaIDDigit);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("jsp/index.jsp");
			dispatcher.forward(request, response);
		} catch (IOException e) {
			throw new ServletException(e);
		} catch (DBAccessException e) {
			e.printStackTrace();
		}
	}

	private void showCalendar(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		logParameter(request, Parameters.DATE);
		logParameter(request, Parameters.TIMEFRAME);
		logParameter(request, Parameters.KENSA_TYPE);
		logParameter(request, Parameters.EXAM_ROOM);

		try {
			// 表示開始日付
			String strDate = request.getParameter(Parameters.DATE);
			String today = SimpleDateFormatType.FORMAT_YYYYMMDD
					.format(new Date());
			int date = Integer.valueOf(today).intValue();
			if (strDate != null && strDate.length() != 0) {
				date = Integer.valueOf(strDate).intValue();
			}

			// 時間枠
			String strTimeFrame = request.getParameter(Parameters.TIMEFRAME);

			// 検査種別ID
			String kensaTypeParam = request.getParameter(Parameters.KENSA_TYPE);
			String[] kensaTypes = kensaTypeParam.split(":");

			// 検査室ID
			String examRoomParam = request.getParameter(Parameters.EXAM_ROOM);
			String[] examRooms = {};
			if (examRoomParam != null && examRoomParam.length() != 0) {
				examRooms = examRoomParam.split(":");
			}

			// RI区分ID
			String riOrderParam = request.getParameter(Parameters.RI_ORDER);
			String[] riOrders = {};
			if (riOrderParam != null && riOrderParam.length() != 0) {
				riOrders = riOrderParam.split(":");
			}

			// 検査ステータスCODE
			String kensaStatusParam = request.getParameter(Parameters.KENSA_STATUS);
			String[] kensaStatus = {};
			if (kensaStatusParam != null && kensaStatusParam.length() != 0) {
				kensaStatus = kensaStatusParam.split(":");
			}

			// 依頼科
			String sectionParam = request.getParameter(Parameters.SECTION);
			String[] section = {};
			if (sectionParam != null && sectionParam.length() > 0) {
				section = sectionParam.split(":");
			}

			// 病棟
			String byoutouParam = request.getParameter(Parameters.BYOUTOU);
			String[] byoutou = {};
			if (byoutouParam != null && byoutouParam.length() > 0) {
				byoutou = byoutouParam.split(":");
			}

			// 患者入外
			String kanjaNyugaiParam = request.getParameter(Parameters.KANJA_NYUGAI);
			String[] kanjaNyugai = {};
			if (kanjaNyugaiParam != null && kanjaNyugaiParam.length() > 0) {
				kanjaNyugai = kanjaNyugaiParam.split(":");
			}

			// 伝票入外
			String denpyouNyugaiParam = request.getParameter(Parameters.DENPYOU_NYUGAI);
			String[] denpyouNyugai = {};
			if (denpyouNyugaiParam != null && denpyouNyugaiParam.length() > 0) {
				denpyouNyugai = denpyouNyugaiParam.split(":");
			}

			int kensaStartTime = Configuration.getInstance().getStartTime();
			int kensaEndTime = Configuration.getInstance().getEndTime();

			int timeFrame = 60;
			if (strTimeFrame != null && strTimeFrame.length() != 0) {
				timeFrame = Integer.valueOf(strTimeFrame).intValue();
			}
			// int timeFrame = Integer.valueOf(strTimeFrame);

			OrderCalenderHome orderCalenderHome = new OrderCalenderHome();
			// 検査種別、検査室などの条件にあった検査一覧を取得。
			List orderCalenderList = orderCalenderHome.getOrderCalendarItems(
					kensaStartTime, date, kensaTypes, examRooms, riOrders,
					kensaStatus, section, byoutou, kanjaNyugai, denpyouNyugai);
			CalenderAction calenderAction = new CalenderAction();
			// 表示開始日からの一週間分の日付リストを作成。
			List dateList = calenderAction.createDateList(date);

			// 時間枠リストを作成。
			List timeFrameList = calenderAction.createTimeFrameList(
					kensaStartTime, kensaEndTime, timeFrame);

			// 日付と時間で検査をまとめる。
			HashMap timeDateOrderCalenderMap = calenderAction
					.createTimeDateOrderCalenderMap(timeFrameList, dateList,
							orderCalenderList);

			// 休診日取得
			Set<Integer> kyusinbiMap = getKyushinbi();
			List<DayClassification> dayClassifications = getDayClassifications();

			// 表示する日にちと休診日の関連付け
			ArrayList dateBeanList = new ArrayList();
			for (int i = 0; i < dateList.size(); i++) {
				int tempDate = new Integer(dateList.get(i).toString())
						.intValue();

				DateBean dateBean = new DateBean();
				dateBean.setKyusinbi(tempDate);
				dateBean.setKyusinbiFlg(isKyusinbi(tempDate, kyusinbiMap, dayClassifications));

				dateBeanList.add(dateBean);
			}

			request.setAttribute(Parameters.KENSALIST, timeDateOrderCalenderMap);
			request.setAttribute(Parameters.TIMEFRAME, timeFrameList);
			request.setAttribute(Parameters.DATELIST, dateList);
			request.setAttribute(Parameters.KYUSINBI_DATE, dateBeanList);

			RequestDispatcher dispatcher = request
					.getRequestDispatcher("jsp/calendar.jsp");
			dispatcher.forward(request, response);
		} catch (IOException e) {
			throw new ServletException(e);
		} catch (DBAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private Set<Integer> getKyushinbi() throws DBAccessException {
		if (kyusinbiMap == null) {
			CalendarDataHome calendarDataHome = new CalendarDataHome();
			ArrayList kyusinbiList = calendarDataHome.getKyusinbiList();
			kyusinbiMap = new HashSet<Integer>();
			for (int i = 0; i < kyusinbiList.size(); i++) {
				String kyusinbi = kyusinbiList.get(i).toString();
				int kyusinbiInt = convertDate(kyusinbi);
				kyusinbiMap.add(kyusinbiInt);
			}
		}

		return kyusinbiMap;
	}

	private List<DayClassification> getDayClassifications()
			throws DBAccessException {
		if (dayClassifications == null) {
			DayClassificationTableDataHome dayClassificationHome = new DayClassificationTableDataHome();
			dayClassifications = dayClassificationHome.getDayClassifications();
		}

		return dayClassifications;
	}

	private boolean isKyusinbi(int date, Set<Integer> kyusinbiList, List<DayClassification> dayClassifications) {
		// DAYCLASSIFICATIONTABLE
		int classification = getClassification(date, dayClassifications);
		if (classification == 0) {
			getLogger().debug(date + " is nonconsultation day by DAYCLASSIFICATIONTABLE.");
			return true;
		}

		// CALENDARMASTER
		if (kyusinbiList.contains(date)) {
			getLogger().debug(date + " is nonconsultation day by CALENDARMASTER.");
			return true;
		}
//		for (int i = 0; i < kyusinbiList.size(); i++) {
//			String kyusinbi = kyusinbiList.get(i).toString();
//			// 休診日をyyyyMMddに変換
//			int kyusinbiInt = convertDate(kyusinbi);
//			if (kyusinbiInt == date) {
//				getLogger().debug(date + " is nonconsultation day by CALENDARMASTER.");
//				return true;
//			}
//		}

		return false;
	}

	private int getClassification(int date,
			List<DayClassification> dayClassifications) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.clear();
		cal.set(date / 10000, date % 10000 / 100 - 1, date % 100);

		// 曜日の取得
		int dayOfWeek = 0;
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			dayOfWeek = 0;
			break;
		case Calendar.MONDAY:
			dayOfWeek = 1;
			break;
		case Calendar.TUESDAY:
			dayOfWeek = 2;
			break;
		case Calendar.WEDNESDAY:
			dayOfWeek = 3;
			break;
		case Calendar.THURSDAY:
			dayOfWeek = 4;
			break;
		case Calendar.FRIDAY:
			dayOfWeek = 5;
			break;
		case Calendar.SATURDAY:
			dayOfWeek = 6;
			break;
		}

		// 第N週の取得
		int weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);

		// 休診日定義の取得
		int classification = 9;
		for (DayClassification dayClassification : dayClassifications) {
			if (dayClassification.getDayOfWeek() == dayOfWeek) {
				switch (weekOfMonth) {
				case 1:
					classification = dayClassification.getWeek01();
					break;
				case 2:
					classification = dayClassification.getWeek02();
					break;
				case 3:
					classification = dayClassification.getWeek03();
					break;
				case 4:
					classification = dayClassification.getWeek04();
					break;
				case 5:
					classification = dayClassification.getWeek05();
					break;
				case 6:
					classification = dayClassification.getWeek06();
					break;
				}
			}
		}

		return classification;
	}

	private int convertDate(String date) {
		String[] temp = date.split("/");
		String strConvertDate = "";
		for (int i = 0; i < temp.length; i++) {
			strConvertDate += temp[i];
		}
		return new Integer(strConvertDate).intValue();
	}

	private void showOrderDetail(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		logParameter(request, Parameters.RIS_ID);

		String risIDParam = request.getParameter(Parameters.RIS_ID);

		ArrayList orderDetails = new ArrayList();
		int lineCount = 9;
		try {
			String[] risIDs = risIDParam.split(":");

			CodeConvertDataHome convertDataHome = new CodeConvertDataHome();
			OrderDetailDataHome orderDetailDataHome = new OrderDetailDataHome();
			ArrayList deleteOrderDetailList = new ArrayList();
			orderDetails = orderDetailDataHome.getOrderDetails(risIDs);
			if (orderDetails == null | orderDetails.size() == 0) {
				for (int i = 0; i < lineCount; i++) {
					orderDetails.add(new OrderDetail());
				}
			} else {
				for (int i = 0; i < orderDetails.size(); i++) {
					OrderDetail orderDetail = (OrderDetail) orderDetails.get(i);
					if (i < orderDetails.size() - 1) {
						OrderDetail nextOrderDetail = (OrderDetail) orderDetails
								.get(i + 1);
						if (nextOrderDetail.getRisID().equalsIgnoreCase(
								orderDetail.getRisID())) {
							// 部位名連結
							String buiName = orderDetail.getBuiName();
							String nextbuiName = nextOrderDetail.getBuiName();
							String totalBuiName = buiName + "," + nextbuiName;
							nextOrderDetail.setBuiName(totalBuiName);

							// 部位略名連結
							String buiRyakuName = orderDetail.getBuiRyaku();
							String nextbuiRyakuName = nextOrderDetail
									.getBuiRyaku();
							String totalBuiRyakuName = buiRyakuName + ","
									+ nextbuiRyakuName;
							nextOrderDetail.setBuiRyaku(totalBuiRyakuName);

							// 方法名連結
							String houhouName = orderDetail.getHouhou();
							String nextHouhouName = nextOrderDetail.getHouhou();
							String totalHouhouName = houhouName + ","
									+ nextHouhouName;
							nextOrderDetail.setHouhou(totalHouhouName);

							// 方法略名連結
							String houhouRyakuName = orderDetail
									.getHouhouRyaku();
							String nextHouhouRyakuName = nextOrderDetail
									.getHouhouRyaku();
							String totalHouhouRyakuName = houhouRyakuName + ","
									+ nextHouhouRyakuName;
							nextOrderDetail
									.setHouhouRyaku(totalHouhouRyakuName);

							// 左右名連結
							String sayuuName = orderDetail.getSayuu();
							String nextSayuuName = nextOrderDetail.getSayuu();
							String totalSayuuName = sayuuName + ","
									+ nextSayuuName;
							nextOrderDetail.setSayuu(totalSayuuName);

							// 左右略名連結
							String sayuuRyakuName = orderDetail.getSayuuRyaku();
							String nextSayuuRyakuName = nextOrderDetail
									.getSayuuRyaku();
							String totalSayuuRyakuName = sayuuRyakuName + ","
									+ nextSayuuRyakuName;
							nextOrderDetail.setSayuuRyaku(totalSayuuRyakuName);

							// 方向名連結
							String houkouName = orderDetail.getHoukou();
							String nextHoukouName = nextOrderDetail.getHoukou();
							String totalHoukouName = houkouName + ","
									+ nextHoukouName;
							nextOrderDetail.setHoukou(totalHoukouName);

							// 方攻略名連結
							String houkouRyakuName = orderDetail
									.getHoukouRyaku();
							String nextHoukouRyakuName = nextOrderDetail
									.getHoukouRyaku();
							String totalHoukouRyakuName = houkouRyakuName + ","
									+ nextHoukouRyakuName;
							nextOrderDetail
									.setHouhouRyaku(totalHoukouRyakuName);

							deleteOrderDetailList.add(orderDetail);
						}
					}

					// 性別変換
					String value = orderDetail.getSex();
					String sex = convertDataHome.getSex(value);
					orderDetail.setSex(sex);

					// 状態変換
					String transport = orderDetail.getTransporttype();
					String transportType = convertDataHome
							.getTransPortType(transport);
					orderDetail.setTransporttype(transportType);

/*
					// 患者入外変換
					String kanjaNyugai = orderDetail.getKanjaNyugaiKbn();
					String kanjaNyugaiKbn = convertDataHome
							.getNyugai(kanjaNyugai);
					orderDetail.setKanjaNyugaiKbn(kanjaNyugaiKbn);

					// 伝票入外変換
					String denpyoNyugai = orderDetail.getDenPyoNyugaiKbn();
					String denpyoNyugaiKbn = convertDataHome
							.getNyugai(denpyoNyugai);
					orderDetail.setDenPyoNyugaiKbn(denpyoNyugaiKbn);

					// RI区分変換
					String riOrderFlg = orderDetail.getRiOrder();
					String riOrder = convertDataHome
							.getRIOrder(riOrderFlg);
					orderDetail.setRiOrder(riOrder);
*/
				}
				orderDetails.removeAll(deleteOrderDetailList);

				DataSort dataSort = new DataSort();
				orderDetails = (ArrayList) dataSort
						.GetOrderItemList2(orderDetails);
			}
			if (orderDetails.size() <= lineCount) {
				int count = lineCount - orderDetails.size();
				for (int i = 0; i < count; i++) {
					orderDetails.add(new OrderDetail());
				}
			}
			request.setAttribute(Parameters.ORDER_DETAILS, orderDetails);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("jsp/orderdetail.jsp");
			dispatcher.forward(request, response);
		} catch (IOException e) {
			throw new ServletException(e);
		} catch (DBAccessException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	private void showExamRoom(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		try {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("jsp/examroom.jsp");
			dispatcher.forward(request, response);
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	private Logger getLogger() {
		return ReservationFormLoggerFactory.getInstance().getLogger();
	}

	private void logParameter(HttpServletRequest request, String parameter) {
		if (getLogger().isInfoEnabled()) {
			getLogger().info(
					parameter + " : [" + request.getParameter(parameter) + "]");
		}
	}
}
