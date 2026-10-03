package com.nexturn.hms.service;

import java.util.List;

import com.nexturn.hms.dto.PrescriptionRequestDto;
import com.nexturn.hms.dto.PrescriptionResponseDto;

public interface PrescriptionService {
	PrescriptionResponseDto createPrescription(int appointmentId, PrescriptionRequestDto dto);
	PrescriptionResponseDto getPrescriptionById(int prescriptionId);
	PrescriptionResponseDto getPrescriptionByAppointment(int appointmentId);
	List<PrescriptionResponseDto> getPrescriptionsByPatient(int patientId);
	PrescriptionResponseDto updatePrescription(int prescriptionId, PrescriptionRequestDto dto);
	void deletePrescription(int prescriptionId);
}