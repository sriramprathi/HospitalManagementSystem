package com.nexturn.hms.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.nexturn.hms.dto.LoginRequestDto;
import com.nexturn.hms.dto.LoginResponseDto;
import com.nexturn.hms.dto.PatientRegisteredResponseDto;
import com.nexturn.hms.dto.PatientRequestDto;
import com.nexturn.hms.dto.PatientResponseDto;
import com.nexturn.hms.entity.Gender;
import com.nexturn.hms.entity.Role;
import com.nexturn.hms.exceptions.PatientNotFoundException;
import com.nexturn.hms.service.PatientService;
import com.nexturn.hms.service.UserLoginService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional
class PatientServiceTest {

	@Autowired
	private PatientService patientService;
	@Autowired
	private UserLoginService userLoginService;
	@PersistenceContext
	private EntityManager entityManager;

	private PatientRequestDto patientDto() {
		return new PatientRequestDto("Ravi", "Kumar", Gender.values()[0], LocalDate.of(1995, 5, 10), "9876543210");
	}

	private int registerPatient() {
		return patientService.registerPatient(patientDto()).patientId();
	}

	@Test
	void testRegisterPatient() {
		PatientRegisteredResponseDto result = patientService.registerPatient(patientDto());
		assertTrue(result.patientId() > 0);
		assertFalse(result.userName().isBlank());
		assertFalse(result.password().isBlank());
	}

	@Test
	void testRegisterPatientCreatesPatientLogin() {
		PatientRegisteredResponseDto result = patientService.registerPatient(patientDto());
		LoginResponseDto login = userLoginService.verifyLogin(new LoginRequestDto(result.userName(), result.password()));
		assertEquals(Role.PATIENT, login.role());
	}

	@Test
	void testGetPatientById() {
		int patientId = registerPatient();
		PatientResponseDto result = patientService.getPatientById(patientId);
		assertEquals(patientId, result.patientId());
		assertEquals("Ravi", result.firstName());
		assertEquals("Kumar", result.lastName());
		assertEquals("9876543210", result.phoneNumber());
	}

	@Test
	void testGetPatientByInvalidId() {
		assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(99999));
	}

	@Test
	void testGetPatientByUserId() {
		int patientId = registerPatient();
		int userId = patientService.getPatientById(patientId).userId();
		PatientResponseDto result = patientService.getPatientByUserId(userId);
		assertEquals(patientId, result.patientId());
	}

	@Test
	void testGetPatientByInvalidUserId() {
		assertThrows(PatientNotFoundException.class, () -> patientService.getPatientByUserId(99999));
	}

	@Test
	void testUpdatePatient() {
		int patientId = registerPatient();
		PatientRequestDto updateDto = new PatientRequestDto("Ravi", "Sharma", Gender.values()[0],
				LocalDate.of(1990, 1, 1), "9000000000");
		PatientResponseDto result = patientService.updatePatient(patientId, updateDto);
		assertEquals(patientId, result.patientId());
		assertEquals("Sharma", result.lastName());
		assertEquals("9000000000", result.phoneNumber());
		assertEquals(LocalDate.of(1990, 1, 1), result.dateOfBirth());
	}

	@Test
	void testUpdateInvalidPatient() {
	    PatientRequestDto request = patientDto();

	    assertThrows(
	            PatientNotFoundException.class,
	            () -> patientService.updatePatient(99999, request)
	    );
	}

	@Test
	void testGetAllPatients() {
		registerPatient();
		List<PatientResponseDto> result = patientService.getAllPatients();
		assertFalse(result.isEmpty());
	}

	@Test
	void testDeletePatient() {
		int patientId = registerPatient();
		entityManager.flush();
		entityManager.clear();
		patientService.deletePatient(patientId);
		entityManager.flush();
		entityManager.clear();
		assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(patientId));
	}

	@Test
	void testDeleteInvalidPatient() {
		assertThrows(PatientNotFoundException.class, () -> patientService.deletePatient(99999));
	}
}