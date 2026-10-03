package com.nexturn.hms.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AppointmentRequestDto(
		@Positive int patientId,
		@Positive int doctorId,
		@NotNull @FutureOrPresent LocalDate date,
		@NotNull LocalTime startTime,
		@Size(max = 100) String disease) {

}