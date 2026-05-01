package com.example.homeworkapi.dto;

import java.util.List;

public record SensorQueryResponse(List<MetricAverage> averages) {}
