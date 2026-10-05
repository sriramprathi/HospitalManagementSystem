package com.nexturn.hms.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.nexturn.hms.dto.PatientRequestDto;
import com.nexturn.hms.dto.PrescriptionItemRequestDto;
import com.nexturn.hms.dto.PrescriptionRequestDto;
import com.nexturn.hms.dto.PrescriptionResponseDto;
import com.nexturn.hms.entity.Department;
import com.nexturn.hms.entity.Gender;
import com.nexturn.hms.entity.MedicineCategory;
import com.nexturn.hms.exceptions.AppointmentNotFoundException;
import com.nexturn.hms.exceptions.InvalidPrescriptionException;
import com.nexturn.hms.exceptions.MedicineNotFoundException;
import com.nexturn.hms.exceptions.PatientNotFoundException;
import com.nexturn.hms.exceptions.PrescriptionAlreadyExistsException;
import com.nexturn.hms.exceptions.PrescriptionNotFoundException;
import com.nexturn.hms.service.AppointmentService;
import com.nexturn.hms.service.DoctorService;
import com.nexturn.hms.service.MedicineService;
import com.nexturn.hms.service.PatientService;
import com.nexturn.hms.service.PrescriptionService;

@SpringBootTest
@Transactional
class PrescriptionServiceTest{

	@Autowired
	private PrescriptionService prescriptionService;
	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private PatientService patientService;
	@Autowired
	private DoctorService doctorService;
	@Autowired
	private MedicineService medicineService;

	private int createPatient() {
		PatientRequestDto dto = new PatientRequestDto("Ravi", "Kumar", Gender.values()[0], LocalDate.of(1995, 5, 10),
				"9876543210");
		return patientService.registerPatient(dto).patientId();
	}

	private int createDoctor() {
		DoctorRequestDto dto = new DoctorRequestDto("Test", "Doctor", Department.Cardiology, "MBBS", "9999999999",
				new BigDecimal("500.00"));
		return doctorService.addDoctor(dto).doctorId();
	}

	private int createMedicine(int availability) {
		MedicineRequestDto dto = new MedicineRequestDto("Med" + System.nanoTime(), new BigDecimal("25.00"), availability,
				MedicineCategory.CAPSULE);
		return medicineService.addMedicine(dto).medicineId();
	}

	private int bookAppointment(int patientId, int doctorId) {
		AppointmentRequestDto dto = new AppointmentRequestDto(patientId, doctorId, LocalDate.now().plusDays(1),
				LocalTime.of(10, 0), "Fever");
		return appointmentService.bookAppointment(dto).appointmentId();
	}

	private PrescriptionRequestDto prescriptionDto(int medicineId) {
		PrescriptionItemRequestDto item = new PrescriptionItemRequestDto(medicineId, 2, 5, 3);
		return new PrescriptionRequestDto("Rest and fluids", List.of(item));
	}

	@Test
	void testCreatePrescription() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		int medicineId = createMedicine(100);
		PrescriptionResponseDto result = prescriptionService.createPrescription(appointmentId,
				prescriptionDto(medicineId));
		assertTrue(result.prescriptionId() > 0);
		assertEquals(appointmentId, result.appointmentId());
		assertEquals("Rest and fluids", result.notes());
		assertEquals("Ravi Kumar", result.patientName());
		assertEquals(1, result.items().size());
		assertEquals(medicineId, result.items().get(0).medicineId());
		assertEquals(2, result.items().get(0).quantity());
	}

	@Test
	void testCreatePrescriptionForInvalidAppointment() {
		int medicineId = createMedicine(100);
		assertThrows(AppointmentNotFoundException.class,
				() -> prescriptionService.createPrescription(99999, prescriptionDto(medicineId)));
	}

	@Test
	void testCreateSecondPrescriptionForSameAppointment() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		int medicineId = createMedicine(100);
		prescriptionService.createPrescription(appointmentId, prescriptionDto(medicineId));
		assertThrows(PrescriptionAlreadyExistsException.class,
				() -> prescriptionService.createPrescription(appointmentId, prescriptionDto(medicineId)));
	}

	@Test
	void testCreatePrescriptionForCancelledAppointment() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		int medicineId = createMedicine(100);
		appointmentService.cancelAppointment(appointmentId);
		assertThrows(InvalidPrescriptionException.class,
				() -> prescriptionService.createPrescription(appointmentId, prescriptionDto(medicineId)));
	}

	@Test
	void testCreatePrescriptionWithUnknownMedicine() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		assertThrows(MedicineNotFoundException.class,
				() -> prescriptionService.createPrescription(appointmentId, prescriptionDto(99999)));
	}

	@Test
	void testCreatePrescriptionWithOutOfStockMedicine() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		int outOfStockId = createMedicine(0);
		assertThrows(InvalidPrescriptionException.class,
				() -> prescriptionService.createPrescription(appointmentId, prescriptionDto(outOfStockId)));
	}

	@Test
	void testGetPrescriptionById() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		int prescriptionId = prescriptionService.createPrescription(appointmentId, prescriptionDto(createMedicine(100)))
				.prescriptionId();
		PrescriptionResponseDto result = prescriptionService.getPrescriptionById(prescriptionId);
		assertEquals(prescriptionId, result.prescriptionId());
	}

	@Test
	void testGetPrescriptionByInvalidId() {
		assertThrows(PrescriptionNotFoundException.class, () -> prescriptionService.getPrescriptionById(99999));
	}

	@Test
	void testGetPrescriptionByAppointment() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		int prescriptionId = prescriptionService.createPrescription(appointmentId, prescriptionDto(createMedicine(100)))
				.prescriptionId();
		PrescriptionResponseDto result = prescriptionService.getPrescriptionByAppointment(appointmentId);
		assertEquals(prescriptionId, result.prescriptionId());
	}

	@Test
	void testGetPrescriptionByAppointmentWithNoPrescription() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		assertThrows(PrescriptionNotFoundException.class,
				() -> prescriptionService.getPrescriptionByAppointment(appointmentId));
	}

	@Test
	void testGetPrescriptionsByPatient() {
		int patientId = createPatient();
		int appointmentId = bookAppointment(patientId, createDoctor());
		prescriptionService.createPrescription(appointmentId, prescriptionDto(createMedicine(100)));
		List<PrescriptionResponseDto> result = prescriptionService.getPrescriptionsByPatient(patientId);
		assertEquals(1, result.size());
	}

	@Test
	void testGetPrescriptionsByInvalidPatient() {
		assertThrows(PatientNotFoundException.class, () -> prescriptionService.getPrescriptionsByPatient(99999));
	}

	@Test
	void testUpdatePrescription() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		int firstMedicineId = createMedicine(100);
		int secondMedicineId = createMedicine(100);
		int prescriptionId = prescriptionService.createPrescription(appointmentId, prescriptionDto(firstMedicineId))
				.prescriptionId();
		List<PrescriptionItemRequestDto> items = List.of(new PrescriptionItemRequestDto(firstMedicineId, 2, 5, 3),
				new PrescriptionItemRequestDto(secondMedicineId, 1, 7, 2));
		PrescriptionResponseDto result = prescriptionService.updatePrescription(prescriptionId,
				new PrescriptionRequestDto("Antibiotics for 7 days", items));
		assertEquals(prescriptionId, result.prescriptionId());
		assertEquals("Antibiotics for 7 days", result.notes());
		assertEquals(2, result.items().size());
	}

	@Test
	void testUpdateInvalidPrescription() {
		int medicineId = createMedicine(100);
		assertThrows(PrescriptionNotFoundException.class,
				() -> prescriptionService.updatePrescription(99999, prescriptionDto(medicineId)));
	}

	@Test
	void testDeletePrescription() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		int prescriptionId = prescriptionService.createPrescription(appointmentId, prescriptionDto(createMedicine(100)))
				.prescriptionId();
		prescriptionService.deletePrescription(prescriptionId);
		assertThrows(PrescriptionNotFoundException.class, () -> prescriptionService.getPrescriptionById(prescriptionId));
	}

	@Test
	void testDeleteInvalidPrescription() {
		assertThrows(PrescriptionNotFoundException.class, () -> prescriptionService.deletePrescription(99999));
	}
}