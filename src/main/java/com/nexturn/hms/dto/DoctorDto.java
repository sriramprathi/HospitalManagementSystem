package com.nexturn.hms.dto;

import java.math.BigDecimal;

import com.nexturn.hms.entity.Department;

public record DoctorDto(
		    int doctorId,
	        String firstName,
	        String lastName,
	        Department department,
	        String qualification,
	        String phoneNumber,
	        BigDecimal consultationFee) {

}
