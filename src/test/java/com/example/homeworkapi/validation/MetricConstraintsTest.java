package com.example.homeworkapi.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class MetricConstraintsTest {

    @Test
    void forMetric_returnsEmpty_forUnknownMetric() {
        assertThat(MetricConstraints.forMetric("co2_level")).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({ "temperature", "humidity", "wind_speed", "pressure" })
    void forMetric_returnsPresent_forEachKnownMetric(String metric) {
        assertThat(MetricConstraints.forMetric(metric)).isPresent();
    }

    @ParameterizedTest(name = "{0} = {1} is valid")
    @CsvSource({
        "temperature, -90.0",   // min boundary
        "temperature,  60.0",   // max boundary
        "temperature,  20.0",   // typical value
        "humidity,      0.0",   // min boundary
        "humidity,    100.0",   // max boundary
        "humidity,     55.0",   // typical value
        "wind_speed,    0.0",   // min boundary
        "wind_speed,  500.0",   // max boundary
        "wind_speed,   80.0",   // typical value
        "pressure,    300.0",   // min boundary
        "pressure,   1100.0",   // max boundary
        "pressure,   1013.0",   // typical sea-level value
    })
    void isValid_returnsTrue_forValuesWithinRange(String metric, double value) {
        assertThat(MetricConstraints.forMetric(metric).orElseThrow().isValid(value)).isTrue();
    }

    @ParameterizedTest(name = "{0} = {1} is invalid")
    @CsvSource({
        "temperature, -90.1",   // just below min
        "temperature,  60.1",   // just above max
        "humidity,     -0.1",   // just below min
        "humidity,    100.1",   // just above max
        "wind_speed,   -0.1",   // just below min
        "wind_speed,  500.1",   // just above max
        "pressure,    299.9",   // just below min
        "pressure,   1100.1",   // just above max
    })
    void isValid_returnsFalse_forValuesOutsideRange(String metric, double value) {
        assertThat(MetricConstraints.forMetric(metric).orElseThrow().isValid(value)).isFalse();
    }
}
