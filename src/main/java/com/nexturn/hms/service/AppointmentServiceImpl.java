package com.nexturn.hms.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexturn.hms.dto.AppointmentRequestDto;
import com.nexturn.hms.dto.AppointmentResponseDto;
import com.nexturn.hms.dto.RescheduleRequestDto;
import com.nexturn.hms.entity.Appointment;
import com.nexturn.hms.entity.AppointmentStatus;
import com.nexturn.hms.entity.Doctor;
import com.nexturn.hms.entity.Patient;
import com.nexturn.hms.exceptions.AppointmentNotFoundException;
import com.nexturn.hms.exceptions.DoctorNotFoundException;
import com.nexturn.hms.exceptions.InvalidAppointmentException;
import com.nexturn.hms.exceptions.PatientNotFoundException;
import com.nexturn.hms.exceptions.SlotNotAvailableException;
import com.nexturn.hms.repository.AppointmentRepository;
import com.nexturn.hms.repository.DoctorRepository;
import com.nexturn.hms.repository.PatientRepository;

@Service
public class AppointmentServiceImpl implements AppointmentService {

	// every appointment is a fixed-length slot; the patient only chooses the start time
	private static final int SLOT_MINUTES = 30;

	// working hours: 10:00 to 16:00, with a lunch break from 13:00 to 14:00
	private static final LocalTime OPENING_TIME = LocalTime.of(10, 0);
	private static final LocalTime CLOSING_TIME = LocalTime.of(16, 0);
	private static final LocalTime LUNCH_START = LocalTime.of(13, 0);
	private static final LocalTime LUNCH_END = LocalTime.of(14, 0);

	private final AppointmentRepository appointmentRepo;
	private final PatientRepository patientRepo;
	private final DoctorRepository doctorRepo;

	public AppointmentServiceImpl(AppointmentRepository appointmentRepo, PatientRepository patientRepo,
			DoctorRepository doctorRepo) {
		this.appointmentRepo = appointmentRepo;
		this.patientRepo = patientRepo;
		this.doctorRepo = doctorRepo;
	}

	@Override
	@Transactional
	public AppointmentResponseDto bookAppointment(AppointmentRequestDto dto) {
		Patient patient = patientRepo.findById(dto.patientId())
				.orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + dto.patientId()));
		Doctor doctor = doctorRepo.findById(dto.doctorId())
				.orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + dto.doctorId()));

		LocalTime endTime = validateAndGetEndTime(dto.date(), dto.startTime());
		checkPatientHasNoOtherAppointment(patient.getPatientId(), doctor.getDoctorId(), dto.date());
		checkSlotFree(doctor.getDoctorId(), dto.date(), dto.startTime(), endTime, 0);

		Appointment appointment = new Appointment();
		appointment.setPatient(patient);
		appointment.setDoctor(doctor);
		appointment.setDate(dto.date());
		appointment.setStartTime(dto.startTime());
		appointment.setEndTime(endTime);
		appointment.setDisease(dto.disease());
		appointment.setStatus(AppointmentStatus.SCHEDULED);

		return toResponse(appointmentRepo.save(appointment));
	}

	@Override
	@Transactional
	public AppointmentResponseDto cancelAppointment(int appointmentId) {
		Appointment appointment = findAppointment(appointmentId);
		requireBooked(appointment, "cancelled");
		// the row is kept with status Cancelled so the history is not lost
		appointment.setStatus(AppointmentStatus.CANCELLED);
		return toResponse(appointmentRepo.save(appointment));
	}

	@Override
	@Transactional
	public AppointmentResponseDto rescheduleAppointment(int appointmentId, RescheduleRequestDto dto) {
		Appointment appointment = findAppointment(appointmentId);
		requireBooked(appointment, "rescheduled");

		LocalTime endTime = validateAndGetEndTime(dto.date(), dto.startTime());
		// the appointment's own id is excluded so it does not clash with its old slot
		checkSlotFree(appointment.getDoctor().getDoctorId(), dto.date(), dto.startTime(), endTime, appointmentId);

		appointment.setDate(dto.date());
		appointment.setStartTime(dto.startTime());
		appointment.setEndTime(endTime);
		return toResponse(appointmentRepo.save(appointment));
	}

	@Override
	@Transactional(readOnly = true)
	public AppointmentResponseDto getAppointmentById(int appointmentId) {
		return toResponse(findAppointment(appointmentId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppointmentResponseDto> getAppointmentsByPatient(int patientId) {
		if (!patientRepo.existsById(patientId)) {
			throw new PatientNotFoundException("Patient not found with id: " + patientId);
		}
		return appointmentRepo.findByPatientPatientIdOrderByDateDescStartTimeDesc(patientId).stream()
				.map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppointmentResponseDto> getAppointmentsByDoctor(int doctorId) {
		if (!doctorRepo.existsById(doctorId)) {
			throw new DoctorNotFoundException("Doctor not found with id: " + doctorId);
		}
		return appointmentRepo.findByDoctorDoctorIdOrderByDateAscStartTimeAsc(doctorId).stream()
				.map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppointmentResponseDto> getAppointmentsByDate(LocalDate date) {
		return appointmentRepo.findByDateOrderByStartTimeAsc(date).stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<LocalTime> getAvailableSlots(int doctorId, LocalDate date) {
		if (!doctorRepo.existsById(doctorId)) {
			throw new DoctorNotFoundException("Doctor not found with id: " + doctorId);
		}
		List<Appointment> taken = appointmentRepo.findByDoctorDoctorIdAndDateAndStatusNot(doctorId, date,
				AppointmentStatus.CANCELLED);

		List<LocalTime> free = new ArrayList<>();
		LocalTime start = OPENING_TIME;
		while (!start.plusMinutes(SLOT_MINUTES).isAfter(CLOSING_TIME)) {
			LocalTime end = start.plusMinutes(SLOT_MINUTES);

			boolean duringLunch = start.isBefore(LUNCH_END) && end.isAfter(LUNCH_START);
			boolean inThePast = date.isBefore(LocalDate.now())
					|| (date.isEqual(LocalDate.now()) && start.isBefore(LocalTime.now()));
			boolean alreadyBooked = false;
			for (Appointment a : taken) {
				if (a.getStartTime().isBefore(end) && a.getEndTime().isAfter(start)) {
					alreadyBooked = true;
				}
			}

			if (!duringLunch && !inThePast && !alreadyBooked) {
				free.add(start);
			}
			start = end;
		}
		return free;
	}

	@Override
	@Transactional
	public AppointmentResponseDto updateStatus(int appointmentId, AppointmentStatus status) {
		Appointment appointment = findAppointment(appointmentId);
		requireBooked(appointment, "updated");
		appointment.setStatus(status);
		return toResponse(appointmentRepo.save(appointment));
	}

	private Appointment findAppointment(int appointmentId) {
		return appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));
	}

	// only an appointment that is still Scheduled can be cancelled, rescheduled or updated
	private void requireBooked(Appointment appointment, String action) {
		if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
			throw new InvalidAppointmentException(
					"Appointment cannot be " + action + " because its status is " + appointment.getStatus());
		}
	}

	private LocalTime validateAndGetEndTime(LocalDate date, LocalTime startTime) {
		if (date.isBefore(LocalDate.now())
				|| (date.isEqual(LocalDate.now()) && startTime.isBefore(LocalTime.now()))) {
			throw new InvalidAppointmentException("Appointment time cannot be in the past");
		}
		LocalTime endTime = startTime.plusMinutes(SLOT_MINUTES);
		// a late start (e.g. 23:45) would wrap past midnight and give an end time before the start
		if (!endTime.isAfter(startTime)) {
			throw new InvalidAppointmentException("Appointment must start earlier in the day");
		}

		boolean onSlot = startTime.getMinute() % SLOT_MINUTES == 0 && startTime.getSecond() == 0;
		boolean insideWorkingHours = !startTime.isBefore(OPENING_TIME) && !endTime.isAfter(CLOSING_TIME);
		boolean duringLunch = startTime.isBefore(LUNCH_END) && endTime.isAfter(LUNCH_START);
		if (!onSlot || !insideWorkingHours || duringLunch) {
			throw new InvalidAppointmentException(
					"Appointments are available from 10:00 to 16:00 in 30-minute slots, except the lunch break from 13:00 to 14:00");
		}
		return endTime;
	}

	// a patient can have only one open appointment with the same doctor, and only one per day
	private void checkPatientHasNoOtherAppointment(int patientId, int doctorId, LocalDate date) {
		if (appointmentRepo.existsByPatientPatientIdAndDoctorDoctorIdAndStatus(patientId, doctorId,
				AppointmentStatus.SCHEDULED)) {
			throw new InvalidAppointmentException(
					"This patient already has a scheduled appointment with this doctor. A new one can be booked after it is completed or cancelled");
		}
		if (appointmentRepo.existsByPatientPatientIdAndDoctorDoctorIdAndDateAndStatusNot(patientId, doctorId, date,
				AppointmentStatus.CANCELLED)) {
			throw new InvalidAppointmentException("This patient already has an appointment with this doctor on this day");
		}
	}

	private void checkSlotFree(int doctorId, LocalDate date, LocalTime start, LocalTime end, int excludeId) {
		if (appointmentRepo.existsOverlap(doctorId, date, start, end, AppointmentStatus.CANCELLED, excludeId)) {
			throw new SlotNotAvailableException("The doctor already has an appointment at this time");
		}
	}

	private AppointmentResponseDto toResponse(Appointment a) {
		Patient p = a.getPatient();
		Doctor d = a.getDoctor();
		return new AppointmentResponseDto(a.getAppointmentId(), a.getDate(), a.getStartTime(), a.getEndTime(),
				a.getDisease(), a.getStatus(), p.getPatientId(), p.getFirstName() + " " + p.getLastName(),
				d.getDoctorId(), d.getFirstName() + " " + d.getLastName());
	}
}