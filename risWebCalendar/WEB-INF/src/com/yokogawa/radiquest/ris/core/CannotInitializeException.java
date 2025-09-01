package com.yokogawa.radiquest.ris.core;

public class CannotInitializeException extends Exception {

	public CannotInitializeException() {
		super();
	}

	public CannotInitializeException(String message) {
		super(message);
	}

	public CannotInitializeException(Throwable throwable) {

	}

	public CannotInitializeException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
