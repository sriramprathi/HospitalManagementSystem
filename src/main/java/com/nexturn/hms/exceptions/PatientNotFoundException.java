package com.nexturn.hms.exceptions;

public class PatientNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PatientNotFoundException(String msg) {
		super(msg);
	}
}