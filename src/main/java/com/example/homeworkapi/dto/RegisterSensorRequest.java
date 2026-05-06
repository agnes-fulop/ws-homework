package com.example.homeworkapi.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterSensorRequest(
        @NotBlank String sensorId,
        String country,
        String city
) {}
