package com.nexturn.hms.exceptions;

public class DoctorNotFoundException extends RuntimeException{

	public DoctorNotFoundException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;

}
