package com.yokogawa.radiquest.ris.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yokogawa.radiquest.ris.DBAccess.OrderCalenderHome;
import com.yokogawa.radiquest.ris.DBAccess.SystemParam2DataHome;

/**
 * @author Shogo TANIAI
 * @author KUROYAMA Hiroyuki
 */
public class ConfiguratorServlet extends HttpServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger = ReservationFormLoggerFactory.getInstance()
			.getLogger();

	public void init(ServletConfig config) throws ServletException {
		String strSettingFile = config.getInitParameter("SettingFileName");
		ServletContext context = config.getServletContext();
		String rootPath = context.getRealPath("/WEB-INF");
		if (strSettingFile == null)
			throw new ServletException("web.xmlに設定ファイル名が記載されていません。");

		File settingFile = new File(rootPath, strSettingFile);
		if (!settingFile.exists())
			throw new ServletException(settingFile.getAbsolutePath()
					+ "が見つかりません。");

		Document settingDoc = null;
		XmlParser parser = new XmlParser();
		try {
			settingDoc = parser.readXmlFile(settingFile, "Shift_JIS");

			// ログ設定
			initLogger(rootPath, settingDoc);

			logger.info("initialize start.");

			// DB環境設定
			DBSessionManager manager = DBSessionManager.getInstance();
			manager.init(settingDoc);

			// 熊大HTML環境設定
			initKumadaiHtml(settingDoc);

			initCalendarSetting(settingDoc);

			// カレンダー表示時間
			initCalndarTime(settingDoc);

			// カレンダー表示項目
			//initCalendarDispMode(settingDoc);

			// オーダ詳細情報表示項目設定
			initOrderDetailItems(settingDoc);

			// 2012.03.06 Add Yk.Suzuki@CIJ Start SSGH-4-003
			// カレンダー区切り文字設定
			initCalSeparator(settingDoc);
			// 2012.03.06 Add Yk.Suzuki@CIJ End   SSGH-4-003

			// 2013.10.29 Add T.Koudate@Cosmo Start #1694
			// SystemParam2情報の設定
			initSystemParam2();
			// 2013.10.29 Add T.Koudate@Cosmo End   #1694

			// 2013.11.19 Add Yk.Suzuki@CIJ Start NCC_C-2-TKC-R005
			// オーダ件数 単位文字 設定
			initOrderCountUnit(settingDoc);
			// 2013.11.19 Add Yk.Suzuki@CIJ End   NCC_C-2-TKC-R005

			logger.info("initialize end.");
		} catch (CannotInitializeException e) {
			throw new ServletException(e);
		} catch (ParserConfigurationException e) {
			throw new ServletException(e);
		} catch (SAXException e) {
			throw new ServletException(e);
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	private void initLogger(String rootPath, Document settingDoc)
			throws ServletException, CannotInitializeException {
		XmlParser parser = new XmlParser();

		// logノードからlog4jのプロパティファイルのパスを取得する。
		Node logNode = parser.getTargetElement(settingDoc, "log");
		if (logNode == null)
			throw new ServletException();

		String path = parser.getTextNodeValue(logNode);
		if (path == null || path.length() == 0)
			throw new ServletException();

		// パスにファイル名だけが記述されていた場合、/WEB-INF以下にあるものとして
		// フルパスを作成する
		if (path.compareTo(ReservationFormLoggerFactory.PROPERTIES_FILE_NAME) == 0) {
			File file = new File(rootPath, path);
			path = file.getAbsolutePath();
		}
		ReservationFormLoggerFactory.getInstance().init(path);
	}

	private void initCalendarSetting(Document doc) {
		XmlParser parser = new XmlParser();
		Configuration configuration = Configuration.getInstance();

		// 休診日を含める・含めない
		Node displayNonconsultationDayNode = parser.getTargetElement(doc,
				"displayNonconsultationDay");
		String displayNonconsultationDayStr = parser
				.getTextNodeValue(displayNonconsultationDayNode);

		boolean displayNonconsultationDay = false;
		if (displayNonconsultationDayStr.equalsIgnoreCase("true"))
			displayNonconsultationDay = true;

		configuration.setDisplayNonconsultationDay(displayNonconsultationDay);

		// カレンダー枠の表示切替ON/OFF
		Node calendarDisplayChangeNode = parser.getTargetElement(doc,
				"calendarDisplayChange");
		boolean calendarDisplayChange = false;
		if (calendarDisplayChangeNode != null) {
			String calendarDisplayChangeStr = parser
					.getTextNodeValue(calendarDisplayChangeNode);

			if (calendarDisplayChangeStr.equalsIgnoreCase("true"))
				calendarDisplayChange = true;
		}

		configuration.setCalendarDisplayChange(calendarDisplayChange);
	}

	private void initKumadaiHtml(Document doc) {
		XmlParser parser = new XmlParser();
		Configuration configuration = Configuration.getInstance();
		Node htmlPathNode = parser.getTargetElement(doc, "htmlPath");
		String htmlPath = parser.getTextNodeValue(htmlPathNode);
		configuration.setHtmlPath(htmlPath);
	}

	private void initCalndarTime(Document doc) {
		XmlParser parser = new XmlParser();
		Configuration configuration = Configuration.getInstance();

		Node calendarTimeNode = parser.getTargetElement(doc, "CalendarTime");

		// 開始時刻
		int startTime = 0;
		Node startNode = parser.getTargetElement(calendarTimeNode, "start");
		String startStr = parser.getTextNodeValue(startNode);
		if (startStr != null) {
			try {
				startTime = Integer.parseInt(startStr);
			} catch (NumberFormatException e) {
				// NOP
				logger.warn("Invalid Start time [" + startStr + "]");
			}
		}
		if (startTime < 0 || startTime > 2400) {
			logger.warn("Invalid Start time [" + startTime + "]");
			startTime = 0;
		}
		configuration.setStartTime(startTime);

		// 終了時刻
		int endTime = 2400;
		Node endNode = parser.getTargetElement(calendarTimeNode, "end");
		String endStr = parser.getTextNodeValue(endNode);
		if (endStr != null) {
			try {
				endTime = Integer.parseInt(endStr);
			} catch (NumberFormatException e) {
				// NOP
				logger.warn("Invalid End time [" + endStr + "]");
			}
		}
		if (endTime < 0 || endTime > 2400) {
			logger.warn("Invalid End time [" + endTime + "]");
			endTime = 2400;
		}
		configuration.setEndTime(endTime);

		logger.info("表示範囲時間 : " + startTime + " ～ " + endTime);
	}

	private void initCalendarDispMode(Document doc) {
		// logノードからlog4jのプロパティファイルのパスを取得する。
		// Node logNode = parser.getTargetElement(settingDoc, "log");
		// if (logNode == null)
		// throw new ServletException();
		//
		// String path = parser.getTextNodeValue(logNode);
		// if (path == null || path.length() == 0)
		// throw new ServletException();
		XmlParser parser = new XmlParser();
		Configuration configuration = Configuration.getInstance();

		Node calendarItemsNode = parser.getTargetElement(doc, "calendarItems");

		int dispPattern = 0;
		String strPattern = parser.getAttributeNodeValue(calendarItemsNode,
				"pattern");
		if (strPattern != null) {
			try {
				dispPattern = Integer.parseInt(strPattern);
			} catch (NumberFormatException e) {
				logger.warn("Invalid pattern [" + strPattern + "]");
			}
		}
		if (dispPattern <= 0 || 2 < dispPattern) {
			logger.warn("Invalid pattern [" + strPattern + "]");
			dispPattern = 1;
		}
		configuration.setDispPattern(dispPattern);

		if (dispPattern == 1) {
			logger.info("カレンダー表示項目：時刻、検査項目略名称、依頼科または病棟");
		} else if (dispPattern == 2) {
			logger.info("カレンダー表示項目：時刻、患者氏名カナ、検査項目略名称");
		}
	}

	private void initOrderDetailItems(Document doc) {
		XmlParser parser = new XmlParser();
		Configuration configuration = Configuration.getInstance();

		Node orderDetailItemsNode = parser.getTargetElement(doc,
				"orderDetailItems");
		NodeList orderDetailItemNodes = parser.getElementsByTagName(
				orderDetailItemsNode, "item");

		List odItemList = new ArrayList();
		for (int i = 0; i < orderDetailItemNodes.getLength(); i++) {
			Configuration.OrderDetailItem odItem = configuration.new OrderDetailItem();

			Node orderDetailItemNode = orderDetailItemNodes.item(i);
			String sNo = parser
					.getAttributeNodeValue(orderDetailItemNode, "no");
			String sShowOrder = parser.getAttributeNodeValue(
					orderDetailItemNode, "showOrder");
			String sWidth = parser.getAttributeNodeValue(orderDetailItemNode,
					"width");
			if (sWidth != null) {
				try {
					int nWidth = Integer.parseInt(sWidth);
					// widthが0以下の場合はリストに入れない
					if (nWidth <= 0)
						continue;
					odItem.setWidth(nWidth);
				} catch (NumberFormatException e) {
					logger.warn("Invalid Start time [" + sWidth + "]");
					continue;
				}
			}
			if (sNo != null) {
				try {
					int nNo = Integer.parseInt(sNo);
					odItem.setNo(nNo);
				} catch (NumberFormatException e) {
					logger.warn("Invalid Start time [" + sNo + "]");
					continue;
				}
			}
			if (sShowOrder != null) {
				try {
					int nShowOrder = Integer.parseInt(sShowOrder);
					odItem.setShowOrder(nShowOrder);
				} catch (NumberFormatException e) {
					logger.warn("Invalid Start time [" + sShowOrder + "]");
					continue;
				}
			}
			odItemList.add(odItem);
		}
		DataSort dc = new DataSort();
		odItemList = dc.GetOrderItemList(odItemList);
		configuration.setOrderDetailItems(odItemList);

		// オーダ一覧の検査ステータスによる文字色変更の有無
		Node orderDetailStatusColorNode = parser.getTargetElement(doc,
				"orderDetailStatusColor");
		boolean orderDetailStatusColor = false;
		if (orderDetailStatusColorNode != null) {
			String orderDetailStatusStr = parser
					.getTextNodeValue(orderDetailStatusColorNode);

			if (orderDetailStatusStr.equalsIgnoreCase("true"))
				orderDetailStatusColor = true;
		}
		configuration.setOrderDetailStatusColor(orderDetailStatusColor);
	}

	// 2012.03.06 Add Yk.Suzuki@CIJ Start SSGH-4-003
	/**
	 * カレンダー区切り文字初期化
	 * @param doc XMLノード
	 */
	private void initCalSeparator(Document doc) {
		XmlParser parser = new XmlParser();
		Configuration configuration = Configuration.getInstance();
		Node sepPathNode = parser.getTargetElement(doc, "calseparator");
		String calseparator = parser.getTextNodeValue(sepPathNode);
		configuration.setCalseparator(calseparator);
	}
	// 2012.03.06 Add Yk.Suzuki@CIJ End   SSGH-4-003

	// 2013.10.29 Add T.Koudate@Cosmo Start #1694
	/**
	 * SystemParam2情報取得
	 */
	private void initSystemParam2(){
		SystemParam2DataHome systemParam2DataHome = new SystemParam2DataHome();
		try{
			// 休診日モード取得
			String holidayMode = systemParam2DataHome.getHolidayMode();
			Configuration configuration = Configuration.getInstance();
			configuration.setHolidayMode(holidayMode);
		}catch(DBAccessException e) {
			logger.warn("休診日モード取得失敗",e);
		}
		// 2014.05.07 Add T.Koudate@COSMO Start #2681
		try{
			// RI区分拡張フラグ取得
			String extendRiOrderFlg = systemParam2DataHome.getExtendRiOrderFlg();
			Configuration configuration = Configuration.getInstance();
			configuration.setExtendRiOrderFlg(extendRiOrderFlg);
			// 2016.08.08 Add T.Koudate@COSMO Start #2629 検索条件のRI区分をV2.03標準対応する
			// RI区分拡張フラグを元に、対象となるRI区分の一覧を設定する
			if (SystemParam2DataHome.EXTEND_RI_ORDER_FLG_NEEDLE_INSPECT.equals(extendRiOrderFlg)){
				configuration.setRiOrderFlgList(new String[]{
						OrderCalenderHome.RI_ORDER_FLG_OTHER,
						OrderCalenderHome.RI_ORDER_FLG_NEEDLE,
						OrderCalenderHome.RI_ORDER_FLG_INSPECT,
						OrderCalenderHome.RI_ORDER_FLG_NEEDLE_INSPECT
				});
			} else if (SystemParam2DataHome.EXTEND_RI_ORDER_FLG_FOLLOW.equals(extendRiOrderFlg)){
				configuration.setRiOrderFlgList(new String[]{
						OrderCalenderHome.RI_ORDER_FLG_OTHER,
						OrderCalenderHome.RI_ORDER_FLG_NEEDLE,
						OrderCalenderHome.RI_ORDER_FLG_INSPECT,
						OrderCalenderHome.RI_ORDER_FLG_FOLLOW
				});
			} else if (SystemParam2DataHome.EXTEND_RI_ORDER_FLG_NI_FO.equals(extendRiOrderFlg)){
				configuration.setRiOrderFlgList(new String[]{
						OrderCalenderHome.RI_ORDER_FLG_OTHER,
						OrderCalenderHome.RI_ORDER_FLG_NEEDLE,
						OrderCalenderHome.RI_ORDER_FLG_INSPECT,
						OrderCalenderHome.RI_ORDER_FLG_FOLLOW,
						OrderCalenderHome.RI_ORDER_FLG_NEEDLE_INSPECT
				});
			} else {
				configuration.setRiOrderFlgList(new String[]{
						OrderCalenderHome.RI_ORDER_FLG_OTHER,
						OrderCalenderHome.RI_ORDER_FLG_NEEDLE,
						OrderCalenderHome.RI_ORDER_FLG_INSPECT
				});
			}
			// 2016.08.08 Add T.Koudate@COSMO End #2629 検索条件のRI区分をV2.03標準対応する
		}catch(DBAccessException e) {
			logger.warn("RI区分拡張フラグ取得失敗",e);
		}
		// 2014.05.07 Add T.Koudate@COSMO End   #2681
	}
	// 2013.10.29 Add T.Koudate@Cosmo End   #1694

	// 2013.11.19 Add Yk.Suzuki@CIJ Start NCC_C-2-TKC-R005
	/**
	 * オーダ件数 単位文字 初期化
	 * @param doc XMLノード
	 */
	private void initOrderCountUnit(Document doc) {
		XmlParser parser = new XmlParser();
		Configuration configuration = Configuration.getInstance();
		Node unitPathNode = parser.getTargetElement(doc, "orderCountUnit");
		String orderCountUnit = parser.getTextNodeValue(unitPathNode);
		configuration.setOrderCountUnit(orderCountUnit);
	}
	// 2013.11.19 Add Yk.Suzuki@CIJ End   NCC_C-2-TKC-R005

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		try {
			DBSessionManager.getInstance().closeConnection();
		} catch (Throwable e) {
		}
	}

}
