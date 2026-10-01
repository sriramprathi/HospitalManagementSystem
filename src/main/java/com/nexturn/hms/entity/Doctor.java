package com.nexturn.hms.entity;

import java.math.BigDecimal;
import java.util.List;
 import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Table(name="doctors")
@Entity
public class Doctor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="doctor_id")
	private int doctorId;
	@Column(length=30,nullable=false)
	private String firstName;
	@Column(length=30,nullable=false)
	private String lastName;
    @Enumerated(EnumType.STRING)
	@Column(length=30,nullable=false)
	private Department department;
	@Column(length=50,nullable=false)
	private String qualification;
	@Column(length=15,nullable=false)
	private String phoneNumber;
	@Column(nullable=false, columnDefinition = "DECIMAL(10,2)")
	private BigDecimal consultationFee;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private UserLogin login;
	@OneToMany(mappedBy = "doctor")
	private List<Appointment> appointments;

	
	public int getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public BigDecimal getConsultationFee() {
		return consultationFee;
	}
	public void setConsultationFee(BigDecimal consultationFee) {
		this.consultationFee = consultationFee;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public UserLogin getLogin() {
		return login;
	}
	public void setLogin(UserLogin login) {
		this.login = login;
	}
	public List<Appointment> getAppointments() {
		return appointments;
	}
	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}
}