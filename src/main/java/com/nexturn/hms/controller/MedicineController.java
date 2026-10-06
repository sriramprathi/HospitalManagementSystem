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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexturn.hms.dto.MedicineRequestDto;
import com.nexturn.hms.dto.MedicineResponseDto;
import com.nexturn.hms.service.MedicineService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class MedicineController {

	private final MedicineService medicineService;

	public MedicineController(MedicineService medicineService) {
		this.medicineService = medicineService;
	}

	@PostMapping
	public ResponseEntity<MedicineResponseDto> addMedicine(@Valid @RequestBody MedicineRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(medicineService.addMedicine(dto));
	}

	@GetMapping("/{medicineId}")
	public ResponseEntity<MedicineResponseDto> getMedicineById(@PathVariable int medicineId) {
		return ResponseEntity.ok(medicineService.getMedicineById(medicineId));
	}

	@GetMapping
	public ResponseEntity<List<MedicineResponseDto>> getAllMedicines() {
		return ResponseEntity.ok(medicineService.getAllMedicines());
	}

	// example: GET /api/medicines/search?name=para
	@GetMapping("/search")
	public ResponseEntity<List<MedicineResponseDto>> searchMedicinesByName(@RequestParam String name) {
		return ResponseEntity.ok(medicineService.searchMedicinesByName(name));
	}

	@PutMapping("/{medicineId}")
	public ResponseEntity<MedicineResponseDto> updateMedicine(@PathVariable int medicineId,
			@Valid @RequestBody MedicineRequestDto dto) {
		return ResponseEntity.ok(medicineService.updateMedicine(medicineId, dto));
	}

	@DeleteMapping("/{medicineId}")
	public ResponseEntity<Void> deleteMedicine(@PathVariable int medicineId) {
		medicineService.deleteMedicine(medicineId);
		return ResponseEntity.noContent().build();
	}
}