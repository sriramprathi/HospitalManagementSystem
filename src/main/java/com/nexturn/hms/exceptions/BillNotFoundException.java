package com.nexturn.hms.exceptions;

public class BillNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BillNotFoundException(String msg) {
		super(msg);
	}
}