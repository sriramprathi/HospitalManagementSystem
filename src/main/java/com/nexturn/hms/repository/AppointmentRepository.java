package com.nexturn.hms.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.nexturn.hms.entity.Appointment;
import com.nexturn.hms.entity.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

	// true if the doctor already has a non-cancelled appointment overlapping the given time range
	// excludeId lets reschedule ignore the appointment being moved (pass 0 when booking a new one)
	@Query("select count(a) > 0 from Appointment a "
			+ "where a.doctor.doctorId = :doctorId and a.date = :date "
			+ "and a.status <> :cancelled and a.appointmentId <> :excludeId "
			+ "and a.startTime < :endTime and a.endTime > :startTime")
	boolean existsOverlap(@Param("doctorId") int doctorId, @Param("date") LocalDate date,
			@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime,
			@Param("cancelled") AppointmentStatus cancelled, @Param("excludeId") int excludeId);

	List<Appointment> findByPatientPatientIdOrderByDateDescStartTimeDesc(int patientId);
	List<Appointment> findByDoctorDoctorIdOrderByDateAscStartTimeAsc(int doctorId);
	List<Appointment> findByDateOrderByStartTimeAsc(LocalDate date);
}