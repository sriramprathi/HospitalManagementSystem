package com.nexturn.hms.dto;

import java.time.LocalDate;

import com.nexturn.hms.entity.Blood;
import com.nexturn.hms.entity.Gender;
import com.nexturn.hms.entity.PatientType;

public record PatientDto(
		    int patientId,
	        String firstName,
	        String lastName,
	        Gender gender,
	        LocalDate dateOfBirth,
	        int age,
	        String phoneNumber,
	        Blood bloodType,
	        String emergencyContact,
	        PatientType patientType,
	        AddressDto address) {

}
