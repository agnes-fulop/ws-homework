package com.example.homeworkapi.dto;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record SensorQueryRequest(
        List<String> sensorIds,
        @NotEmpty List<String> metrics,
        LocalDate from,
        LocalDate to
) {}
