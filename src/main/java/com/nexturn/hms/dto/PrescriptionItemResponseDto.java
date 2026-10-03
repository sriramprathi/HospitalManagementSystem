package com.nexturn.hms.dto;

public record PrescriptionItemResponseDto(
		int prescriptionItemId,
		int medicineId,
		String medicineName,
		int quantity,
		int duration,
		int frequency) {

}