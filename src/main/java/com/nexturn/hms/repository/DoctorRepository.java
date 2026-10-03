package com.nexturn.hms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexturn.hms.entity.Department;
import com.nexturn.hms.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor,Integer> {
	   Optional<Doctor> findByLoginUserId(int userId);
	   List<Doctor> findByDepartment(Department department);
}
