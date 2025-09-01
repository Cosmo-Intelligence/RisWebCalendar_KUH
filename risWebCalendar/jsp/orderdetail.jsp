<%@ page pageEncoding="UTF-8"%>
<%@ include file="nocache.inc"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page
	import="com.yokogawa.radiquest.ris.servlet.SimpleDateFormatType"%>
<%@ page import="com.yokogawa.radiquest.ris.core.Configuration"%>
<%@ page import="com.yokogawa.radiquest.ris.servlet.Parameters"%>
<%@ page import="com.yokogawa.radiquest.ris.bean.*"%>
<%@ page import="com.yokogawa.radiquest.ris.bean.OrderDetail"%>
<%@ page
	import="com.yokogawa.radiquest.ris.DBAccess.CodeConvertDataHome"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	SimpleDateFormat hhmm = new SimpleDateFormat("HHmm");
	SimpleDateFormat hhmm1 = new SimpleDateFormat("HH:mm");
	SimpleDateFormat hmm = new SimpleDateFormat("Hmm");
	SimpleDateFormat hmm1 = new SimpleDateFormat("H:mm");

	Configuration configuration = Configuration.getInstance();
	ArrayList orderDetailItems = (ArrayList) configuration
			.getOrderDetailItems();

	ArrayList orderDetails = (ArrayList) request
			.getAttribute(Parameters.ORDER_DETAILS);

	boolean orderDetailStatusColor = configuration.isOrderDetailStatusColor();

	CodeConvertDataHome convertDataHome = new CodeConvertDataHome();
%>

<div id="OrderDetailSection">
	<div id="OrderDetailHead">
		<table>
			<thead>
				<tr>
					<th style="display: none">
					</td>
					<%
				int tableWidth = 0;

				for (int i = 0; i < orderDetailItems.size(); i++) {
					Configuration.OrderDetailItem odItem = (Configuration.OrderDetailItem) orderDetailItems
							.get(i);
					tableWidth += odItem.getWidth();
					String thTitle = "";
					int no = odItem.getNo();
					switch (no) {
					case 1:
						thTitle = "検査日";
						break;
					case 2:
						thTitle = "検査進捗";
						break;
					case 3:
						thTitle = "受付番号";
						break;
					case 4:
						thTitle = "予約時刻";
						break;
					case 5:
						thTitle = "受付時刻";
						break;
					case 6:
						thTitle = "年齢";
						break;
					case 7:
						thTitle = "性別";
						break;
					case 8:
						thTitle = "状態";
						break;
					case 9:
						thTitle = "患者ID";
						break;
					case 10:
						thTitle = "カナ氏名";
						break;
					case 11:
						thTitle = "患者氏名(漢字)";
						break;
					case 12:
						thTitle = "患者氏名(ローマ字)";
						break;
					case 13:
						thTitle = "患者入外";
						break;
					case 14:
						thTitle = "依頼科";
						break;
					case 15:
						thTitle = "依頼科";
						break;
					case 16:
						thTitle = "病棟";
						break;
					case 17:
						thTitle = "病棟";
						break;
					case 18:
						thTitle = "伝票病棟";
						break;
					case 19:
						thTitle = "依頼医";
						break;
					case 20:
						thTitle = "検査種別";
						break;
					case 21:
						thTitle = "部位名称";
						break;
					case 22:
						thTitle = "部位名称";
						break;
					case 23:
						thTitle = "方法";
						break;
					case 24:
						thTitle = "方法";
						break;
					case 25:
						thTitle = "左右";
						break;
					case 26:
						thTitle = "左右";
						break;
					case 27:
						thTitle = "方向";
						break;
					case 28:
						thTitle = "方向";
						break;
					case 29:
						thTitle = "検査進捗";
						break;
					case 30:
						thTitle = "伝票入外";
						break;
					case 31:
						thTitle = "所属科";
						break;
					case 32:
						thTitle = "予定検査機器";
						break;
					case 33:
						thTitle = "実施検査機器";
						break;
					case 34:
						thTitle = "入力日";
						break;
					case 35:
						thTitle = "入力時間";
						break;
					case 36:
						thTitle = "実施者";
						break;
					case 37:
						thTitle = "受付者";
						break;
					case 38:
						thTitle = "RI区分";
						break;
					case 39:
						thTitle = "病室名";
						break;
					case 40:
						thTitle = "検査開始時刻";
						break;
					case 41:
						thTitle = "検査終了時刻";
						break;
					case 42:
						thTitle = "予定検査室";
						break;
					case 43:
						thTitle = "実施検査室";
						break;
					case 44:
						thTitle = "オーダ日時";
						break;
					case 45:
						thTitle = "呼出時刻";
						break;
					default:
						break;
					}
			%>

					<th style="width: <%= odItem.getWidth() %>px;">
						<div style="width: <%= odItem.getWidth() %>px;"><%= thTitle %></div>
					</th>
					<%
				}
			%>
				</tr>
			</thead>
		</table>
	</div>
	<input id="dataCnt" type="hidden" value="<%=orderDetails.size()%>">
	<input id="columnCnt" type="hidden"
		value="<%=orderDetailItems.size()%>"> <input id="tableWidth"
		type="hidden" value="<%=tableWidth%>">
	<div id="OrderDetailList" onscroll="hScrollOrderDetail()">
		<table>
			<tbody>
				<%
			for (int i = 0; i < orderDetails.size(); i++) {
				OrderDetail orderDetail = (OrderDetail) orderDetails.get(i);
				String status = "";
				if (orderDetailStatusColor)
					status = orderDetail.getStatus();
		%>
				<tr class="status<%= status %>">
					<td style="display: none"><span class="orderDetailRisID"
						style="display: none;"><%= orderDetail.getRisID() %></span></td>
					<%
				for (int j = 0; j < orderDetailItems.size(); j++) {
						Configuration.OrderDetailItem odItem = (Configuration.OrderDetailItem) orderDetailItems
								.get(j);
						String tdData = "";

						int no = odItem.getNo();
						switch (no) {
						case 1:
							if (orderDetail.getKensaDate() != null) {
								//検査日
								tdData = SimpleDateFormatType.FORMAT_YYYYMMDD_SLA
										.format(orderDetail.getKensaDate());
							}
							break;
						case 2:
							//検査進捗
							tdData = orderDetail.getKensaStatus();
							break;
						case 3:
							//受付番号
							tdData = orderDetail.getReceiptNumber();
							break;
						case 4:
							if (orderDetail.getKensaStartTime() != null) {
								//予約時刻
								tdData = SimpleDateFormatType.FORMAT_HHMM_COLON
										.format(orderDetail.getKensaStartTime());
							}
							break;
						case 5:
							if (orderDetail.getReceiptDate() != null) {
								//受付時刻
								tdData = SimpleDateFormatType.FORMAT_HHMM_COLON
										.format(orderDetail.getReceiptDate());
							}
							break;
						case 6:
							// 年齢
							tdData = orderDetail.getKensaDate_Age();
							break;
						case 7:
							//性別
							tdData = orderDetail.getSex();
							break;
						case 8:
							// 状態
							tdData = orderDetail.getTransporttype();
							break;
						case 9:
							//患者ID
							tdData = orderDetail.getKanjaID();
							break;
						case 10:
							//カナ氏名
							tdData = orderDetail.getKanaSimei();
							break;
						case 11:
							//患者氏名(漢字)
							tdData = orderDetail.getKanjiSimei();
							break;
						case 12:
							//患者氏名(ローマ字)
							tdData = orderDetail.getRomaSimei();
							break;
						case 13:
							//患者入外
							//tdData = convertDataHome.getNyugai(orderDetail.getKanjaNyugaiKbn());
							tdData = "";
							if (orderDetail.getKanjaNyugaiKbn().equals("1")) {
								tdData = "外来";
							} else if (orderDetail.getKanjaNyugaiKbn().equals("2")) {
								tdData = "入院";
							}
							break;
						case 14:
							//依頼科
							tdData = orderDetail.getIraiSection();
							break;
						case 15:
							//依頼科
							tdData = orderDetail.getIraiSectionRyaku();
							break;
						case 16:
							//病棟
							tdData = orderDetail.getByoutouName();
							break;
						case 17:
							//病棟
							tdData = orderDetail.getByoutouRyaku();
							break;
						case 18:
							//伝票病棟
							tdData = orderDetail.getDenpyoByoutou();
							break;
						case 19:
							//依頼医
							tdData = orderDetail.getIraiDoctorName();
							break;
						case 20:
							//検査種別
							tdData = orderDetail.getKensaTypeName();
							break;
						case 21:
							// 部位名称
							tdData = orderDetail.getBuiName();
							break;
						case 22:
							//部位名称
							tdData = orderDetail.getBuiRyaku();
							break;
						case 23:
							//方法
							tdData = orderDetail.getHouhou();
							break;
						case 24:
							//方法
							tdData = orderDetail.getHouhouRyaku();
							break;
						case 25:
							//左右
							tdData = orderDetail.getSayuu();
							break;
						case 26:
							//左右
							tdData = orderDetail.getSayuuRyaku();
							break;
						case 27:
							//方向
							tdData = orderDetail.getHoukou();
							break;
						case 28:
							//方向
							tdData = orderDetail.getHoukouRyaku();
							break;
						case 29:
							//検査進捗
							tdData = orderDetail.getKensaStatus();
							break;
						case 30:
							//伝票入外
							//tdData = convertDataHome.getNyugai(orderDetail.getDenPyoNyugaiKbn());
							tdData = "";
							if (orderDetail.getDenPyoNyugaiKbn().equals("1")) {
								tdData = "外来";
							} else if (orderDetail.getDenPyoNyugaiKbn().equals("2")) {
								tdData = "入院";
							} else if (orderDetail.getDenPyoNyugaiKbn().equals("3")) {
								tdData = "入院中外来";
							}
							break;
						case 31:
							//所属科
							tdData = orderDetail.getKanjaSection();
							break;
						case 32:
							//予定検査機器
							tdData = orderDetail.getYoteiKensaRoom();
							break;
						case 33:
							//実施検査機器
							tdData = orderDetail.getKensaRoom();
							break;
						case 34:
							if (orderDetail.getInputDate() != null) {
								//入力日
								tdData = SimpleDateFormatType.FORMAT_YYYYMMDD_SLA
										.format(orderDetail.getInputDate());
							}
							break;
						case 35:
							if (orderDetail.getInputTime() != null) {
								//入力時間
								tdData = SimpleDateFormatType.FORMAT_HHMM_COLON
										.format(orderDetail.getInputTime());
							}
							break;
						case 36:
							//実施者
							tdData = orderDetail.getKensaGisiName();
							break;
						case 37:
							//受付者
							tdData = orderDetail.getUketukeTantouName();
							break;
						case 38:
							//RI区分
							// 2014.05.07 Mod T.Koudate@COSMO Start #2681
							// コメントアウトされていたCodeConvertを元にした変換処理を復活
							tdData = convertDataHome.getRIOrder(orderDetail.getRiOrder());
							/*
							tdData = "";
							if (orderDetail.getRiOrder().equals("1")) {
								tdData = "注射";
							} else if (orderDetail.getRiOrder().equals("2")) {
								tdData = "検査";
							} else if (orderDetail.getRiOrder().equals("3")) {
								tdData = "追跡";
							}
							*/
							// 2014.05.07 Mod T.Koudate@COSMO End   #2681
							break;
						case 39:
							// 病室名
							tdData = orderDetail.getByousitu();
							break;
						case 40:
							if (orderDetail.getKensaStartDate() != null) {
								//検査開始時刻
								tdData = SimpleDateFormatType.FORMAT_HHMM_COLON
										.format(orderDetail.getKensaStartDate());
							}
							break;
						case 41:
							if (orderDetail.getKensaEndDate() != null) {
								//検査終了時刻
								tdData = SimpleDateFormatType.FORMAT_HHMM_COLON
										.format(orderDetail.getKensaEndDate());
							}
							break;
						case 42:
							//予定検査室
							tdData = orderDetail.getYoteiKensaRoom();
							break;
						case 43:
							//実施検査室
							tdData = orderDetail.getKensaRoom();
							break;
						case 44:
							if (orderDetail.getOrderDate() != null) {
								//オーダ日時
								tdData = SimpleDateFormatType.FORMAT_YYYYMMDD_SLA
										.format(orderDetail.getOrderDate());
							}
							break;
						case 45:
							if (orderDetail.getYobidasiDate() != null) {
								//呼出時刻
								tdData = SimpleDateFormatType.FORMAT_HHMM_COLON
										.format(orderDetail.getYobidasiDate());
							}
							break;
						default:
							break;
						}
						if (tdData.length() == 0) {
							tdData = "&nbsp";
						}
			%>
					<td class="orderDetailLine"
						style="width: <%= odItem.getWidth() %>px;">
						<div
							style="white-space: nowrap; overflow-x:hidden; width:<%= odItem.getWidth() %>px;">
							<%= tdData %></div>
					</td>
					<%
				}
			%>
				</tr>
				<%
			}
		%>
			</tbody>
		</table>
	</div>
</div>
