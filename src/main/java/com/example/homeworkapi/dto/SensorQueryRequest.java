package com.example.homeworkapi.dto;

import java.time.LocalDate;
import java.util.List;

public record SensorQueryRequest(
        List<String> sensorIds,
        List<String> metrics,
        LocalDate from,
        LocalDate to
) {}
