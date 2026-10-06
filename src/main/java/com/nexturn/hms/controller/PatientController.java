package com.nexturn.hms.controller;

import java.util.List;

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

import com.nexturn.hms.dto.PatientRegisteredResponseDto;
import com.nexturn.hms.dto.PatientRequestDto;
import com.nexturn.hms.dto.PatientResponseDto;
import com.nexturn.hms.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class PatientController {

	private final PatientService patientService;

	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	@PostMapping
	public ResponseEntity<PatientRegisteredResponseDto> registerPatient(@Valid @RequestBody PatientRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(patientService.registerPatient(dto));
	}
    //we will add commands
	@GetMapping("/{patientId}")
	public ResponseEntity<PatientResponseDto> getPatientById(@PathVariable int patientId) {
		return ResponseEntity.ok(patientService.getPatientById(patientId));
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<PatientResponseDto> getPatientByUserId(@PathVariable int userId) {
		return ResponseEntity.ok(patientService.getPatientByUserId(userId));
	}

	@GetMapping
	public ResponseEntity<List<PatientResponseDto>> getAllPatients() {
		return ResponseEntity.ok(patientService.getAllPatients());
	}

	@PutMapping("/{patientId}")
	public ResponseEntity<PatientResponseDto> updatePatient(@PathVariable int patientId,
			@Valid @RequestBody PatientRequestDto dto) {
		return ResponseEntity.ok(patientService.updatePatient(patientId, dto));
	}

	@DeleteMapping("/{patientId}")
	public ResponseEntity<Void> deletePatient(@PathVariable int patientId) {
		patientService.deletePatient(patientId);
		return ResponseEntity.noContent().build();
	}
}