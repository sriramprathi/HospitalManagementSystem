package com.nexturn.hms.dto;

import java.time.LocalDate;

import com.nexturn.hms.entity.Blood;
import com.nexturn.hms.entity.Gender;
import com.nexturn.hms.entity.PatientType;

public record PatientResponseDto(
		int patientId,
		String firstName,
		String lastName,
		Gender gender,
		LocalDate dateOfBirth,
		String phoneNumber,
		Blood bloodType,
		String emergencyContact,
		PatientType patientType,
		int userId) {

}