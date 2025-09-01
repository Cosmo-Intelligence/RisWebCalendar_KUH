<%@ page pageEncoding="UTF-8"%>
<%@ include file="nocache.inc"%>
<%@ page import="java.util.*"%>
<%@ page import="com.yokogawa.radiquest.ris.bean.*"%>
<%@ page import="com.yokogawa.radiquest.ris.core.Configuration"%>
<%@ page import="com.yokogawa.radiquest.ris.servlet.Parameters"%>
<%@ page
	import="com.yokogawa.radiquest.ris.servlet.CSSColorValueConverter"%>
<%@ page import="java.text.SimpleDateFormat"%>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%
String startDate = (String)request.getAttribute(Parameters.START_DATE);
// 2015.02.27 Del S.Matsumoto@CST Start KANA-C1-001 オーダ表示方法の変更 
//String endDate = (String)request.getAttribute(Parameters.END_DATE);
// 2015.02.27 Del S.Matsumoto@CST End KANA-C1-001 オーダ表示方法の変更
List kensaTypeList = (List)request.getAttribute(Parameters.KENSA_TYPES);
List riOrderList = (List)request.getAttribute(Parameters.RI_ORDER);
Configuration configuration = Configuration.getInstance();
String htmlPath = configuration.getHtmlPath();
List kensaStatuses = (List) request.getAttribute(Parameters.KENSA_STATUS);
List sectionList = (List) request.getAttribute(Parameters.SECTIONS);
List byoutouList = (List) request.getAttribute(Parameters.BYOUTOUS);

String displayTypeBlockStyle = "";
if (!configuration.isCalendarDisplayChange()) {
	displayTypeBlockStyle = "display: none;";
}
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ja" lang="ja">
<head>
<%@ page contentType="text/html; charset=UTF-8"%>
<!-- IE8標準準拠モード -->
<!-- 2016.12.20 Mod T.onoda@Cosmo Start KUMA205-3-CAL003 検査室毎画面　縦表示 */ -->
<meta http-equiv="X-UA-Compatible" content="IE=7" ; IE=8 />
<!-- <meta http-equiv="X-UA-Compatible" content="IE=7; IE=8" /> -->
<!-- 2016.12.20 Mod T.onoda@Cosmo Start KUMA205-3-CAL003 検査室毎画面　縦表示 */ -->
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="content-script-type" content="text/javascript" />
<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv="Cache-Control" content="no-cache">
		<meta http-equiv="Expires" content="Sat, 01 Jan 2000 00:00:00 GMT">
			<meta http-equiv="content-style-type" content="text/css">
				<meta http-equiv="content-script-type" content="text/javascript">
					<link rel="stylesheet" type="text/css"
						href="css/smoothness/jquery-ui-1.8.7.custom.css" />
					<link rel="stylesheet" type="text/css" href="css/index.css" />
					<link rel="stylesheet" type="text/css" href="css/calendar.css" />
					<link rel="stylesheet" type="text/css" href="css/orderdetail.css" />
					<!--2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更 -->
					<link rel="stylesheet" type="text/css" href="css/select2.css" />
					<!--2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更 -->


					<!-- <script type="text/javascript" src="js/jquery-1.4.4.min.js"></script>
	<script type="text/javascript" src="js/jquery-2.1.0.js"></script> -->
					<script type="text/javascript" src="js/jquery-1.7.min.js"></script>

					<script type="text/javascript" src="js/jquery.cookie.js"></script>
					<script type="text/javascript"
						src="js/jquery-ui-1.8.7.custom.min.js"></script>
					<script type="text/javascript" src="js/jquery-ui-i18n.js"></script>
					<script type="text/javascript" src="js/jquery.ui.datepicker-ja.js"></script>
					<script type="text/javascript" src="js/jquery.dimensions.min.js"></script>
					<script type="text/javascript" src="js/jquery.dump.js"></script>
					<script type="text/javascript" src="js/jquery.ui.position.js"></script>
					<script type="text/javascript" src="js/log4js.js"></script>
					<!--2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更 -->
					<script type="text/javascript" src="js/jquery.contextmenu.r2.js"></script>

					<!--2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更 -->
					<script type="text/javascript" src="js/index.js" charset="UTF-8"></script>

					<script type="text/javascript" src="js/select2.js"></script>

					<style type="text/css">
					
<%
for(int i = 0 ; i < kensaStatuses.size(); i++) {
	StatusDefine statusItem = (StatusDefine) kensaStatuses.get(i); 
%> 
#timeblock
	div.status<%= statusItem.getStatusCode() 
%> 
	{ color: 
<%=
	CSSColorValueConverter.convertFromDecimal (statusItem.getColor ()) 
%>;
}
#OrderDetailList

tr.status
<%=
statusItem.getStatusCode()
%>
{
color:
<%=
CSSColorValueConverter.convertFromDecimal(statusItem.getColor())
%>;
}
<%
}
%>
</style>
<link rel="stylesheet" type="text/css" href="css/status.css" />
<script type="text/javascript">
<!--
	htmlPath = "<%= htmlPath %>";

	function init() {
		kensaTypeArray = new Array();
		riOrderArray = new Array();
		kensaStatusArray = new Array();
		sectionArray = new Array();
		byoutouArray = new Array();
		kanjaNyugaiArray = new Array();
		denpyouNyugaiArray = new Array();
		kensaRoomArrayGlobal = new Array(); // 2014.05.07 Add T.Koudate@COSMO Start #2682 Cookie不正


<%
		// 検査種別
		for(int i = 0 ; i < kensaTypeList.size(); i++) {
			KensaType kensaItem = (KensaType)kensaTypeList.get(i);
			if(kensaItem != null){
%>
		kensaTypeArray.push({"name" : "<%= kensaItem.getKensaTypeName() %>", "id" : "<%= kensaItem.getKensaTypeID() %>"});
<%
			}
		}

		// 2014.05.07 Mod T.Koudate@COSMO Start #2681
		// コメントアウトされていた、riOrderListを用いたMap作成を復活し、固定値登録をコメントアウト
		// RI区分

		for(int i = 0 ; i < riOrderList.size(); i++) {
			CodeConvert riOrderItem = (CodeConvert)riOrderList.get(i);
			if(riOrderItem != null){
%>
		var riOrder = new RiOrder("<%= riOrderItem.getRiOrder() %>", "<%= riOrderItem.getItemValue() %>");
		riOrderArray.push(riOrder);
<%
			}
		}

%>
		//riOrderArray.push({"name" : "注射", "id" : "1"});
		//riOrderArray.push({"name" : "検査", "id" : "2"});
		//riOrderArray.push({"name" : "追跡", "id" : "3"});
<%
		// 2014.05.07 Mod T.Koudate@COSMO End   #2681

		// 検査ステータス
		for(int i = 0; i < kensaStatuses.size(); i++){
			StatusDefine statusItem = (StatusDefine) kensaStatuses.get(i);
			if(statusItem != null){
%>
		kensaStatusArray.push({"name" : "<%= statusItem.getShortLabel() %>", "id" : "<%= statusItem.getStatusCode() %>"});
<%
			}
		}

		// 依頼科
		for (int i = 0; i < sectionList.size(); i++) {
			Section section = (Section) sectionList.get(i);
			if (section != null) {
%>
		sectionArray.push({"name" : "<%= section.getSectionName() %>", "id" : "<%= section.getSectionID() %>"});
<%
			}
		}

		// 病棟
		for (int i = 0; i < byoutouList.size(); i++) {
			Byoutou byoutou = (Byoutou) byoutouList.get(i);
			if (byoutou != null) {
%>
		byoutouArray.push({"name" : "<%= byoutou.getByoutouName() %>", "id" : "<%= byoutou.getByoutouID() %>"});
<%
			}
		}
%>
		<% // 患者入外 %>
		kanjaNyugaiArray.push({"name" : "外来", "id" : "1"});
		kanjaNyugaiArray.push({"name" : "入院", "id" : "2"});

		<% // 伝票入外 %>
		denpyouNyugaiArray.push({"name" : "外来", "id" : "1"});
		denpyouNyugaiArray.push({"name" : "入院", "id" : "2"});
		<% // denpyouNyugaiArray.push({"name" : "入院中外来", "id" : "3"}); // ※入院中外来は不使用とする %>

		setValuesFromCookie();

		insertKensaType();
		insertRiOrder();
		insertKensaStatus();
		insertSection();
		insertByoutou();
		insertKanjaNyugai();
		insertDenpyouNyugai();

		searchOrder();
	}
// -->
</script>

					<title>検査予約</title>
</head>
<body>
	<div id="header">
		<div id="SearchBlock">
			<div id="SelectBlock">
				<div id="kensaTypeBlock">
					検査種別 <input type="text" readonly id="kensaTypeText" name="" /> <input
						type="hidden" readonly id="kensaTypeID" value="" />
					<button class="openDialog" id="openKensaTypeDialog">選</button>
					<div id="kensaTypeDialog" class="selectDialog" title="検査種別">
						<form>
							<div id="kensaTypeCheckBoxes" class="checkBoxes"></div>
						</form>
					</div>
				</div>
				<div id="kensaRoomBlock">
					検査室 <input type="text" readonly id="kensaRoomText" /> <input
						type="hidden" readonly id="kensaRoomID" value="" />
					<button class="openDialog" id="openKensaRoomDialog" disabled>選</button>
					<div id="kensaRoomDialog" class="selectDialog" title="検査室">
						<form>
							<div id="kensaRoomCheckBoxes" class="checkBoxes"></div>
						</form>
					</div>
				</div>
				<div id="timeFrameBlock">
					枠 <select id="timeFrame" name="timeFrame" onchange="searchOrder()">
						<option value="15">15</option>
						<option value="20">20</option>
						<option value="30">30</option>
						<option value="45">45</option>
						<option value="60">60</option>
						<option value="75">75</option>
						<option value="90">90</option>
						<option value="105">105</option>
						<option value="120">120</option>
					</select> (分)
				</div>
				<div id="riOrderBlock">
					RI区分 <input type="text" readonly id="riOrderText" /> <input
						type="hidden" readonly id="riOrderID" value="" />
					<button class="openDialog" id="openRiOrderDialog">選</button>
					<div id="riOrderDialog" class="selectDialog" title="RI区分">
						<form>
							<div id="riOrderCheckBoxes" class="checkBoxes"></div>
						</form>
					</div>
				</div>
				<div id="kensaStatusBlock">
					検査ステータス <input type="text" readonly id="kensaStatusText" /> <input
						type="hidden" readonly id="kensaStatusID" value="" />
					<button class="openDialog" id="openKensaStatusDialog">選</button>
					<div id="kensaStatusDialog" class="selectDialog" title="検査ステータス">
						<form>
							<div id="kensaStatusCheckBoxes" class="checkBoxes"></div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<div id="SearchBlockCenter">
			<div id="sectionBlock">
				依頼科 <input type="text" readonly id="sectionText" name="" /> <input
					type="hidden" readonly id="sectionID" value="" />
				<button class="openDialog" id="openSectionDialog">選</button>
				<div id="sectionDialog" class="selectDialog" title="依頼科">
					<form>
						<div id="sectionCheckBoxes" class="checkBoxes"></div>
					</form>
				</div>
			</div>
			<div id="byoutouBlock">
				病棟 <input type="text" readonly id="byoutouText" /> <input
					type="hidden" readonly id="byoutouID" value="" />
				<button class="openDialog" id="openByoutouDialog">選</button>
				<div id="byoutouDialog" class="selectDialog" title="病棟">
					<form>
						<div id="byoutouCheckBoxes" class="checkBoxes"></div>
					</form>
				</div>
			</div>
			<div id="kanjaNyugaiBlock">
				患者入外 <input type="text" readonly id="kanjaNyugaiText" /> <input
					type="hidden" readonly id="kanjaNyugaiID" value="" />
				<button class="openDialog" id="openKanjaNyugaiDialog">選</button>
				<div id="kanjaNyugaiDialog" class="selectDialog" title="患者入外">
					<form>
						<div id="kanjaNyugaiCheckBoxes" class="checkBoxes"></div>
					</form>
				</div>
			</div>
			<div id="denpyouNyugaiBlock">
				伝票入外 <input type="text" readonly id="denpyouNyugaiText" /> <input
					type="hidden" readonly id="denpyouNyugaiID" value="" />
				<button class="openDialog" id="openDenpyouNyugaiDialog">選</button>
				<div id="denpyouNyugaiDialog" class="selectDialog" title="伝票入外">
					<form>
						<div id="denpyouNyugaiCheckBoxes" class="checkBoxes"></div>
					</form>
				</div>
			</div>
			<div id="displayTypeBlock" style="<%= displayTypeBlockStyle %>">
				表示切替 <select id="displayType" name="displayType"
					onchange="displayTypeChange();searchOrder()">
					<option value="kanja">患者氏名</option>
					<option value="kensa">検査内容</option>
				</select>
			</div>
			<!--  2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更 -->
			<div id="laboratoryBlock">
				<div id="timelaboratoryDialog" class="selectDialog"
					title="予約時刻・検査室変更">
					<div id="ReservationTime">
						<span class="dlg-itemname dlg-col1">予約時刻</span> <select
							id="cmbReservationTimeDlg"></select>
					</div>
					<div id="PlansLaboratory">
						<span class="dlg-itemname dlg-col2">予定検査室</span> <select
							id="cmbPlansLaboratoryDlg"></select>
					</div>
				</div>
			</div>
			<!--  2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更 -->
		</div>

		<%
	String calendarBlockClass = "";
	if (!configuration.isDisplayNonconsultationDay())
		calendarBlockClass = "nonconsultationDayHidden";
%>
		<%-- 2015.02.27 Mod S.Matsumoto@CST Start KANA-C1-002 日にち切替ボタン変更 --%>
		<div id="SearchBlock_Bottom">
			<input id="prevWeek" name="prevWeek" type="button" value="　先週　"
				onclick="setOrderDay(this.name);" /> <input id="prevDay"
				name="prevDay" type="button" value="　昨日　"
				onclick="setOrderDay(this.name);" /> &nbsp;
			<div id="searchDate" class="DateDisplay" onclick="showCalendar()"><%= startDate %></div>
			&nbsp;&nbsp; <input id="today" name="today" type="button"
				value="　本日　" onclick="setToday();" /> <input id="nextDay"
				name="nextDay" type="button" value="　明日　"
				onclick="setOrderDay(this.name);" /> <input id="nextWeek"
				name="nextWeek" type="button" value="　翌週　"
				onclick="setOrderDay(this.name);" /> <input id="reloadBtn"
				type="button" value="　更新　" onclick="searchOrder()" />
		</div>
		<%-- <div id="SearchBlock_Bottom"> --%>
		<%-- <input id="prevMonth"  name="prevMonth" type="button" value="　先月　" onclick="setOrderDay(this.name);" /> --%>
		<%-- <input id="prevWeek"  name="prevWeek" type="button" value="　先週　" onclick="setOrderDay(this.name);" /> --%>
		<%-- 2011.Dec カレンダー追加 --%>
		<%-- <div id="searchDate" ondblclick="showCalendar()"><%= startDate %> ～ <%= endDate %></div> --%>
		<%-- &nbsp;		 --%>
		<%-- <div id="searchDate" class="DateDisplay" onclick="showCalendar()"><%= startDate %></div> --%>
		<%-- &nbsp;&nbsp;～&nbsp; --%>
		<%-- <div id="searchDate2" class="DateDisplay" onclick="showCalendar()"><%= endDate %></div> --%>
		<%-- &nbsp;&nbsp; --%>
		<%-- <input id="today" name="today" type="button" value="　本日　" onclick="setToday();" /> --%>
		<%-- <input id="nextWeek"  name="nextWeek" type="button" value="　来週　" onclick="setOrderDay(this.name);" /> --%>
		<%-- <input id="nextMonth"  name="nextMonth" type="button" value="　来月　" onclick="setOrderDay(this.name);" /> --%>
		<%-- <input id="reloadBtn"  type="button" value="　更新　"  onclick="searchOrder()" /> --%>
		<%-- </div> --%>
		<%-- 2015.02.27 Mod S.Matsumoto@CST End KANA-C1-002 日にち切替ボタン変更 --%>
		<div id="IraiSyosaiTitle" class="shosai">
			<div id="IraiSyosai">依頼詳細</div>
		</div>
	</div>
	<div id="main">
		<div id="calendarArea">
			<div id="CalendarBlock" class="<%= calendarBlockClass %>"></div>
		</div>
		<div id="IraiSyosaiArea" class="shosai">
			<div id="IraiSyosaiBlock">
				<iframe id="IraiFrame" width="100%" src="">
				<head>
<meta http-equiv="X-UA-Compatible" content="IE=7" />
				</head>
				</iframe>
			</div>
		</div>
	</div>
	<div id="OrderArea">
		<div id="OrderBlock"></div>
	</div>
	<!--  2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更 -->
	<div class="contextMenu" id="change">
		<ul style="width: 165%;">
			<li id="timeRoomChange">予約時刻・検査室変更</li>
		</ul>
	</div>
	<!--  2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更 -->
</body>
</html>
