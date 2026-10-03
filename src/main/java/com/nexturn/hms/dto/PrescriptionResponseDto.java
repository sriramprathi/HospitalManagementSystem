package com.nexturn.hms.dto;

import java.util.List;

public record PrescriptionResponseDto(
		int prescriptionId,
		int appointmentId,
		String patientName,
		String doctorName,
		String notes,
		List<PrescriptionItemResponseDto> items) {

}