package com.nexturn.hms.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.nexturn.hms.entity.AppointmentStatus;

public record AppointmentResponseDto(
		int appointmentId,
		LocalDate date,
		LocalTime startTime,
		LocalTime endTime,
		String disease,
		AppointmentStatus status,
		int patientId,
		String patientName,
		int doctorId,
		String doctorName) {

}