package com.example.homeworkapi.service;

import com.example.homeworkapi.dto.RegisterSensorRequest;
import com.example.homeworkapi.dto.SensorResponse;

public interface SensorService {

    SensorResponse registerSensor(RegisterSensorRequest request);
}
