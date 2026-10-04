package com.nexturn.hms.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexturn.hms.dto.AppointmentRequestDto;
import com.nexturn.hms.dto.AppointmentResponseDto;
import com.nexturn.hms.dto.RescheduleRequestDto;
import com.nexturn.hms.entity.AppointmentStatus;
import com.nexturn.hms.service.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
   private final AppointmentService appointmentService;

   public AppointmentController(AppointmentService appointmentService) {
	this.appointmentService = appointmentService;
   }

    @PostMapping
	public ResponseEntity<AppointmentResponseDto> bookAppointment(@Valid @RequestBody AppointmentRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.bookAppointment(dto));
	}
    
    @GetMapping("/{appointmentId}")
	public ResponseEntity<AppointmentResponseDto> getAppointmentById(@PathVariable int appointmentId) {
		return ResponseEntity.ok(appointmentService.getAppointmentById(appointmentId));
	}
    @GetMapping("/patient/{patientId}")
	public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByPatient(@PathVariable int patientId) {
		return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
	}
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByDoctor(@PathVariable int doctorId) {
    	return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId));
    }
 
 	@GetMapping("/date")
 	public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByDate(
 			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
 		return ResponseEntity.ok(appointmentService.getAppointmentsByDate(date));
 	} 

 	@PutMapping("/{appointmentId}/reschedule")
 	public ResponseEntity<AppointmentResponseDto> rescheduleAppointment(@PathVariable int appointmentId,
 			@Valid @RequestBody RescheduleRequestDto dto) {
 		return ResponseEntity.ok(appointmentService.rescheduleAppointment(appointmentId, dto));
 	}

 	@PatchMapping("/{appointmentId}/cancel")
 	public ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable int appointmentId) {
 		return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId));
 	}
 	
 	@PatchMapping("/{appointmentId}/status")
 	public ResponseEntity<AppointmentResponseDto> updateStatus(@PathVariable int appointmentId,
 			@RequestParam AppointmentStatus status) {
 		return ResponseEntity.ok(appointmentService.updateStatus(appointmentId, status));
 	}

    
}
