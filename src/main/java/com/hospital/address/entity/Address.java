package com.hospital.address.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "address")
public class Address {
    @Id
    @Column(length = 60)
    private UUID userId;
    @Column(length = 60)
    private String street;
    @Column
    private int pincode;
    @Column
    private String city;
    @Column
    private String state;

    public Address() {
    }

    public Address(UUID userId, String street, int pincode, String city, String state) {
        this.userId = userId;
        this.street = street;
        this.pincode = pincode;
        this.city = city;
        this.state = state;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
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
}