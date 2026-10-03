package com.nexturn.hms.exceptions;

public class PrescriptionAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PrescriptionAlreadyExistsException(String msg) {
		super(msg);
	}
}