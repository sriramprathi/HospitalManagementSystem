package com.nexturn.hms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nexturn.hms.entity.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription,Integer> {
	boolean existsByAppointmentAppointmentId(int appointmentId);
	Optional<Prescription> findByAppointmentAppointmentId(int appointmentId);
	List<Prescription> findByAppointmentPatientPatientIdOrderByAppointmentDateDesc(int patientId);
}
