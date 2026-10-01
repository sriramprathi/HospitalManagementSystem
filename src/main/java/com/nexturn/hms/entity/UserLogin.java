package com.nexturn.hms.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Table(name="user_login")
@Entity
public class UserLogin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private int userId;
    @Column(nullable = false, unique = true,length=25)
	private String userName;
    @Column(nullable = false,length=25)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false,length=20)
    private Role role;
    @OneToOne(mappedBy = "login", cascade = CascadeType.ALL)
    private Doctor doctor;
    @OneToOne(mappedBy = "login", cascade = CascadeType.ALL)
    private Patient patient;
    @OneToOne(mappedBy = "login", cascade = CascadeType.ALL)
    private Address address;
    
    public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
}