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
import com.nexturn.hms.dto.AppointmentResponseDto;
import com.nexturn.hms.dto.DoctorRequestDto;
import com.nexturn.hms.dto.PatientRequestDto;
import com.nexturn.hms.dto.RescheduleRequestDto;
import com.nexturn.hms.entity.AppointmentStatus;
import com.nexturn.hms.entity.Department;
import com.nexturn.hms.entity.Gender;
import com.nexturn.hms.exceptions.AppointmentNotFoundException;
import com.nexturn.hms.exceptions.DoctorNotFoundException;
import com.nexturn.hms.exceptions.InvalidAppointmentException;
import com.nexturn.hms.exceptions.PatientNotFoundException;
import com.nexturn.hms.exceptions.SlotNotAvailableException;
import com.nexturn.hms.service.AppointmentService;
import com.nexturn.hms.service.DoctorService;
import com.nexturn.hms.service.PatientService;

@SpringBootTest
@Transactional
class AppointmentServiceTest {

	@Autowired
	AppointmentService appointmentService;
	@Autowired
	PatientService patientService;
	@Autowired
	DoctorService doctorService;

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

	private AppointmentResponseDto book(int patientId, int doctorId, LocalTime time) {
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		return appointmentService.bookAppointment(new AppointmentRequestDto(patientId, doctorId, tomorrow, time, "Fever"));
	}

	@Test
	void testBookAppointment() {
		int patientId = createPatient();
		int doctorId = createDoctor();
		AppointmentResponseDto result = book(patientId, doctorId, LocalTime.of(10, 0));
		assertTrue(result.appointmentId() > 0);
		assertEquals(AppointmentStatus.SCHEDULED, result.status());
		assertEquals(patientId, result.patientId());
		assertEquals(doctorId, result.doctorId());
		assertEquals(LocalTime.of(10, 0), result.startTime());
		assertEquals(LocalTime.of(10, 30), result.endTime());
	}

	@Test
	void testBookAppointmentWithInvalidPatient() {
		int doctorId = createDoctor();
		assertThrows(PatientNotFoundException.class, () -> book(99999, doctorId, LocalTime.of(10, 0)));
	}

	@Test
	void testBookAppointmentWithInvalidDoctor() {
		int patientId = createPatient();
		assertThrows(DoctorNotFoundException.class, () -> book(patientId, 99999, LocalTime.of(10, 0)));
	}

	@Test
	void testBookAppointmentInThePast() {
		int patientId = createPatient();
		int doctorId = createDoctor();
		LocalDate yesterday = LocalDate.now().minusDays(1);
		AppointmentRequestDto dto = new AppointmentRequestDto(patientId, doctorId, yesterday, LocalTime.of(10, 0), "Fever");
		assertThrows(InvalidAppointmentException.class, () -> appointmentService.bookAppointment(dto));
	}

	@Test
	void testBookAppointmentOverlappingSlot() {
		int patientId = createPatient();
		int doctorId = createDoctor();
		book(patientId, doctorId, LocalTime.of(10, 0));
		assertThrows(SlotNotAvailableException.class, () -> book(patientId, doctorId, LocalTime.of(10, 15)));
	}

	@Test
	void testBookAppointmentInNextFreeSlot() {
		int patientId = createPatient();
		int doctorId = createDoctor();
		book(patientId, doctorId, LocalTime.of(10, 0));
		AppointmentResponseDto result = book(patientId, doctorId, LocalTime.of(10, 30));
		assertEquals(LocalTime.of(10, 30), result.startTime());
	}

	@Test
	void testGetAppointmentById() {
		AppointmentResponseDto booked = book(createPatient(), createDoctor(), LocalTime.of(10, 0));
		AppointmentResponseDto result = appointmentService.getAppointmentById(booked.appointmentId());
		assertEquals(booked.appointmentId(), result.appointmentId());
	}

	@Test
	void testGetAppointmentByInvalidId() {
		assertThrows(AppointmentNotFoundException.class, () -> appointmentService.getAppointmentById(99999));
	}

	@Test
	void testCancelAppointment() {
		AppointmentResponseDto booked = book(createPatient(), createDoctor(), LocalTime.of(10, 0));
		AppointmentResponseDto result = appointmentService.cancelAppointment(booked.appointmentId());
		assertEquals(AppointmentStatus.CANCELLED, result.status());
	}

	@Test
	void testCancelAlreadyCancelledAppointment() {
		AppointmentResponseDto booked = book(createPatient(), createDoctor(), LocalTime.of(10, 0));
		appointmentService.cancelAppointment(booked.appointmentId());
		assertThrows(InvalidAppointmentException.class,
				() -> appointmentService.cancelAppointment(booked.appointmentId()));
	}

	@Test
	void testCancelledSlotCanBeBookedAgain() {
		int patientId = createPatient();
		int doctorId = createDoctor();
		AppointmentResponseDto booked = book(patientId, doctorId, LocalTime.of(10, 0));
		appointmentService.cancelAppointment(booked.appointmentId());
		AppointmentResponseDto result = book(patientId, doctorId, LocalTime.of(10, 0));
		assertEquals(AppointmentStatus.SCHEDULED, result.status());
	}

	@Test
	void testRescheduleAppointment() {
		AppointmentResponseDto booked = book(createPatient(), createDoctor(), LocalTime.of(10, 0));
		LocalDate newDate = LocalDate.now().plusDays(3);
		AppointmentResponseDto result = appointmentService.rescheduleAppointment(booked.appointmentId(),
				new RescheduleRequestDto(newDate, LocalTime.of(11, 0)));
		assertEquals(newDate, result.date());
		assertEquals(LocalTime.of(11, 0), result.startTime());
		assertEquals(LocalTime.of(11, 30), result.endTime());
	}

	@Test
	void testRescheduleOverlappingOwnOldSlot() {
		AppointmentResponseDto booked = book(createPatient(), createDoctor(), LocalTime.of(10, 0));
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		AppointmentResponseDto result = appointmentService.rescheduleAppointment(booked.appointmentId(),
				new RescheduleRequestDto(tomorrow, LocalTime.of(10, 15)));

		assertEquals(LocalTime.of(10, 15), result.startTime());
	}

	@Test
	void testRescheduleIntoBookedSlot() {
	    int patientId = createPatient();
	    int doctorId = createDoctor();

	    book(patientId, doctorId, LocalTime.of(10, 0));
	    AppointmentResponseDto second = book(patientId, doctorId, LocalTime.of(11, 0));
	    LocalDate tomorrow = LocalDate.now().plusDays(1);
	    int appointmentId = second.appointmentId();
	    RescheduleRequestDto request =
	            new RescheduleRequestDto(tomorrow, LocalTime.of(10, 0));
	    assertThrows(
	            SlotNotAvailableException.class,
	            () -> appointmentService.rescheduleAppointment(appointmentId, request)
	    );
	}

	@Test
	void testRescheduleCancelledAppointment() {
		AppointmentResponseDto booked = book(createPatient(), createDoctor(), LocalTime.of(10, 0));
		appointmentService.cancelAppointment(booked.appointmentId());
		LocalDate tomorrow = LocalDate.now().plusDays(1);

		assertThrows(InvalidAppointmentException.class, () -> appointmentService
				.rescheduleAppointment(booked.appointmentId(), new RescheduleRequestDto(tomorrow, LocalTime.of(12, 0))));
	}

	@Test
	void testUpdateStatus() {
		AppointmentResponseDto booked = book(createPatient(), createDoctor(), LocalTime.of(10, 0));
		AppointmentResponseDto result = appointmentService.updateStatus(booked.appointmentId(),
				AppointmentStatus.COMPLETED);
		assertEquals(AppointmentStatus.COMPLETED, result.status());
	}

	@Test
	void testUpdateStatusOfCompletedAppointment() {
		AppointmentResponseDto booked = book(createPatient(), createDoctor(), LocalTime.of(10, 0));
		appointmentService.updateStatus(booked.appointmentId(), AppointmentStatus.COMPLETED);
		assertThrows(InvalidAppointmentException.class,
				() -> appointmentService.updateStatus(booked.appointmentId(), AppointmentStatus.CANCELLED));
	}

	@Test
	void testGetAppointmentsByPatient() {
		int patientId = createPatient();
		book(patientId, createDoctor(), LocalTime.of(10, 0));
		List<AppointmentResponseDto> result = appointmentService.getAppointmentsByPatient(patientId);
		assertEquals(1, result.size());
	}

	@Test
	void testGetAppointmentsByInvalidPatient() {
		assertThrows(PatientNotFoundException.class, () -> appointmentService.getAppointmentsByPatient(99999));
	}

	@Test
	void testGetAppointmentsByDoctor() {
		int doctorId = createDoctor();
		book(createPatient(), doctorId, LocalTime.of(10, 0));
		List<AppointmentResponseDto> result = appointmentService.getAppointmentsByDoctor(doctorId);
		assertEquals(1, result.size());
	}

	@Test
	void testGetAppointmentsByInvalidDoctor() {
		assertThrows(DoctorNotFoundException.class, () -> appointmentService.getAppointmentsByDoctor(99999));
	}

	@Test
	void testGetAppointmentsByDate() {
		book(createPatient(), createDoctor(), LocalTime.of(10, 0));
		List<AppointmentResponseDto> result = appointmentService.getAppointmentsByDate(LocalDate.now().plusDays(1));
		assertFalse(result.isEmpty());
	}
}