package com.nexturn.hms.dto;

import java.time.LocalDate;

import com.nexturn.hms.entity.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record PatientRequestDto(
		@NotBlank @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain letters only") String firstName,
		@NotBlank @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain letters only") String lastName,
		@NotNull Gender gender,
		@NotNull @Past LocalDate dateOfBirth,
		@NotBlank @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits") String phoneNumber) {

}