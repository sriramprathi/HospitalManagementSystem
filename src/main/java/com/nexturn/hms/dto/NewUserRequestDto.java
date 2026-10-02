package com.nexturn.hms.dto;

import com.nexturn.hms.entity.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record NewUserRequestDto(
		@NotBlank @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain letters only") String firstName,
		@NotBlank @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain letters only") String lastName,
		@NotNull Role role) {
}