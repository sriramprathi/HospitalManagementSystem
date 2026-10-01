package com.nexturn.hms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nexturn.hms.entity.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine,Integer> {

}
