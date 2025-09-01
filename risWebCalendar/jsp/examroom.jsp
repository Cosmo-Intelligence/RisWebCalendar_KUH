<%@ page pageEncoding="UTF-8"%>
<%@ include file="nocache.inc"%>
<%@ page contentType="text/javascript; charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.yokogawa.radiquest.ris.bean.ExamRoom"%>
<%@ page import="com.yokogawa.radiquest.ris.servlet.ActionType"%>
<%@ page import="com.yokogawa.radiquest.ris.servlet.Parameters"%>

[
<%
	List examRooms = (List) request.getAttribute(Parameters.EXAM_ROOMS);
	for (int i = 0; i < examRooms.size(); i++) {
		if (i > 0)
			out.print(", ");
		ExamRoom examRoom = (ExamRoom) examRooms.get(i);
%>
{ "id" : "<%= examRoom.getExamRoomID() %>", "name" : "<%= examRoom.getExamRoomName() %>"
}
<%
	}
%>
]
