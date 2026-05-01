package com.example.homeworkapi.service;

import com.example.homeworkapi.dto.SensorQueryRequest;
import com.example.homeworkapi.dto.SensorQueryResponse;
import com.example.homeworkapi.dto.SensorReadingRequest;
import com.example.homeworkapi.dto.SensorReadingResponse;

public interface SensorReadingService {

    SensorReadingResponse recordReading(String sensorId, SensorReadingRequest request);

    SensorQueryResponse queryAverages(SensorQueryRequest request);
}
