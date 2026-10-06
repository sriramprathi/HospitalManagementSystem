package com.nexturn.hms.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.nexturn.hms.dto.DoctorRegisteredResponseDto;
import com.nexturn.hms.dto.DoctorRequestDto;
import com.nexturn.hms.dto.DoctorResponseDto;
import com.nexturn.hms.dto.LoginRequestDto;
import com.nexturn.hms.dto.LoginResponseDto;
import com.nexturn.hms.entity.Department;
import com.nexturn.hms.entity.Role;
import com.nexturn.hms.exceptions.DoctorNotFoundException;
import com.nexturn.hms.service.DoctorService;
import com.nexturn.hms.service.UserLoginService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional
class DoctorServiceTest {

	@Autowired
	private DoctorService doctorService;
	@Autowired
	private UserLoginService userLoginService;
	@PersistenceContext
	private EntityManager entityManager;

	private DoctorRequestDto doctorDto() {
		return new DoctorRequestDto("Test", "Doctor", Department.CARDIOLOGY, "MBBS", "9999999999",
				new BigDecimal("500.00"));
	}

	private int addDoctor() {
		return doctorService.addDoctor(doctorDto()).doctorId();
	}

	@Test
	void testAddDoctor() {
		DoctorRegisteredResponseDto result = doctorService.addDoctor(doctorDto());
		assertTrue(result.doctorId() > 0);
		assertFalse(result.userName().isBlank());
		assertFalse(result.password().isBlank());
	}

	@Test
	void testAddDoctorCreatesDoctorLogin() {
		DoctorRegisteredResponseDto result = doctorService.addDoctor(doctorDto());
		LoginResponseDto login = userLoginService.verifyLogin(new LoginRequestDto(result.userName(), result.password()));
		assertEquals(Role.DOCTOR, login.role());
	}

	@Test
	void testGetDoctorById() {
		int doctorId = addDoctor();
		DoctorResponseDto result = doctorService.getDoctorById(doctorId);
		assertEquals(doctorId, result.doctorId());
		assertEquals("Test", result.firstName());
		assertEquals(Department.CARDIOLOGY, result.department());
		assertEquals(500.0, result.consultationFee().doubleValue());
	}

	@Test
	void testGetDoctorByInvalidId() {
		assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctorById(999999));
	}

	@Test
	void testGetDoctorByUserId() {
		int doctorId = addDoctor();
		int userId = doctorService.getDoctorById(doctorId).userId();
		DoctorResponseDto result = doctorService.getDoctorByUserId(userId);
		assertEquals(doctorId, result.doctorId());
	}

	@Test
	void testGetDoctorByInvalidUserId() {
		assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctorByUserId(99999));
	}

	@Test
	void testGetAllDoctors() {
		addDoctor();
		List<DoctorResponseDto> result = doctorService.getAllDoctors();
		assertFalse(result.isEmpty());
	}

	@Test
	void testGetDoctorsByDepartment() {
		addDoctor();
		List<DoctorResponseDto> result = doctorService.getDoctorsByDepartment(Department.CARDIOLOGY);
		assertFalse(result.isEmpty());
		assertEquals(Department.CARDIOLOGY, result.get(0).department());
	}

	@Test
	void testUpdateDoctor() {
		int doctorId = addDoctor();
		DoctorRequestDto updateDto = new DoctorRequestDto("Test", "Doctor", Department.CARDIOLOGY, "MD", "8888888888",
				new BigDecimal("700.00"));
		DoctorResponseDto result = doctorService.updateDoctor(doctorId, updateDto);
		assertEquals(doctorId, result.doctorId());
		assertEquals("MD", result.qualification());
		assertEquals("8888888888", result.phoneNumber());
		assertEquals(700.0, result.consultationFee().doubleValue());
	}

	@Test
	void testUpdateInvalidDoctor() {
	    DoctorRequestDto request = doctorDto();

	    assertThrows(
	            DoctorNotFoundException.class,
	            () -> doctorService.updateDoctor(99999, request)
	    );
	}
	@Test
	void testDeleteDoctor() {
		int doctorId = addDoctor();
		// reload from the database, the way a real request would
		entityManager.flush();
		entityManager.clear();
		doctorService.deleteDoctor(doctorId);
		entityManager.flush();
		entityManager.clear();
		assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctorById(doctorId));
	}

	@Test
	void testDeleteInvalidDoctor() {
		assertThrows(DoctorNotFoundException.class, () -> doctorService.deleteDoctor(99999));
	}
}