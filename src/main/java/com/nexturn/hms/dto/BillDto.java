package com.nexturn.hms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nexturn.hms.entity.BillStatus;
import com.nexturn.hms.entity.BillType;

public record BillDto(
		    int billId,
	        int patientId,
	        String patientName,
	        BillType billType,
	        BigDecimal amount,
	        BillStatus status,
	        LocalDate dateOfGeneration) {

}
