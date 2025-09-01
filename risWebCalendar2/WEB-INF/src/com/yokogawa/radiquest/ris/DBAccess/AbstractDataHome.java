package com.yokogawa.radiquest.ris.DBAccess;

import org.apache.log4j.Logger;

import com.yokogawa.radiquest.ris.core.ReservationFormLoggerFactory;

public abstract class AbstractDataHome {
	protected Logger getLogger() {
		return ReservationFormLoggerFactory.getInstance().getLogger();
	}

	protected String trimData(String s) {
		return convertMS932(s);
	}

	public String convertMS932(String text) {
		if (text == null)
			return null;

		text = text.replaceAll("\u301C", "\uFF5E"); // ～
		text = text.replaceAll("\u2016", "\u2225"); // ∥
		text = text.replaceAll("\u2212", "\uFF0D"); // －
		text = text.replaceAll("\u00A2", "\uFFE0"); // ￠
		text = text.replaceAll("\u00A3", "\uFFE1"); // ￡
		text = text.replaceAll("\u00AC", "\uFFE2"); // ￢

		return text;
	}

	/**
	 * 文字列をHTML用に変換します
	 * @param escapeString	元の文字列
	 * @return	変換した文字列
	 */
	public static String makeHtmlEntities(String escapeString) {
		if (escapeString == null) {
			return null;
		}

		escapeString = escapeString.replaceAll("&", "&amp;");
		escapeString = escapeString.replaceAll("<", "&lt;");
		escapeString = escapeString.replaceAll(">", "&gt;");
		escapeString = escapeString.replaceAll("\"", "&quot;");

		// ISSUE: when apostrophy entity occurs in an attribute with javascript,
		// it is treated as a literal apostrophe and is executed. 
		// WORKAROUND: use rsquo entity instead.		
		//		escapeString = escapeString.replaceAll("'", "&#39;");
		//		escapeString = escapeString.replaceAll("'", "&rsquo;");		

		return escapeString;
	}
}
