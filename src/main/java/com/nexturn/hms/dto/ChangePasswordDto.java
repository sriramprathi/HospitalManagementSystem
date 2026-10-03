package com.nexturn.hms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
		@NotBlank String oldPassword,
		@NotBlank @Size(max = 50) String newPassword) {

}