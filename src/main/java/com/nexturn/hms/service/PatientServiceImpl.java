package com.nexturn.hms.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexturn.hms.dto.NewUserRequestDto;
import com.nexturn.hms.dto.PatientRegisteredResponseDto;
import com.nexturn.hms.dto.PatientRequestDto;
import com.nexturn.hms.dto.PatientResponseDto;
import com.nexturn.hms.dto.UserCredentialsResponseDto;
import com.nexturn.hms.entity.Patient;
import com.nexturn.hms.entity.Role;
import com.nexturn.hms.exceptions.PatientNotFoundException;
import com.nexturn.hms.repository.PatientRepository;
import com.nexturn.hms.repository.UserLoginRepository;

@Service
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepo;
	private final UserLoginRepository loginRepo;
	private final UserLoginService userLoginService;
	private final ModelMapper modelMapper;

	public PatientServiceImpl(PatientRepository patientRepo, UserLoginRepository loginRepo,
			UserLoginService userLoginService, ModelMapper modelMapper) {
		this.patientRepo = patientRepo;
		this.loginRepo = loginRepo;
		this.userLoginService = userLoginService;
		this.modelMapper = modelMapper;
	}

	@Override
	@Transactional
	public PatientRegisteredResponseDto registerPatient(PatientRequestDto dto) {
		// 1. create the login first (role is always PATIENT, never taken from the request)
		UserCredentialsResponseDto creds = userLoginService
				.registerNewUser(new NewUserRequestDto(dto.firstName(), dto.lastName(), Role.Patient));

		// 2. save the patient linked to that login
		Patient patient = new Patient();
		patient.setLogin(loginRepo.getReferenceById(creds.userId()));
		applyDetails(patient, dto);
		patient = patientRepo.save(patient);

		return new PatientRegisteredResponseDto(patient.getPatientId(), creds.userName(), creds.password());
	}

	@Override
	public PatientResponseDto getPatientById(int patientId) {
		return toResponse(findPatient(patientId));
	}

	@Override
	public PatientResponseDto getPatientByUserId(int userId) {
		Patient patient = patientRepo.findByLoginUserId(userId)
				.orElseThrow(() -> new PatientNotFoundException("Patient not found for user id: " + userId));
		return toResponse(patient);
	}

	@Override
	@Transactional
	public PatientResponseDto updatePatient(int patientId, PatientRequestDto dto) {
		Patient patient = findPatient(patientId);
		applyDetails(patient, dto);
		return toResponse(patientRepo.save(patient));
	}

	@Override
	public List<PatientResponseDto> getAllPatients() {
		return patientRepo.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional
	public void deletePatient(int patientId) {
		Patient patient = findPatient(patientId);
		// deleting the user also removes the patient row (cascade = ALL on UserLogin)
		userLoginService.deleteUser(patient.getLogin().getUserId());
	}

	private Patient findPatient(int patientId) {
		return patientRepo.findById(patientId)
				.orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + patientId));
	}

	private void applyDetails(Patient patient, PatientRequestDto dto) {
		modelMapper.map(dto, patient);
	}

	private PatientResponseDto toResponse(Patient p) {
		return new PatientResponseDto(p.getPatientId(), p.getFirstName(), p.getLastName(), p.getGender(),
				p.getDateOfBirth(), p.getPhoneNumber(), p.getBloodType(), p.getEmergencyContact(),
				p.getPatientType(), p.getLogin().getUserId());
	}
}