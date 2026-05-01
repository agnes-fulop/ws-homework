package com.example.homeworkapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensors")
public class Sensor {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    private String country;

    private String city;

    protected Sensor() {}

    public Sensor(String id, String country, String city) {
        this.id = id;
        this.country = country;
        this.city = city;
    }

    public String getId() { return id; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
}
