package com.nexturn.hms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nexturn.hms.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor,Integer> {

}
