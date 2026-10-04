package com.nexturn.hms.service;

import java.util.List;

import com.nexturn.hms.dto.MedicineRequestDto;
import com.nexturn.hms.dto.MedicineResponseDto;

public interface MedicineService {
	MedicineResponseDto addMedicine(MedicineRequestDto dto);
	MedicineResponseDto getMedicineById(int medicineId);
	List<MedicineResponseDto> getAllMedicines();
	List<MedicineResponseDto> searchMedicinesByName(String name);
	MedicineResponseDto updateMedicine(int medicineId, MedicineRequestDto dto);
	void deleteMedicine(int medicineId);
}