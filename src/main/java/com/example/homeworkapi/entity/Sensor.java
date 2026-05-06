package com.example.homeworkapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensors")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false, unique = true)
    private String sensorId;

    private String country;

    private String city;

    protected Sensor() {}

    public Sensor(String sensorId, String country, String city) {
        this.sensorId = sensorId;
        this.country = country;
        this.city = city;
    }

    public Long getId() { return id; }
    public String getSensorId() { return sensorId; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
}
