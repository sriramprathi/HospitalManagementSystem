package com.nexturn.hms.exceptions;

public class MedicineInUseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MedicineInUseException(String msg) {
		super(msg);
	}
}