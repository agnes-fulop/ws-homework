package com.example.homeworkapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "sensor_readings", indexes = {
        @Index(name = "idx_sensor_readings_sensor_id", columnList = "sensor_id"),
        @Index(name = "idx_sensor_readings_recorded_at", columnList = "recorded_at")
})
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sensor_id", nullable = false, updatable = false)
    private Sensor sensor;

    @Column(nullable = false)
    private String metric;

    @Column(name = "reading_value", nullable = false)
    private Double value;

    @Column(nullable = false)
    private Instant recordedAt;

    protected SensorReading() {}

    public SensorReading(Sensor sensor, String metric, Double value, Instant recordedAt) {
        this.sensor = sensor;
        this.metric = metric;
        this.value = value;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public Sensor getSensor() { return sensor; }
    public String getMetric() { return metric; }
    public Double getValue() { return value; }
    public Instant getRecordedAt() { return recordedAt; }
}
