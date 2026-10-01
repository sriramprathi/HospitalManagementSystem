package com.nexturn.hms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nexturn.hms.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient,Integer> {

}
