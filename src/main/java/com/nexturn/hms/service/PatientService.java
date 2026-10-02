package com.nexturn.hms.service;

import java.util.List;
import com.nexturn.hms.dto.NewPatientRequestDto;
import com.nexturn.hms.dto.PatientResponseDto;
import com.nexturn.hms.dto.UpdatePatientRequestDto;
import com.nexturn.hms.entity.Patient;

public interface PatientService {
	PatientResponseDto registerPatient(NewPatientRequestDto patientDto);
    PatientResponseDto updatePatient(UpdatePatientRequestDto patientDto);
    void deletePatient(int patientId);
    List<Patient> getAllPatients();
}
