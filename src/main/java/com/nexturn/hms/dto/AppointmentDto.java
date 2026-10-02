package com.nexturn.hms.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.nexturn.hms.entity.AppointmentStatus;

public record AppointmentDto(
        int appointmentId,
        int patientId,
        String patientName,
        int doctorId,
        String doctorName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String disease,
        AppointmentStatus status) {
}
