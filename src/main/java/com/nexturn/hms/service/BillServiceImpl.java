package com.nexturn.hms.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nexturn.hms.dto.BillResponseDto;
import com.nexturn.hms.entity.Appointment;
import com.nexturn.hms.entity.AppointmentStatus;
import com.nexturn.hms.entity.Bill;
import com.nexturn.hms.entity.BillStatus;
import com.nexturn.hms.entity.BillType;
import com.nexturn.hms.entity.Patient;
import com.nexturn.hms.entity.Prescription;
import com.nexturn.hms.exceptions.AppointmentNotFoundException;
import com.nexturn.hms.exceptions.BillNotFoundException;
import com.nexturn.hms.exceptions.InvalidBillException;
import com.nexturn.hms.exceptions.PatientNotFoundException;
import com.nexturn.hms.exceptions.PrescriptionNotFoundException;
import com.nexturn.hms.repository.AppointmentRepository;
import com.nexturn.hms.repository.BillRepository;
import com.nexturn.hms.repository.PatientRepository;
import com.nexturn.hms.repository.PrescriptionRepository;

@Service
public class BillServiceImpl implements BillService {

	private final BillRepository billRepo;
	private final AppointmentRepository appointmentRepo;
	private final PrescriptionRepository prescriptionRepo;
	private final PatientRepository patientRepo;

	public BillServiceImpl(BillRepository billRepo, AppointmentRepository appointmentRepo,
			PrescriptionRepository prescriptionRepo, PatientRepository patientRepo) {
		this.billRepo = billRepo;
		this.appointmentRepo = appointmentRepo;
		this.prescriptionRepo = prescriptionRepo;
		this.patientRepo = patientRepo;
	}

	@Override
	@Transactional
	public BillResponseDto generateConsultationBill(int appointmentId) {
		Appointment appointment = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));
		if (appointment.getStatus() == AppointmentStatus.Cancelled) {
			throw new InvalidBillException("Cannot generate a bill for a cancelled appointment");
		}
		// the amount is the doctor's consultation fee
		BigDecimal amount = appointment.getDoctor().getConsultationFee();
		return toResponse(billRepo.save(newBill(BillType.CONSULTATION, amount, appointment.getPatient())));
	}

	@Override
	@Transactional
	public BillResponseDto generateMedicineBill(int prescriptionId) {
		Prescription prescription = prescriptionRepo.findById(prescriptionId)
				.orElseThrow(() -> new PrescriptionNotFoundException(
						"Prescription not found with id: " + prescriptionId));
		// the amount is the sum of (medicine price x quantity) over all prescription items
		BigDecimal amount = prescription.getItems().stream()
				.map(i -> i.getMedicine().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		return toResponse(billRepo.save(
				newBill(BillType.MEDICINE, amount, prescription.getAppointment().getPatient())));
	}

	@Override
	@Transactional(readOnly = true)
	public BillResponseDto getBillById(int billId) {
		return toResponse(findBill(billId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<BillResponseDto> getBillsByPatient(int patientId) {
		if (!patientRepo.existsById(patientId)) {
			throw new PatientNotFoundException("Patient not found with id: " + patientId);
		}
		return billRepo.findByPatientPatientIdOrderByDateOfGenerationDesc(patientId).stream()
				.map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BillResponseDto> getAllBills() {
		return billRepo.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional
	public BillResponseDto updateStatus(int billId, BillStatus status) {
		Bill bill = findBill(billId);
		// only a PENDING bill can be marked PAID or CANCELLED
		if (bill.getStatus() != BillStatus.PENDING) {
			throw new InvalidBillException("Bill status cannot be changed because it is already " + bill.getStatus());
		}
		if (status == BillStatus.PENDING) {
			throw new InvalidBillException("Bill is already pending");
		}
		bill.setStatus(status);
		return toResponse(billRepo.save(bill));
	}

	private Bill findBill(int billId) {
		return billRepo.findById(billId)
				.orElseThrow(() -> new BillNotFoundException("Bill not found with id: " + billId));
	}

	private Bill newBill(BillType type, BigDecimal amount, Patient patient) {
		Bill bill = new Bill();
		bill.setBillType(type);
		bill.setAmount(amount);
		bill.setStatus(BillStatus.PENDING);
		bill.setDateOfGeneration(LocalDate.now());
		bill.setPatient(patient);
		return bill;
	}

	private BillResponseDto toResponse(Bill b) {
		Patient p = b.getPatient();
		return new BillResponseDto(b.getBillId(), b.getBillType(), b.getAmount(), b.getStatus(),
				b.getDateOfGeneration(), p.getPatientId(), p.getFirstName() + " " + p.getLastName());
	}
}