package com.nexturn.hms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexturn.hms.dto.AddressRequestDto;
import com.nexturn.hms.dto.AddressResponseDto;
import com.nexturn.hms.service.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/{userId}/address")
public class AddressController {

	private final AddressService addressService;

	public AddressController(AddressService addressService) {
		this.addressService = addressService;
	}

	// one endpoint for both adding a new address and updating the existing one
	@PutMapping
	public ResponseEntity<AddressResponseDto> saveOrUpdateAddress(@PathVariable int userId,
			@Valid @RequestBody AddressRequestDto dto) {
		return ResponseEntity.ok(addressService.saveOrUpdateAddress(userId, dto));
	}

	@GetMapping
	public ResponseEntity<AddressResponseDto> getAddressByUserId(@PathVariable int userId) {
		return ResponseEntity.ok(addressService.getAddressByUserId(userId));
	}
}