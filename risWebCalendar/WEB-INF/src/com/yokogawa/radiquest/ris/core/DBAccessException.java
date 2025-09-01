package com.yokogawa.radiquest.ris.core;

public class DBAccessException extends Exception {
	public DBAccessException() {
		super();
	}

	public DBAccessException(String message) {
		super(message);
	}

	public DBAccessException(Throwable cause) {
		super(cause);
	}

	public DBAccessException(String message, Throwable cause) {
		super(message, cause);
	}

}
