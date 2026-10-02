package com.nexturn.hms.dto;

import java.math.BigDecimal;

import com.nexturn.hms.entity.MedicineCategory;

public record MedicineDto(
		    int medicineId,
	        String medicineName,
	        BigDecimal price,
	        int availability,
	        MedicineCategory category) {

}
