package com.nexturn.hms.exceptions;

public class PrescriptionNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PrescriptionNotFoundException(String msg) {
		super(msg);
	}
}