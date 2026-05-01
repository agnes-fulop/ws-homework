package com.example.homeworkapi.dto;

import java.time.Instant;

public record SensorReadingResponse(
        Long id,
        String sensorId,
        String metric,
        Double value,
        Instant recordedAt
) {}
