package com.nexturn.hms.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nexturn.hms.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Integer> {

	List<Bill> findByPatientPatientIdOrderByDateOfGenerationDesc(int patientId);
}