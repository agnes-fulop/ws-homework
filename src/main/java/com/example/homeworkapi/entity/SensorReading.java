package com.example.homeworkapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "sensor_readings")
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sensorId;

    @Column(nullable = false)
    private String metric;

    @Column(name = "reading_value, nullable = false")
    private Double value;

    @Column(nullable = false)
    private Instant recordedAt;

    protected SensorReading() {}

    public SensorReading(String sensorId, String metric, Double value, Instant recordedAt) {
        this.sensorId = sensorId;
        this.metric = metric;
        this.value = value;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public String getSensorId() { return sensorId; }
    public String getMetric() { return metric; }
    public Double getValue() { return value; }
    public Instant getRecordedAt() { return recordedAt; }
}
