package com.nexturn.hms.service;

import java.util.List;
import com.nexturn.hms.dto.PatientRequestDto;
import com.nexturn.hms.dto.PatientRegisteredResponseDto;
import com.nexturn.hms.dto.PatientResponseDto;


public interface PatientService {
    
    PatientRegisteredResponseDto registerPatient(PatientRequestDto dto);
	PatientResponseDto getPatientById(int patientId);
	PatientResponseDto getPatientByUserId(int userId);
	PatientResponseDto updatePatient(int patientId, PatientRequestDto dto);
	List<PatientResponseDto> getAllPatients();
	void deletePatient(int patientId);
}
