package com.nexturn.hms.service;

import java.util.List;

import com.nexturn.hms.dto.DoctorRegisteredResponseDto;
import com.nexturn.hms.dto.DoctorRequestDto;
import com.nexturn.hms.dto.DoctorResponseDto;
import com.nexturn.hms.entity.Department;

public interface DoctorService {
	DoctorRegisteredResponseDto addDoctor(DoctorRequestDto dto);
	DoctorResponseDto getDoctorById(int doctorId);
	DoctorResponseDto getDoctorByUserId(int userId);
	List<DoctorResponseDto> getAllDoctors();
	List<DoctorResponseDto> getDoctorsByDepartment(Department department);
	DoctorResponseDto updateDoctor(int doctorId, DoctorRequestDto dto);
	void deleteDoctor(int doctorId);
}
