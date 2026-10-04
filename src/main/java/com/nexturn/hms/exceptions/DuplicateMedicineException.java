package com.nexturn.hms.exceptions;

public class DuplicateMedicineException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DuplicateMedicineException(String msg) {
		super(msg);
	}
}