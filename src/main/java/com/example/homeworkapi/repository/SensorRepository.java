package com.example.homeworkapi.repository;

import com.example.homeworkapi.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, String> {}
