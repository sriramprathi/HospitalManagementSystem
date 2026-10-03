package com.nexturn.hms.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PrescriptionRequestDto(
		@Size(max = 500) String notes,
		@NotEmpty @Valid List<PrescriptionItemRequestDto> items) {

}