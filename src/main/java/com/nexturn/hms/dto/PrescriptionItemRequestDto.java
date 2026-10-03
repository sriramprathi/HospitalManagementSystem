package com.nexturn.hms.dto;

import jakarta.validation.constraints.Positive;

public record PrescriptionItemRequestDto(
		@Positive int medicineId,
		@Positive int quantity,
		@Positive int duration,
		@Positive int frequency) {

}