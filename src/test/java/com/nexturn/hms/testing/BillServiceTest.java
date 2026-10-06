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
import com.nexturn.hms.dto.BillResponseDto;
import com.nexturn.hms.dto.DoctorRequestDto;
import com.nexturn.hms.dto.MedicineRequestDto;
import com.nexturn.hms.dto.PatientRequestDto;
import com.nexturn.hms.dto.PrescriptionItemRequestDto;
import com.nexturn.hms.dto.PrescriptionRequestDto;
import com.nexturn.hms.entity.BillStatus;
import com.nexturn.hms.entity.BillType;
import com.nexturn.hms.entity.Department;
import com.nexturn.hms.entity.Gender;
import com.nexturn.hms.entity.MedicineCategory;
import com.nexturn.hms.exceptions.AppointmentNotFoundException;
import com.nexturn.hms.exceptions.BillNotFoundException;
import com.nexturn.hms.exceptions.InvalidBillException;
import com.nexturn.hms.exceptions.PatientNotFoundException;
import com.nexturn.hms.exceptions.PrescriptionNotFoundException;
import com.nexturn.hms.service.AppointmentService;
import com.nexturn.hms.service.BillService;
import com.nexturn.hms.service.DoctorService;
import com.nexturn.hms.service.MedicineService;
import com.nexturn.hms.service.PatientService;
import com.nexturn.hms.service.PrescriptionService;

@SpringBootTest
@Transactional
class BillServiceTest {

	@Autowired
	private BillService billService;
	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private PatientService patientService;
	@Autowired
	private DoctorService doctorService;
	@Autowired
	private MedicineService medicineService;
	@Autowired
	private PrescriptionService prescriptionService;

	private int createPatient() {
		PatientRequestDto dto = new PatientRequestDto("Ravi", "Kumar", Gender.values()[0], LocalDate.of(1995, 5, 10),
				"9876543210");
		return patientService.registerPatient(dto).patientId();
	}

	private int createDoctor() {
		DoctorRequestDto dto = new DoctorRequestDto("Test", "Doctor", Department.CARDIOLOGY, "MBBS", "9999999999",
				new BigDecimal("500.00"));
		return doctorService.addDoctor(dto).doctorId();
	}

	private int bookAppointment(int patientId, int doctorId) {
		AppointmentRequestDto dto = new AppointmentRequestDto(patientId, doctorId, LocalDate.now().plusDays(1),
				LocalTime.of(10, 0), "Fever");
		return appointmentService.bookAppointment(dto).appointmentId();
	}

	@Test
	void testGenerateConsultationBill() {
		int patientId = createPatient();
		int appointmentId = bookAppointment(patientId, createDoctor());
		BillResponseDto result = billService.generateConsultationBill(appointmentId);
		assertTrue(result.billId() > 0);
		assertEquals(BillType.CONSULTATION, result.billType());
		assertEquals(BillStatus.PENDING, result.status());
		assertEquals(LocalDate.now(), result.dateOfGeneration());
		assertEquals(patientId, result.patientId());
		assertEquals(500.0, result.amount().doubleValue());
	}

	@Test
	void testGenerateConsultationBillForInvalidAppointment() {
		assertThrows(AppointmentNotFoundException.class, () -> billService.generateConsultationBill(99999));
	}

	@Test
	void testGenerateConsultationBillForCancelledAppointment() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		appointmentService.cancelAppointment(appointmentId);
		assertThrows(InvalidBillException.class, () -> billService.generateConsultationBill(appointmentId));
	}

	@Test
	void testGenerateMedicineBill() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		int medicineId = medicineService.addMedicine(
				new MedicineRequestDto("Med" + System.nanoTime(), new BigDecimal("25.00"), 100, MedicineCategory.CAPSULE))
				.medicineId();
		PrescriptionItemRequestDto item = new PrescriptionItemRequestDto(medicineId, 2, 5, 3);
		int prescriptionId = prescriptionService
				.createPrescription(appointmentId, new PrescriptionRequestDto("Rest", List.of(item))).prescriptionId();
		BillResponseDto result = billService.generateMedicineBill(prescriptionId);
		assertEquals(BillType.MEDICINE, result.billType());
		assertEquals(BillStatus.PENDING, result.status());
		assertEquals(50.0, result.amount().doubleValue());
	}

	@Test
	void testGenerateMedicineBillForInvalidPrescription() {
		assertThrows(PrescriptionNotFoundException.class, () -> billService.generateMedicineBill(99999));
	}

	@Test
	void testGetBillById() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		BillResponseDto generated = billService.generateConsultationBill(appointmentId);
		BillResponseDto result = billService.getBillById(generated.billId());
		assertEquals(generated.billId(), result.billId());
	}

	@Test
	void testGetBillByInvalidId() {
		assertThrows(BillNotFoundException.class, () -> billService.getBillById(99999));
	}

	@Test
	void testGetBillsByPatient() {
		int patientId = createPatient();
		int appointmentId = bookAppointment(patientId, createDoctor());
		billService.generateConsultationBill(appointmentId);
		List<BillResponseDto> result = billService.getBillsByPatient(patientId);
		assertEquals(1, result.size());
	}

	@Test
	void testGetBillsByInvalidPatient() {
		assertThrows(PatientNotFoundException.class, () -> billService.getBillsByPatient(99999));
	}

	@Test
	void testGetAllBills() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		billService.generateConsultationBill(appointmentId);
		List<BillResponseDto> result = billService.getAllBills();
		assertFalse(result.isEmpty());
	}

	@Test
	void testUpdateStatus() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		BillResponseDto generated = billService.generateConsultationBill(appointmentId);
		BillResponseDto result = billService.updateStatus(generated.billId(), BillStatus.PAID);
		assertEquals(BillStatus.PAID, result.status());
	}

	@Test
	void testUpdateStatusOfPaidBill() {
	    int appointmentId = bookAppointment(createPatient(), createDoctor());
	    BillResponseDto generated = billService.generateConsultationBill(appointmentId);

	    billService.updateStatus(generated.billId(), BillStatus.PAID);

	    BillStatus cancelledStatus = BillStatus.CANCELLED;

	    assertThrows(
	            InvalidBillException.class,
	            () -> billService.updateStatus(generated.billId(), cancelledStatus)
	    );
	}

	@Test
	void testUpdateStatusToPending() {
		int appointmentId = bookAppointment(createPatient(), createDoctor());
		BillResponseDto generated = billService.generateConsultationBill(appointmentId);
		assertThrows(InvalidBillException.class,
				() -> billService.updateStatus(generated.billId(), BillStatus.PENDING));
	}

	@Test
	void testUpdateStatusOfInvalidBill() {
		assertThrows(BillNotFoundException.class, () -> billService.updateStatus(99999, BillStatus.PAID));
	}
}