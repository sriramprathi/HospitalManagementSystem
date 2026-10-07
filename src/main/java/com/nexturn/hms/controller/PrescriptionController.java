package com.nexturn.hms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexturn.hms.dto.PrescriptionRequestDto;
import com.nexturn.hms.dto.PrescriptionResponseDto;
import com.nexturn.hms.service.PrescriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class PrescriptionController {
	
	@Autowired
	PrescriptionService prescriptionService;


	@PostMapping("/appointment/{appointmentId}")
	public ResponseEntity<PrescriptionResponseDto> createPrescription(@PathVariable int appointmentId,
			@Valid @RequestBody PrescriptionRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(prescriptionService.createPrescription(appointmentId, dto));
	}

	@GetMapping("/{prescriptionId}")
	public ResponseEntity<PrescriptionResponseDto> getPrescriptionById(@PathVariable int prescriptionId) {
		return ResponseEntity.ok(prescriptionService.getPrescriptionById(prescriptionId));
	}

	@GetMapping("/appointment/{appointmentId}")
	public ResponseEntity<PrescriptionResponseDto> getPrescriptionByAppointment(@PathVariable int appointmentId) {
		return ResponseEntity.ok(prescriptionService.getPrescriptionByAppointment(appointmentId));
	}

	@GetMapping("/patient/{patientId}")
	public ResponseEntity<List<PrescriptionResponseDto>> getPrescriptionsByPatient(@PathVariable int patientId) {
		return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatient(patientId));
	}

	@PutMapping("/{prescriptionId}")
	public ResponseEntity<PrescriptionResponseDto> updatePrescription(@PathVariable int prescriptionId,
			@Valid @RequestBody PrescriptionRequestDto dto) {
		return ResponseEntity.ok(prescriptionService.updatePrescription(prescriptionId, dto));
	}

	@DeleteMapping("/{prescriptionId}")
	public ResponseEntity<Void> deletePrescription(@PathVariable int prescriptionId) {
		prescriptionService.deletePrescription(prescriptionId);
		return ResponseEntity.noContent().build();
	}
}
