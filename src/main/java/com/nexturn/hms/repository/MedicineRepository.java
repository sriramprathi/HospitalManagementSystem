package com.nexturn.hms.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nexturn.hms.entity.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine, Integer> {

	boolean existsByMedicineNameIgnoreCase(String medicineName);
	List<Medicine> findByMedicineNameContainingIgnoreCase(String name);
}