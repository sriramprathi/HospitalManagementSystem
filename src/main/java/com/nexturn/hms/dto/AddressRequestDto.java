package com.nexturn.hms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequestDto(
		@NotBlank @Size(max = 40) String street,
		@NotBlank @Size(max = 40) String city,
		@NotBlank @Size(max = 40) String state) {

}