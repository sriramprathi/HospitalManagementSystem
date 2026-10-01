package com.nexturn.hms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "address")
public class Address {
	@Id
    @Column(name = "user_id")
    private int userId;
    @Column(name = "street", nullable = false, length = 40)
    private String street;
    @Column(name = "city", nullable = false, length = 40)
    private String city;
    @Column(name = "state", nullable = false, length = 40)
    private String state;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserLogin login;

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public UserLogin getLogin() {
		return login;
	}

	public void setLogin(UserLogin login) {
		this.login = login;
	}

}