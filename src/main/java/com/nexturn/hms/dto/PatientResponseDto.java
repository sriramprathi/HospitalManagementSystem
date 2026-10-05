package com.nexturn.hms.dto;

import java.time.LocalDate;
import com.nexturn.hms.entity.Gender;

public record PatientResponseDto(
		int patientId,
		String firstName,
		String lastName,
		Gender gender,
		LocalDate dateOfBirth,
		String phoneNumber,
		int userId) {

}