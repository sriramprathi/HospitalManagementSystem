package com.nexturn.hms.dto;

import java.math.BigDecimal;

import com.nexturn.hms.entity.MedicineCategory;

public record MedicineResponseDto(
		int medicineId,
		String medicineName,
		BigDecimal price,
		int availability,
		MedicineCategory category) {

}