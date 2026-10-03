package com.nexturn.hms.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record RescheduleRequestDto(
		@NotNull @FutureOrPresent LocalDate date,
		@NotNull LocalTime startTime) {

}