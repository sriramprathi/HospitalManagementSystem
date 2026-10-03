package com.nexturn.hms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexturn.hms.dto.DoctorRegisteredResponseDto;
import com.nexturn.hms.dto.DoctorRequestDto;
import com.nexturn.hms.dto.DoctorResponseDto;
import com.nexturn.hms.entity.Department;
import com.nexturn.hms.service.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

	private final DoctorService doctorService;

	public DoctorController(DoctorService doctorService) {
		this.doctorService = doctorService;
	}

	@PostMapping
	public ResponseEntity<DoctorRegisteredResponseDto> addDoctor(@Valid @RequestBody DoctorRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.addDoctor(dto));
	}
	@GetMapping("/{doctorId}")
	public ResponseEntity<DoctorResponseDto> getDoctorById(@PathVariable int doctorId) {
		return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
	}
	@GetMapping("/user/{userId}")
	public ResponseEntity<DoctorResponseDto> getDoctorByUserId(@PathVariable int userId) {
		return ResponseEntity.ok(doctorService.getDoctorByUserId(userId));
	}
	@GetMapping
	public ResponseEntity<List<DoctorResponseDto>> getAllDoctors() {
		return ResponseEntity.ok(doctorService.getAllDoctors());
	}
	@GetMapping("/department/{department}")
	public ResponseEntity<List<DoctorResponseDto>> getDoctorsByDepartment(@PathVariable Department department) {
		return ResponseEntity.ok(doctorService.getDoctorsByDepartment(department));
	}
	@PutMapping("/{doctorId}")
	public ResponseEntity<DoctorResponseDto> updateDoctor(@PathVariable int doctorId,
			@Valid @RequestBody DoctorRequestDto dto) {
		return ResponseEntity.ok(doctorService.updateDoctor(doctorId, dto));
	}
	@DeleteMapping("/{doctorId}")
	public ResponseEntity<Void> deleteDoctor(@PathVariable int doctorId) {
		doctorService.deleteDoctor(doctorId);
		return ResponseEntity.noContent().build();
	}
}