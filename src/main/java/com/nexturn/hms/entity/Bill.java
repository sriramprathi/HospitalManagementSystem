package com.nexturn.hms.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bill_id")
    private int billId;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false,length=20,name="bill_type")
    private BillType billType;
    @Column(nullable=false,columnDefinition = "DECIMAL(10,2)")
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false,length=20)
    private BillStatus status;
    @Column(name="date_of_generation", nullable=false)
    private LocalDate dateOfGeneration;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
	public int getBillId() {
		return billId;
	}
	public void setBillId(int billId) {
		this.billId = billId;
	}
	public BillType getBillType() {
		return billType;
	}
	public void setBillType(BillType billType) {
		this.billType = billType;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BillStatus getStatus() {
		return status;
	}
	public void setStatus(BillStatus status) {
		this.status = status;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public LocalDate getDateOfGeneration() {
		return dateOfGeneration;
	}
	public void setDateOfGeneration(LocalDate dateOfGeneration) {
		this.dateOfGeneration = dateOfGeneration;
	}   
}