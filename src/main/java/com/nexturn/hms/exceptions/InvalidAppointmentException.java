package com.nexturn.hms.exceptions;

public class InvalidAppointmentException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidAppointmentException(String msg) {
		super(msg);
	}
}