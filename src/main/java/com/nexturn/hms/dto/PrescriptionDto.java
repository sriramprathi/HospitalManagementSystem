package com.nexturn.hms.dto;

import java.time.LocalDate;
import java.util.List;

public record PrescriptionDto(
		    int prescriptionId,
	        int appointmentId,
	        String patientName,
	        String doctorName,
	        LocalDate date,
	        String notes,
	        List<PrescriptionItemsDto> items) {

}
