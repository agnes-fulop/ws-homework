package com.example.homeworkapi.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterSensorRequest(
        @NotBlank String id,
        String country,
        String city
) {}
