package com.example.homeworkapi.repository;

import com.example.homeworkapi.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findBySensorId(String sensorId);
}
