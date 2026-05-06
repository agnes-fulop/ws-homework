package com.example.homeworkapi.service;

import com.example.homeworkapi.dto.RegisterSensorRequest;
import com.example.homeworkapi.dto.SensorResponse;

import java.util.List;

public interface SensorService {

    SensorResponse registerSensor(RegisterSensorRequest request);

    List<SensorResponse> listSensors();
}
