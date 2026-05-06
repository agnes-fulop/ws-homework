package com.example.homeworkapi.exception;

public class InvalidMetricValueException extends RuntimeException {
    public InvalidMetricValueException(String metric, double value, double min, double max, String unit) {
        super(String.format("Value %.2f is out of the valid range [%.2f, %.2f] %s for metric '%s'",
                value, min, max, unit, metric));
    }
}
