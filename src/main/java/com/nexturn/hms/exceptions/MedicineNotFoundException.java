package com.nexturn.hms.exceptions;

public class MedicineNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MedicineNotFoundException(String msg) {
		super(msg);
	}
}