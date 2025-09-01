package com.yokogawa.radiquest.ris.core;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * ロガー生成クラス
 * 
 * @author kuroyama
 */
public class ReservationFormLoggerFactory {
	public static final String PROPERTIES_FILE_NAME = "log4j.xml";

	private static final ReservationFormLoggerFactory factory = new ReservationFormLoggerFactory();

	private Logger logger = Logger.getLogger("ReservationForm");

	private ReservationFormLoggerFactory() {
	}

	public static ReservationFormLoggerFactory getInstance() {
		return factory;
	}

	public void init(String filePath) throws CannotInitializeException {
		DOMConfigurator.configure(filePath);
	}

	/**
	 * Loggerを取得する。
	 * 
	 * @return logger
	 */
	public Logger getLogger() {
		return logger;
	}
}
