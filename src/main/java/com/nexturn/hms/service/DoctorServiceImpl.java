package com.nexturn.hms.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nexturn.hms.dto.DoctorRegisteredResponseDto;
import com.nexturn.hms.dto.DoctorRequestDto;
import com.nexturn.hms.dto.DoctorResponseDto;
import com.nexturn.hms.dto.NewUserRequestDto;
import com.nexturn.hms.dto.UserCredentialsResponseDto;
import com.nexturn.hms.entity.Department;
import com.nexturn.hms.entity.Doctor;
import com.nexturn.hms.entity.Role;
import com.nexturn.hms.exceptions.DoctorNotFoundException;
import com.nexturn.hms.repository.DoctorRepository;
import com.nexturn.hms.repository.UserLoginRepository;

@Service
public class DoctorServiceImpl implements DoctorService {

	private final DoctorRepository doctorRepo;
	private final UserLoginRepository loginRepo;
	private final UserLoginService userLoginService;

	public DoctorServiceImpl(DoctorRepository doctorRepo, UserLoginRepository loginRepo,
			UserLoginService userLoginService) {
		this.doctorRepo = doctorRepo;
		this.loginRepo = loginRepo;
		this.userLoginService = userLoginService;
	}

	@Override
	@Transactional
	public DoctorRegisteredResponseDto addDoctor(DoctorRequestDto dto) {
		UserCredentialsResponseDto creds = userLoginService
				.registerNewUser(new NewUserRequestDto(dto.firstName(), dto.lastName(), Role.Doctor));

		Doctor doctor = new Doctor();
		doctor.setLogin(loginRepo.getReferenceById(creds.userId()));
		applyDetails(doctor, dto);
		doctor = doctorRepo.save(doctor);

		return new DoctorRegisteredResponseDto(doctor.getDoctorId(), creds.userName(), creds.password());
	}

	@Override
	public DoctorResponseDto getDoctorById(int doctorId) {
		return toResponse(findDoctor(doctorId));
	}

	@Override
	public DoctorResponseDto getDoctorByUserId(int userId) {
		Doctor doctor = doctorRepo.findByLoginUserId(userId)
				.orElseThrow(() -> new DoctorNotFoundException("Doctor not found for user id: " + userId));
		return toResponse(doctor);
	}

	@Override
	public List<DoctorResponseDto> getAllDoctors() {
		return doctorRepo.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	public List<DoctorResponseDto> getDoctorsByDepartment(Department department) {
		return doctorRepo.findByDepartment(department).stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional
	public DoctorResponseDto updateDoctor(int doctorId, DoctorRequestDto dto) {
		Doctor doctor = findDoctor(doctorId);
		applyDetails(doctor, dto);
		return toResponse(doctorRepo.save(doctor));
	}

	@Override
	@Transactional
	public void deleteDoctor(int doctorId) {
		Doctor doctor = findDoctor(doctorId);
		// deleting the user also removes the doctor row (cascade = ALL on UserLogin)
		userLoginService.deleteUser(doctor.getLogin().getUserId());
	}

	private Doctor findDoctor(int doctorId) {
		return doctorRepo.findById(doctorId)
				.orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));
	}

	private void applyDetails(Doctor doctor, DoctorRequestDto dto) {
		doctor.setFirstName(dto.firstName());
		doctor.setLastName(dto.lastName());
		doctor.setDepartment(dto.department());
		doctor.setQualification(dto.qualification());
		doctor.setPhoneNumber(dto.phoneNumber());
		doctor.setConsultationFee(dto.consultationFee());
	}

	private DoctorResponseDto toResponse(Doctor d) {
		return new DoctorResponseDto(d.getDoctorId(), d.getFirstName(), d.getLastName(), d.getDepartment(),
				d.getQualification(), d.getPhoneNumber(), d.getConsultationFee(), d.getLogin().getUserId());
	}
}