package com.nexturn.hms.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexturn.hms.dto.MedicineRequestDto;
import com.nexturn.hms.dto.MedicineResponseDto;
import com.nexturn.hms.entity.Medicine;
import com.nexturn.hms.exceptions.DuplicateMedicineException;
import com.nexturn.hms.exceptions.MedicineInUseException;
import com.nexturn.hms.exceptions.MedicineNotFoundException;
import com.nexturn.hms.repository.MedicineRepository;

@Service
public class MedicineServiceImpl implements MedicineService {

	@Autowired
	MedicineRepository medicineRepo;
	ModelMapper modelMapper;

	@Override
	@Transactional
	public MedicineResponseDto addMedicine(MedicineRequestDto dto) {
		String name = dto.medicineName().trim();
		if (medicineRepo.existsByMedicineNameIgnoreCase(name)) {
			throw new DuplicateMedicineException("Medicine already exists: " + name);
		}
		Medicine medicine = new Medicine();
		applyDetails(medicine, dto);
		return toResponse(medicineRepo.save(medicine));
	}

	@Override
	@Transactional(readOnly = true)
	public MedicineResponseDto getMedicineById(int medicineId) {
		return toResponse(findMedicine(medicineId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<MedicineResponseDto> getAllMedicines() {
		return medicineRepo.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<MedicineResponseDto> searchMedicinesByName(String name) {
		return medicineRepo.findByMedicineNameContainingIgnoreCase(name.trim()).stream()
				.map(this::toResponse).toList();
	}

	@Override
	@Transactional
	public MedicineResponseDto updateMedicine(int medicineId, MedicineRequestDto dto) {
		Medicine medicine = findMedicine(medicineId);
		String newName = dto.medicineName().trim();
		// the duplicate check applies only when the name is actually changing
		if (!medicine.getMedicineName().equalsIgnoreCase(newName)
				&& medicineRepo.existsByMedicineNameIgnoreCase(newName)) {
			throw new DuplicateMedicineException("Medicine already exists: " + newName);
		}
		applyDetails(medicine, dto);
		return toResponse(medicineRepo.save(medicine));
	}

	@Override
	@Transactional
	public void deleteMedicine(int medicineId) {
		Medicine medicine = findMedicine(medicineId);
		// a medicine that appears in any prescription cannot be removed
		if (!medicine.getPrescriptionItems().isEmpty()) {
			throw new MedicineInUseException("Medicine is used in prescriptions and cannot be deleted");
		}
		medicineRepo.delete(medicine);
	}

	private Medicine findMedicine(int medicineId) {
		return medicineRepo.findById(medicineId)
				.orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + medicineId));
	}

	private void applyDetails(Medicine medicine, MedicineRequestDto dto) {
		modelMapper.map(dto, medicine);
		// the mapper copies the name as sent, so remove stray spaces afterwards
		medicine.setMedicineName(medicine.getMedicineName().trim());
	}

	private MedicineResponseDto toResponse(Medicine m) {
		return new MedicineResponseDto(m.getMedicineId(), m.getMedicineName(), m.getPrice(),
				m.getAvailability(), m.getCategory());
	}
}