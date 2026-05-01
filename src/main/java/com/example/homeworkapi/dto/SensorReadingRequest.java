package com.example.homeworkapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SensorReadingRequest(
        @NotBlank String metric,
        @NotNull Double value
) {}
