<%@ page pageEncoding="UTF-8"%>
<%@ include file="nocache.inc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.yokogawa.radiquest.ris.servlet.ActionType"%>
<%@ page import="com.yokogawa.radiquest.ris.servlet.Parameters"%>
<%@ page import="com.yokogawa.radiquest.ris.core.Configuration"%>
<%@ page import="com.yokogawa.radiquest.ris.bean.*"%>

<%
// 時間と日付に関連付けられた検査一覧
HashMap timeDateOrderCalenderMap = new HashMap();
// 日付リスト
List dateList = new ArrayList();
// 時間枠リスト
List timeFrameList = new ArrayList();
// 日毎のDOMクラスリスト
List domClassList = new ArrayList();

SimpleDateFormat hhmm = new SimpleDateFormat("HHmm");

SimpleDateFormat hhmm1 = new SimpleDateFormat("HH:mm");

SimpleDateFormat hmm = new SimpleDateFormat("Hmm");

SimpleDateFormat hmm1 = new SimpleDateFormat("H:mm");

SimpleDateFormat yymmddee = new SimpleDateFormat("yyyyMM/dd(EE)");

SimpleDateFormat yyMMdd = new SimpleDateFormat("yyyyMMdd");

if( request.getAttribute(Parameters.KENSALIST) != null ){
	timeDateOrderCalenderMap = (HashMap)request.getAttribute(Parameters.KENSALIST);
}
if( request.getAttribute(Parameters.TIMEFRAME) != null ){
	timeFrameList = (ArrayList)request.getAttribute(Parameters.TIMEFRAME);
}
if( request.getAttribute(Parameters.DATELIST) != null ){
	dateList = (ArrayList)request.getAttribute(Parameters.DATELIST);
}

ArrayList kyusinbi_DateList = (ArrayList)request.getAttribute(Parameters.KYUSINBI_DATE);

// 表示パターン
//Configuration configuration = Configuration.getInstance();
//int dispPattern = configuration.getDispPattern();

// 時間枠
String strTimeFrame = request.getParameter(Parameters.TIMEFRAME);
if (strTimeFrame == null)
	strTimeFrame = "";

// 検査種別ID
String kensaType = request.getParameter(Parameters.KENSA_TYPE);
if (kensaType == null)
	kensaType = "";

// 検査室ID
String examRoom = request.getParameter(Parameters.EXAM_ROOM);
if (examRoom == null)
	examRoom = "";
	
// 2012.03.02 Add Yk.Suzuki@CIJ Start SSGH-4-003
// 区切り文字
String separator = Configuration.getInstance().getCalseparator();
// 2012.03.02 Add Yk.Suzuki@CIJ End   SSGH-4-003
// 2013.11.19 Add Yk.Suzuki@CIJ Start NCC_C-2-TKC-R005
// オーダ件数 単位文字
String orderCountUnit = Configuration.getInstance().getOrderCountUnit();
// 2013.11.19 Add Yk.Suzuki@CIJ End   NCC_C-2-TKC-R005
%>

<script type="text/javascript">
   <!--
   searchKensaType = "<%= kensaType %>";
   searchExamRoom = "<%= examRoom %>";
   searchTimeFrame = "<%= strTimeFrame %>";
   //-->
   </script>

<div id="dateblock">
	<table>
		<tbody>
			<tr>
				<td class="dateOffset"><div>&nbsp;</div></td>
				<%
	Date dToday = new Date();
	int nToday = Integer.parseInt(yyMMdd.format(dToday));

	for(int i = 0; i < dateList.size(); i++){
		int date = ((Integer)dateList.get(i)).intValue();
		//SPAN埋め込み用
		String defStrDate = String.valueOf(date);
		while (defStrDate.length() < 12){
			defStrDate += "0";
		}
		// 月が1づつずれる。0 -> 1月
		date -= 100;
		String strDate = String.valueOf(date);
		if (strDate.length() < 8) {
			strDate = "0" + strDate;
		}
		String year = strDate.substring(0, 4);
		String month = strDate.substring(4, 6);
		String day = strDate.substring(6, 8);
		GregorianCalendar ct = new GregorianCalendar(Integer.valueOf(year)
				.intValue(), Integer.valueOf(month).intValue(), Integer
				.valueOf(day).intValue());

		strDate = yyMMdd.format(ct.getTime());

		// tdに与えるDOMクラス名の作成(日付に対する枠の色をスタイルシートで設定する)
		String domClass = "";
		// 土日(saturday, sunday)・曜日のクラス名を常に設定する。
		switch (ct.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			domClass = "sunday";
			break;
		case Calendar.MONDAY:
			domClass = "monday";
			break;
		case Calendar.TUESDAY:
			domClass = "tuesday";
			break;
		case Calendar.WEDNESDAY:
			domClass = "wednesday";
			break;
		case Calendar.THURSDAY:
			domClass = "thursday";
			break;
		case Calendar.FRIDAY:
			domClass = "friday";
			break;
		case Calendar.SATURDAY:
			domClass = "saturday";
			break;
		}
		// 当日(today)
		if (Integer.parseInt(strDate) == nToday)
			domClass += " today";
		// 休診日(nonconsultationDay)
		DateBean dateBean = (DateBean) kyusinbi_DateList.get(i);
		if (dateBean.isKyusinbiFlg()) {
			domClass += " nonconsultationDay";
		}

		domClassList.add(domClass);

		// 年月/日(曜日)の形 : 例 200810/20(月)
		String yMDE = yymmddee.format(ct.getTime());
		yMDE = yMDE.substring(4, yMDE.length());
%>
				<td id="header<%= strDate %>" class="<%= domClass %>">
					<div class="date">
						<%= yMDE %>
						<span class="dateColNum" style="display: none;"><%=i%></span> <span
							class="datetime" style="display: none;"><%= defStrDate %></span>
					</div>
				</td>
				<%
    }
%>
			</tr>
		</tbody>
	</table>
</div>
<div id="timeblock">
	<table>
		<tbody>
			<%
	for(int i = 0; i < timeFrameList.size()-1; i++){
		String timeFrame = timeFrameList.get(i).toString();
		// 現在見ている時間枠内の検査を取得
		HashMap dateOrderCalenderMap = (HashMap)timeDateOrderCalenderMap.get(timeFrame);
		String strDisplayTimeFrame = timeFrame;
		if(!timeFrame.equalsIgnoreCase("FREE")){
			// 表示用の時間枠は時分なので秒の部分は消す
			int displayTimeFrame = Integer.parseInt(timeFrame)/100;
			strDisplayTimeFrame = String.valueOf(displayTimeFrame);
		}
		Date dateDisplayTimeFrame = null;
		String formatDisplayTimeFrame = "";
		if (strDisplayTimeFrame.equalsIgnoreCase("FREE")){
			formatDisplayTimeFrame = strDisplayTimeFrame;
		}else{
			if(strDisplayTimeFrame.length() < 4){
				while(strDisplayTimeFrame.length() < 4 ){
					if(strDisplayTimeFrame.length() == 3){
						dateDisplayTimeFrame = hmm.parse(strDisplayTimeFrame);
						// 時間枠表示用に直す
						formatDisplayTimeFrame = hmm1.format(dateDisplayTimeFrame);
					}
					StringBuffer sbDisplayTimeFrame = new StringBuffer(strDisplayTimeFrame);
					// 午前8時や午前9時の場合は頭に0をつけ4桁にする。(spanタグの値用に)
					sbDisplayTimeFrame.insert(0, "0");
					strDisplayTimeFrame = sbDisplayTimeFrame.toString();
				}
			}else{
				dateDisplayTimeFrame = hhmm.parse(strDisplayTimeFrame);
				// 時間枠表示用に直す
				formatDisplayTimeFrame = hhmm1.format(dateDisplayTimeFrame);
			}
		}
%>
			<tr>
				<td class="time">
					<%
		if(formatDisplayTimeFrame.equalsIgnoreCase("FREE")){

%>
					<div><%= formatDisplayTimeFrame %></div> <%
		}else{
%>
					<div><%= formatDisplayTimeFrame %>
						～
					</div> <%
		}
%>
				</td>
				<%
		for(int j = 0; j < dateList.size(); j++){
			boolean disableFlag = false;
			int date = ((Integer)dateList.get(j)).intValue();
			if (date < nToday) {
				disableFlag = true;
			}
			// 現在見ている時間、日付に一致する検査一覧を取得。
			List orderCalendarItemList = (ArrayList)dateOrderCalenderMap.get(new Integer(date));

			String domClass = (String)domClassList.get(j);
%>

				<%
			if (i == 0) {
%>
				<td id="data<%= date %>" class="<%= domClass %>">
					<%
			} else {
%>
				
				<td class="<%= domClass %>">
					<%
			}
%> <span class="date" style="display: none;"><%= date %></span>
					<div class="data">
						<span class="" datetime" style="display: none;"><%= date %><%= strDisplayTimeFrame %></span>
						<%
			for(int k = 0; k < orderCalendarItemList.size(); k++){
				OrderCalendarItem orderCalendarItem = (OrderCalendarItem)orderCalendarItemList.get(k);
				int time = orderCalendarItem.getKensaStartTime();
				String strTime1 = String.valueOf(time);
				String startTime = "";
				if(time != -1){
					if(time != 999999){
						if(strTime1.length() <= 2){
							StringBuffer sbTime = new StringBuffer(strTime1);
							while(strTime1.length() < 3){
								sbTime.insert(0, "0");
								strTime1 = sbTime.toString();
							}
							Date dateTime = hmm.parse(strTime1);
							startTime= hmm1.format(dateTime);
						}else{
							time /= 100;
							String strTime = String.valueOf(time);
							StringBuffer sbTime = new StringBuffer(strTime);
							if(strTime.length()==4){
								Date dateTime = hhmm.parse(strTime);
								startTime= hhmm1.format(dateTime);
							}else{
								while(strTime.length() < 3){
									// 3桁になるまで0を付加
									sbTime.insert(0, "0");
									strTime = sbTime.toString();
								}
								Date dateTime = hmm.parse(strTime);
								startTime= hmm1.format(dateTime);
							}
						}
					}else{
						startTime="99:99";
					}
				}

				// 依頼科(病棟)
				String sectionName = orderCalendarItem.getSectionRyakuName();
				if (sectionName.compareTo("") == 0) {
					sectionName = orderCalendarItem.getByoutouRyakuName();
				}

				if ((startTime == null || startTime.length() == 0)
						&& (sectionName == null || sectionName.length() == 0 )) {
					continue;
				}

				// 患者漢字
				String kanjiSimei = orderCalendarItem.getKanjiSimei();

				// 年齢
				String age = orderCalendarItem.getKensaDateAge();
				if(age != null && age.length() != 0){
					age = "(" + age + ")";
				}

				// 検査種別
				String kensaTypeRyakuName = orderCalendarItem.getKensaTypeRyakuName();

				// RI区分
/*
				String riOrder = orderCalendarItem.getRiOrder();
				if (riOrder != null && riOrder.length() > 1 && !riOrder.equals("なし")) {
					riOrder = "(" + riOrder.substring(0, 1) + ")";
				} else {
					riOrder = "";
				}
*/
				String riOrder = "";
				String riOrderFlg = orderCalendarItem.getRiOrderFlg();
				if (riOrderFlg.equals("1")) {
					riOrder = "(注)";
				} else if (riOrderFlg.equals("2")) {
					riOrder = "(検)";
				} else if (riOrderFlg.equals("3")) {
					riOrder = "(追)";
				}
				// 2014.05.07 Add T.Koudate@COSMO Start #2681
				else if (riOrderFlg.equals("4")) {
					riOrder = "(注検)";
				}
				// 2014.05.07 Add T.Koudate@COSMO End   #2681

				// 2012.03.06 Add Yk.Suzuki@CIJ Start SSGH-4-003
				if (riOrder != "")
				{
					riOrder += separator;
				}
				// 2012.03.06 Add Yk.Suzuki@CIJ End   SSGH-4-003

				// 部位
				String bui = orderCalendarItem.getBuiRyakuName();
				// 検査方法
				String kensaHouhou = orderCalendarItem.getKensaHouhouRyakuName();
				// 左右
				String sayuu = orderCalendarItem.getSayuuRyakuName();
				// 方向
				String houkou = orderCalendarItem.getHoukouRyakuName();
				// 検査室
				String kensaSitu = orderCalendarItem.getKensaSituName();
				if(kensaSitu != null && kensaSitu.length() != 0){
					kensaSitu = "(" + kensaSitu + ")";
				}
				// 部位セット
				String buiSet = orderCalendarItem.getBuiSet();

				// RIS識別ID
				String risID = orderCalendarItem.getRisID();

				// 検査ステータス
				String status = orderCalendarItem.getStatus();
				// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL004 表示項目の変更
				// 来院情報
				String rcInfo = "□";
				String rcDate = orderCalendarItem.getRcDate();
				if(rcDate != null && rcDate.length() != 0){
					rcInfo = "■";
				}
				// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL004 表示項目の変更
				
				// 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更
				// 検査種別ID
				String kensatypeID = orderCalendarItem.getKensatypeID();
				// 検査日
				int tempKensaDate = orderCalendarItem.getKensaDate();
				// 検査室名称
				String kensaSituTitle = orderCalendarItem.getKensaSituTitle();
				// 検査室ID
				String kensaSituID = orderCalendarItem.getKensaSituID();
				// 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更
				
%>
						<div class="dataItem status<%= status %>">
							<div class="simpleData">
								<!-- カレンダーエリア枠表示 -->
								<span class="calendarItem startTime"><%= startTime %></span> <span
									class="calendarItem kanjiSimei"><%= kanjiSimei %></span> <span
									class="calendarItem buiSet"><%= buiSet %></span>
							</div>
							<div class="detailData">
								<!-- 枠リスト表示 -->
								<span class="valColNum" style="display: none;"><%= j %></span> <span
									class="risID" style="display: none"><%= risID %></span>
								<!-- 2016.12.20 Add T.onoda@Cosmo Start KUMA205-3-CAL005 予約時刻、予定検査室の変更　-->
								<span class="kensatypeID" style="display: none"><%= kensatypeID %></span>
								<span class="kensaStartTime" style="display: none"><%= startTime %></span>
								<span class="kensastatus" style="display: none"><%= status %></span>
								<span class="kensaDate" style="display: none"><%= tempKensaDate %></span>
								<span class="kensaSituTitle" style="display: none"><%= kensaSituTitle %></span>
								<span class="kensaSituID" style="display: none"><%= kensaSituID %></span>
								<!-- 2016.12.20 Add T.onoda@Cosmo End KUMA205-3-CAL005 予約時刻、予定検査室の変更　-->
								<span class="calendarItem rcInfo"><%= rcInfo %></span> <span
									class="calendarItem kanjiSimei"><%= kanjiSimei %></span><span
									class="calendarItem buiSet"><%= buiSet %> </span><span
									class="calendarItem age"><%= age %></span> <span
									class="calendarItem sectionName"><%= sectionName %></span> <br>
								<span class="calendarItem kensaTypeRyakuName"><%= kensaTypeRyakuName %></span><span
									class="calendarItem riOrder"><%= riOrder %></span> <span
									class="calendarItem bui"><%= bui %></span><span
									class="calendarItem kensaHouhou"><%= kensaHouhou %></span><span
									class="calendarItem sayuu"><%= sayuu %></span><span
									class="calendarItem houkou"><%= houkou %></span> <span
									class="calendarItem kensaSitu"><%= kensaSitu %></span>
							</div>
						</div>
						<%
			}
%>
					</div>
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
<script type="text/javascript">
<!--
// 表示切替の反映
displayTypeChange();
// -->
</script>
