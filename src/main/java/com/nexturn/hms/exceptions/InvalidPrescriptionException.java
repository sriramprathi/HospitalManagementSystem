package com.nexturn.hms.exceptions;

public class InvalidPrescriptionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidPrescriptionException(String msg) {
		super(msg);
	}
}