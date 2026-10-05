package com.nexturn.hms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexturn.hms.dto.PrescriptionItemRequestDto;
import com.nexturn.hms.dto.PrescriptionItemResponseDto;
import com.nexturn.hms.dto.PrescriptionRequestDto;
import com.nexturn.hms.dto.PrescriptionResponseDto;
import com.nexturn.hms.entity.Appointment;
import com.nexturn.hms.entity.AppointmentStatus;
import com.nexturn.hms.entity.Medicine;
import com.nexturn.hms.entity.Prescription;
import com.nexturn.hms.entity.PrescriptionItems;
import com.nexturn.hms.exceptions.AppointmentNotFoundException;
import com.nexturn.hms.exceptions.InvalidPrescriptionException;
import com.nexturn.hms.exceptions.MedicineNotFoundException;
import com.nexturn.hms.exceptions.PatientNotFoundException;
import com.nexturn.hms.exceptions.PrescriptionAlreadyExistsException;
import com.nexturn.hms.exceptions.PrescriptionNotFoundException;
import com.nexturn.hms.repository.AppointmentRepository;
import com.nexturn.hms.repository.MedicineRepository;
import com.nexturn.hms.repository.PatientRepository;
import com.nexturn.hms.repository.PrescriptionRepository;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

	@Autowired
	PrescriptionRepository prescriptionRepo;
	AppointmentRepository appointmentRepo;
	MedicineRepository medicineRepo;
	PatientRepository patientRepo;

	@Override
	@Transactional
	public PrescriptionResponseDto createPrescription(int appointmentId, PrescriptionRequestDto dto) {
		Appointment appointment = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));

		if (appointment.getStatus() == AppointmentStatus.Cancelled) {
			throw new InvalidPrescriptionException("Cannot create a prescription for a cancelled appointment");
		}
		// one prescription per appointment
		if (prescriptionRepo.existsByAppointmentAppointmentId(appointmentId)) {
			throw new PrescriptionAlreadyExistsException(
					"A prescription already exists for appointment id: " + appointmentId);
		}

		Prescription prescription = new Prescription();
		prescription.setAppointment(appointment);
		prescription.setNotes(dto.notes());
		prescription.setItems(new ArrayList<>());
		addItems(prescription, dto.items());

		// the items are saved together with the prescription (cascade = ALL)
		return toResponse(prescriptionRepo.save(prescription));
	}

	@Override
	@Transactional(readOnly = true)
	public PrescriptionResponseDto getPrescriptionById(int prescriptionId) {
		return toResponse(findPrescription(prescriptionId));
	}

	@Override
	@Transactional(readOnly = true)
	public PrescriptionResponseDto getPrescriptionByAppointment(int appointmentId) {
		Prescription prescription = prescriptionRepo.findByAppointmentAppointmentId(appointmentId)
				.orElseThrow(() -> new PrescriptionNotFoundException(
						"No prescription found for appointment id: " + appointmentId));
		return toResponse(prescription);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PrescriptionResponseDto> getPrescriptionsByPatient(int patientId) {
		if (!patientRepo.existsById(patientId)) {
			throw new PatientNotFoundException("Patient not found with id: " + patientId);
		}
		return prescriptionRepo.findByAppointmentPatientPatientIdOrderByAppointmentDateDesc(patientId).stream()
				.map(this::toResponse).toList();
	}

	@Override
	@Transactional
	public PrescriptionResponseDto updatePrescription(int prescriptionId, PrescriptionRequestDto dto) {
		Prescription prescription = findPrescription(prescriptionId);
		prescription.setNotes(dto.notes());

		// clear and refill the SAME list: orphanRemoval then deletes the old items from the database
		prescription.getItems().clear();
		addItems(prescription, dto.items());

		return toResponse(prescriptionRepo.save(prescription));
	}

	@Override
	@Transactional
	public void deletePrescription(int prescriptionId) {
		// the items are removed along with it (cascade = ALL)
		prescriptionRepo.delete(findPrescription(prescriptionId));
	}

	private Prescription findPrescription(int prescriptionId) {
		return prescriptionRepo.findById(prescriptionId)
				.orElseThrow(() -> new PrescriptionNotFoundException(
						"Prescription not found with id: " + prescriptionId));
	}

	private void addItems(Prescription prescription, List<PrescriptionItemRequestDto> itemDtos) {
		for (PrescriptionItemRequestDto itemDto : itemDtos) {
			Medicine medicine = medicineRepo.findById(itemDto.medicineId())
					.orElseThrow(() -> new MedicineNotFoundException(
							"Medicine not found with id: " + itemDto.medicineId()));
			if (medicine.getAvailability() <= 0) {
				throw new InvalidPrescriptionException(medicine.getMedicineName() + " is out of stock");
			}
			PrescriptionItems item = new PrescriptionItems();
			item.setPrescription(prescription);
			item.setMedicine(medicine);
			item.setQuantity(itemDto.quantity());
			item.setDuration(itemDto.duration());
			item.setFrequency(itemDto.frequency());
			prescription.getItems().add(item);
		}
	}

	private PrescriptionResponseDto toResponse(Prescription p) {
		Appointment a = p.getAppointment();
		List<PrescriptionItemResponseDto> items = p.getItems().stream()
				.map(i -> new PrescriptionItemResponseDto(i.getPrescriptionItemId(),
						i.getMedicine().getMedicineId(), i.getMedicine().getMedicineName(),
						i.getQuantity(), i.getDuration(), i.getFrequency()))
				.toList();
		return new PrescriptionResponseDto(p.getPrescriptionId(), a.getAppointmentId(),
				a.getPatient().getFirstName() + " " + a.getPatient().getLastName(),
				a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName(),
				p.getNotes(), items);
	}
}