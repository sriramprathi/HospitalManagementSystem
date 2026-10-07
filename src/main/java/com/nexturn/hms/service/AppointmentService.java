package com.nexturn.hms.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.nexturn.hms.dto.AppointmentRequestDto;
import com.nexturn.hms.dto.AppointmentResponseDto;
import com.nexturn.hms.dto.RescheduleRequestDto;
import com.nexturn.hms.entity.AppointmentStatus;

public interface AppointmentService {
	AppointmentResponseDto bookAppointment(AppointmentRequestDto dto);
	AppointmentResponseDto cancelAppointment(int appointmentId);
	AppointmentResponseDto rescheduleAppointment(int appointmentId, RescheduleRequestDto dto);
	AppointmentResponseDto getAppointmentById(int appointmentId);
	List<AppointmentResponseDto> getAppointmentsByPatient(int patientId);
	List<AppointmentResponseDto> getAppointmentsByDoctor(int doctorId);
	List<AppointmentResponseDto> getAppointmentsByDate(LocalDate date);
	AppointmentResponseDto updateStatus(int appointmentId, AppointmentStatus status);
	
	List<LocalTime> getAvailableSlots(int doctorId, LocalDate date);

}