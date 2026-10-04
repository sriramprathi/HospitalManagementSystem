package com.nexturn.hms.service;

import java.util.List;

import com.nexturn.hms.dto.BillResponseDto;
import com.nexturn.hms.entity.BillStatus;

public interface BillService {
	BillResponseDto generateConsultationBill(int appointmentId);
	BillResponseDto generateMedicineBill(int prescriptionId);
	BillResponseDto getBillById(int billId);
	List<BillResponseDto> getBillsByPatient(int patientId);
	List<BillResponseDto> getAllBills();
	BillResponseDto updateStatus(int billId, BillStatus status);
}