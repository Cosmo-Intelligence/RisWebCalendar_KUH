package com.yokogawa.radiquest.ris.action;

import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
*
* JSP処理「Action」クラスの基本クラス
*
*/

public abstract class FormAction {
	private HttpServletRequest request;
	private HttpServletResponse response;    

	protected FormAction(HttpServletRequest request, HttpServletResponse response) {
		setRequest(request);       
		setResponse(response);
	}
	/**
	* (HTTPプロトコル)レスポンスの設定
	* @param response　HTTPレスポンス
	*/
	protected void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * (HTTPプロトコル)レスポンスの取得
	 * @return　HTTPレスポンス
	 */
	protected HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * (HTTPプロトコル)リクエストの設定
	 * @param request　HTTPリクエスト
	 */
	protected void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * (HTTPプロトコル)リクエストの取得
	 * @return　HTTPリクエスト
	 */
	protected HttpServletRequest getRequest() {
		return request;
	}

	/**
	 *　送信されたHTMLフォームの「input」から文字列を取得します。
	 * @param name	パラメータ名
	 * @return	パラメータの値
	 */
	protected String parseStringParam(String name) {
		String sparam = getRequest().getParameter(name);
		if (sparam == null)	{
			sparam = "";
		}
		
		return sparam;
	}
	
	/**
	 *　送信されたHTMLフォームの「input」から数字を取得します。
	 * @param name	パラメータ名
	 * @return	パラメータの値
	 */
	protected int parseIntParam(String name) {
		String sparam = getRequest().getParameter(name);
		return parseIntString(sparam);
	}

	/**
	 * 文字列から数字を取得します
	 * @param sparam	文字列
	 * @return			取得した数字
	 */
	protected int parseIntString(String sparam) {
		int iparam = -1;
		try {
			if ((sparam != null) && !(sparam.equals(""))) {
				iparam = Integer.parseInt(sparam);
			}
		} catch (NumberFormatException ex) {
			iparam = -1;
		}
		return iparam;
	}

	/**
	 * 現在の日付を取得する
	 * @param
	 * @return		String sToday
	 */
	public String getToday(){
		String sToday = "";
		Calendar cal = Calendar.getInstance();
		
		int nYear = cal.get(Calendar.YEAR);
		int nMonth = cal.get(Calendar.MONTH) + 1;
		int nDate = cal.get(Calendar.DATE);

		String sYear = Integer.toString(nYear);
		String sMonth = Integer.toString(nMonth);
		String sDate = Integer.toString(nDate);
		
		sToday = sYear + "/" + sMonth + "/" + sDate;
		
		return sToday;
	}

//	/**
//	 * HTTPのパラメータ名を取得します
//	 * @param prefix Prefix of the parameter name
//	 * @return　The first parameter name, or "" if not found.
//
//	 */
//	protected String parseNameByPrefix(String prefix) {
//		String paramKey ="";
//		String candidate = "";
//		int plength = prefix.length();
//		Enumeration keys = request.getParameterNames();
//		
//		if (plength < 1) {
//			return paramKey;
//		}
//		
//		while (keys.hasMoreElements()) {
//			candidate = (String) keys.nextElement();
//			if (candidate.length() > plength) {
//				if (candidate.substring(0, plength).equals(prefix)) {
//					paramKey = candidate;
//					break;
//				}
//			}
//		}
//		return paramKey;
//	}
}