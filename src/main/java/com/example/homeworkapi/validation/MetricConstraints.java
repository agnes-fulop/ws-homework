package com.example.homeworkapi.validation;

import java.util.Arrays;
import java.util.Optional;

public enum MetricConstraints {
    TEMPERATURE("temperature", -90.0, 60.0, "°C"),
    HUMIDITY("humidity", 0.0, 100.0, "%"),
    WIND_SPEED("wind_speed", 0.0, 500.0, "km/h"),
    PRESSURE("pressure", 300.0, 1100.0, "hPa");

    private final String metricName;
    private final double min;
    private final double max;
    private final String unit;

    MetricConstraints(String metricName, double min, double max, String unit) {
        this.metricName = metricName;
        this.min = min;
        this.max = max;
        this.unit = unit;
    }

    public static Optional<MetricConstraints> forMetric(String name) {
        return Arrays.stream(values())
                .filter(c -> c.metricName.equals(name))
                .findFirst();
    }

    public boolean isValid(double value) {
        return value >= min && value <= max;
    }

    public String getMetricName() { return metricName; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public String getUnit() { return unit; }
}
