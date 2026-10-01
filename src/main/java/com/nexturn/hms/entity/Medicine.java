package com.nexturn.hms.entity;

import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="medicine_id")
    private int medicineId;
    @Column(name="medicine_name",nullable=false,unique=true,length=40)
    private String medicineName;
    @Column(name="medicine_price",nullable=false,columnDefinition = "DECIMAL(8,2)")
    private BigDecimal price;
    @Column(name="medicine_availability",nullable=false)
    private int availability;
    @Enumerated(EnumType.STRING)
    @Column(name="medicine_category",nullable=false,length=30)
    private MedicineCategory category;
 // Medicine (inverse side)
    @OneToMany(mappedBy = "medicine")
    private List<PrescriptionItems> prescriptionItems;
    
	public int getMedicineId() {
		return medicineId;
	}
	public void setMedicineId(int medicineId) {
		this.medicineId = medicineId;
	}
	public String getMedicineName() {
		return medicineName;
	}
	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public int getAvailability() {
		return availability;
	}
	public void setAvailability(int availability) {
		this.availability = availability;
	}
	public MedicineCategory getCategory() {
		return category;
	}
	public void setCategory(MedicineCategory category) {
		this.category = category;
	}
	public List<PrescriptionItems> getPrescriptionItems() {
		return prescriptionItems;
	}
	public void setPrescriptionItems(List<PrescriptionItems> prescriptionItems) {
		this.prescriptionItems = prescriptionItems;
	}
    
}