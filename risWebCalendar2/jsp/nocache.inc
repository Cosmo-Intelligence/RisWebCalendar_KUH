<%! private String getHTTPDate() {
    java.text.SimpleDateFormat formatter =
      new java.text.SimpleDateFormat("E, dd MMM yyyy hh:mm:ss zzz", java.util.Locale.US);
    formatter.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
    return formatter.format(new java.util.Date());
  }
%><%
  response.setHeader("Expires", getHTTPDate());
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","no-cache");
%>
