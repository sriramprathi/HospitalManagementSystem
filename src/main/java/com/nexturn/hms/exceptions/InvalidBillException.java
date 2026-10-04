package com.nexturn.hms.exceptions;

public class InvalidBillException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidBillException(String msg) {
		super(msg);
	}
}