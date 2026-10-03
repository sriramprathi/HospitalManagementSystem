package com.nexturn.hms.dto;

import java.math.BigDecimal;

import com.nexturn.hms.entity.Department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DoctorRequestDto(
		@NotBlank @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain letters only") String firstName,
		@NotBlank @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain letters only") String lastName,
		@NotNull Department department,
		@NotBlank @Size(max = 50) String qualification,
		@NotBlank @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String phoneNumber,
		@NotNull @Positive BigDecimal consultationFee) {

}