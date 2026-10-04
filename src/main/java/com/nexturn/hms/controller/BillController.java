package com.nexturn.hms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexturn.hms.dto.BillResponseDto;
import com.nexturn.hms.entity.BillStatus;
import com.nexturn.hms.service.BillService;

@RestController
@RequestMapping("/api/bills")
public class BillController {

	private final BillService billService;

	public BillController(BillService billService) {
		this.billService = billService;
	}

	@PostMapping("/consultation/appointment/{appointmentId}")
	public ResponseEntity<BillResponseDto> generateConsultationBill(@PathVariable int appointmentId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(billService.generateConsultationBill(appointmentId));
	}

	@PostMapping("/medicine/prescription/{prescriptionId}")
	public ResponseEntity<BillResponseDto> generateMedicineBill(@PathVariable int prescriptionId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(billService.generateMedicineBill(prescriptionId));
	}

	@GetMapping("/{billId}")
	public ResponseEntity<BillResponseDto> getBillById(@PathVariable int billId) {
		return ResponseEntity.ok(billService.getBillById(billId));
	}

	@GetMapping("/patient/{patientId}")
	public ResponseEntity<List<BillResponseDto>> getBillsByPatient(@PathVariable int patientId) {
		return ResponseEntity.ok(billService.getBillsByPatient(patientId));
	}

	@GetMapping
	public ResponseEntity<List<BillResponseDto>> getAllBills() {
		return ResponseEntity.ok(billService.getAllBills());
	}

	// example: PATCH /api/bills/5/status?status=PAID
	@PatchMapping("/{billId}/status")
	public ResponseEntity<BillResponseDto> updateStatus(@PathVariable int billId,
			@RequestParam BillStatus status) {
		return ResponseEntity.ok(billService.updateStatus(billId, status));
	}
}