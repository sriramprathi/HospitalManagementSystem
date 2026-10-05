package com.nexturn.hms.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.nexturn.hms.dto.AppointmentRequestDto;
import com.nexturn.hms.dto.DoctorRequestDto;
import com.nexturn.hms.dto.MedicineRequestDto;
import com.nexturn.hms.dto.MedicineResponseDto;
import com.nexturn.hms.dto.PatientRequestDto;
import com.nexturn.hms.dto.PrescriptionItemRequestDto;
import com.nexturn.hms.dto.PrescriptionRequestDto;
import com.nexturn.hms.entity.Department;
import com.nexturn.hms.entity.Gender;
import com.nexturn.hms.entity.MedicineCategory;
import com.nexturn.hms.exceptions.DuplicateMedicineException;
import com.nexturn.hms.exceptions.MedicineInUseException;
import com.nexturn.hms.exceptions.MedicineNotFoundException;
import com.nexturn.hms.service.AppointmentService;
import com.nexturn.hms.service.DoctorService;
import com.nexturn.hms.service.MedicineService;
import com.nexturn.hms.service.PatientService;
import com.nexturn.hms.service.PrescriptionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional
class MedicineServiceTest {

	@Autowired
	private MedicineService medicineService;
	@Autowired
	private PatientService patientService;
	@Autowired
	private DoctorService doctorService;
	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private PrescriptionService prescriptionService;

	@PersistenceContext
	private EntityManager entityManager;

	private MedicineRequestDto medicineDto() {
		return new MedicineRequestDto("Med" + System.nanoTime(), new BigDecimal("25.00"), 100, MedicineCategory.CAPSULE);
	}

	@Test
	void testAddMedicine() {
		MedicineRequestDto dto = medicineDto();
		MedicineResponseDto result = medicineService.addMedicine(dto);
		assertTrue(result.medicineId() > 0);
		assertEquals(dto.medicineName(), result.medicineName());
		assertEquals(25.0, result.price().doubleValue());
		assertEquals(100, result.availability());
		assertEquals(MedicineCategory.CAPSULE, result.category());
	}

	@Test
	void testAddDuplicateMedicine() {
		MedicineRequestDto dto = medicineDto();
		medicineService.addMedicine(dto);
		assertThrows(DuplicateMedicineException.class, () -> medicineService.addMedicine(dto));
	}

	@Test
	void testAddDuplicateMedicineIgnoringCase() {
		MedicineRequestDto dto = medicineDto();
		medicineService.addMedicine(dto);
		MedicineRequestDto upperCase = new MedicineRequestDto(dto.medicineName().toUpperCase(), dto.price(),
				dto.availability(), dto.category());
		assertThrows(DuplicateMedicineException.class, () -> medicineService.addMedicine(upperCase));
	}

	@Test
	void testGetMedicineById() {
		MedicineResponseDto added = medicineService.addMedicine(medicineDto());
		MedicineResponseDto result = medicineService.getMedicineById(added.medicineId());
		assertEquals(added.medicineId(), result.medicineId());
		assertEquals(added.medicineName(), result.medicineName());
	}

	@Test
	void testGetMedicineByInvalidId() {
		assertThrows(MedicineNotFoundException.class, () -> medicineService.getMedicineById(99999));
	}

	@Test
	void testGetAllMedicines() {
		medicineService.addMedicine(medicineDto());
		List<MedicineResponseDto> result = medicineService.getAllMedicines();
		assertFalse(result.isEmpty());
	}

	@Test
	void testSearchMedicinesByName() {
		medicineService.addMedicine(medicineDto());
		List<MedicineResponseDto> result = medicineService.searchMedicinesByName("med");
		assertFalse(result.isEmpty());
	}

	@Test
	void testSearchMedicinesWithNoMatch() {
		medicineService.addMedicine(medicineDto());
		List<MedicineResponseDto> result = medicineService.searchMedicinesByName("zzz-no-such-medicine");
		assertTrue(result.isEmpty());
	}

	@Test
	void testUpdateMedicine() {
		MedicineResponseDto added = medicineService.addMedicine(medicineDto());
		MedicineRequestDto updateDto = new MedicineRequestDto("Upd" + System.nanoTime(), new BigDecimal("40.00"), 50,
				MedicineCategory.CAPSULE);
		MedicineResponseDto result = medicineService.updateMedicine(added.medicineId(), updateDto);
		assertEquals(added.medicineId(), result.medicineId());
		assertEquals(updateDto.medicineName(), result.medicineName());
		assertEquals(40.0, result.price().doubleValue());
		assertEquals(50, result.availability());
	}

	@Test
	void testUpdateMedicineKeepingSameName() {
		MedicineResponseDto added = medicineService.addMedicine(medicineDto());
		MedicineRequestDto updateDto = new MedicineRequestDto(added.medicineName(), new BigDecimal("60.00"), 10,
				MedicineCategory.CAPSULE);
		MedicineResponseDto result = medicineService.updateMedicine(added.medicineId(), updateDto);	
		assertEquals(60.0, result.price().doubleValue());
	}

	@Test
	void testUpdateMedicineToExistingName() {
		MedicineResponseDto first = medicineService.addMedicine(medicineDto());
		MedicineResponseDto second = medicineService.addMedicine(medicineDto());
		MedicineRequestDto updateDto = new MedicineRequestDto(first.medicineName(), new BigDecimal("30.00"), 10,
				MedicineCategory.CAPSULE);
		assertThrows(DuplicateMedicineException.class,
				() -> medicineService.updateMedicine(second.medicineId(), updateDto));
	}

	@Test
	void testUpdateInvalidMedicine() {
		assertThrows(MedicineNotFoundException.class, () -> medicineService.updateMedicine(99999, medicineDto()));
	}

	@Test
	void testDeleteMedicine() {
		int medicineId = medicineService.addMedicine(medicineDto()).medicineId();
		// reload from the database, the way a real request would
		entityManager.flush();
		entityManager.clear();
		medicineService.deleteMedicine(medicineId);
		entityManager.flush();
		entityManager.clear();
		assertThrows(MedicineNotFoundException.class, () -> medicineService.getMedicineById(medicineId));
	}

	@Test
	void testDeleteMedicineUsedInPrescription() {
		int medicineId = medicineService.addMedicine(medicineDto()).medicineId();
		int patientId = patientService.registerPatient(new PatientRequestDto("Ravi", "Kumar", Gender.values()[0],
				LocalDate.of(1995, 5, 10), "9876543210"))
				.patientId();
		int doctorId = doctorService.addDoctor(new DoctorRequestDto("Test", "Doctor", Department.Cardiology, "MBBS",
				"9999999999", new BigDecimal("500.00"))).doctorId();
		int appointmentId = appointmentService.bookAppointment(new AppointmentRequestDto(patientId, doctorId,
				LocalDate.now().plusDays(1), LocalTime.of(10, 0), "Fever")).appointmentId();
		PrescriptionItemRequestDto item = new PrescriptionItemRequestDto(medicineId, 2, 5, 3);
		prescriptionService.createPrescription(appointmentId, new PrescriptionRequestDto("Rest", List.of(item)));
		entityManager.flush();
		entityManager.clear();
		assertThrows(MedicineInUseException.class, () -> medicineService.deleteMedicine(medicineId));
	}

	@Test
	void testDeleteInvalidMedicine() {
		assertThrows(MedicineNotFoundException.class, () -> medicineService.deleteMedicine(99999));
	}
}