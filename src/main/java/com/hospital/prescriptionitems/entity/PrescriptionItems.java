package com.hospital.prescriptionitems.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table
@Entity
public class PrescriptionItems {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
	private long itemId;
	@Column(nullable=false)
	private UUID prescriptionItemsID;
	@Column(nullable=false)
	private UUID medicineId;
	@Column(nullable=false)
	private Integer quantity;
	@Column(nullable=false)
	private Integer duration;
	@Column(nullable=false)
	private Integer frequency;
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	public UUID getPrescriptionItemsID() {
		return prescriptionItemsID;
	}
	public void setPrescriptionItemsID(UUID prescriptionItemsID) {
		this.prescriptionItemsID = prescriptionItemsID;
	}
	public UUID getMedicineId() {
		return medicineId;
	}
	public void setMedicineId(UUID medicineId) {
		this.medicineId = medicineId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getFrequency() {
		return frequency;
	}
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}
	
	
	
	

}
