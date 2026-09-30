package com.hospital.prescription.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table
@Entity
public class Prescription {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
	private UUID prescriptionId;
	@Column(nullable=false,unique=true)
	private UUID appointmentId;	
	private String notes;
	public UUID getPrescriptionId() {
		return prescriptionId;
	}
	public void setPrescriptionId(UUID prescriptionId) {
		this.prescriptionId = prescriptionId;
	}
	public UUID getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(UUID appointmentId) {
		this.appointmentId = appointmentId;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	

}
