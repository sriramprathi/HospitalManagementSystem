package com.nexturn.hms.dto;

public record PrescriptionItemsDto(
		 int prescriptionItemId,
	        int medicineId,
	        String medicineName,
	        int quantity,
	        int duration,
	        int frequency) {

}
