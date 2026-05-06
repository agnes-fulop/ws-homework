package com.example.homeworkapi.service;

import com.example.homeworkapi.dto.SensorReadingRequest;
import com.example.homeworkapi.entity.Sensor;
import com.example.homeworkapi.entity.SensorReading;
import com.example.homeworkapi.exception.InvalidMetricValueException;
import com.example.homeworkapi.exception.SensorNotFoundException;
import com.example.homeworkapi.repository.SensorReadingRepository;
import com.example.homeworkapi.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorReadingServiceImplTest {

    @Mock
    private SensorReadingRepository readingRepository;

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private SensorReadingServiceImpl service;

    private final Sensor sensor = new Sensor("s1", "Germany", "Berlin");

    @Test
    void recordReading_throwsSensorNotFoundException_whenSensorDoesNotExist() {
        when(sensorRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recordReading("unknown", new SensorReadingRequest("temperature", 20.0)))
                .isInstanceOf(SensorNotFoundException.class);

        verify(readingRepository, never()).save(any());
    }

    @Test
    void recordReading_throwsInvalidMetricValueException_whenHumidityExceedsMax() {
        when(sensorRepository.findById("s1")).thenReturn(Optional.of(sensor));

        assertThatThrownBy(() -> service.recordReading("s1", new SensorReadingRequest("humidity", 101.0)))
                .isInstanceOf(InvalidMetricValueException.class)
                .hasMessageContaining("humidity");

        verify(readingRepository, never()).save(any());
    }

    @Test
    void recordReading_throwsInvalidMetricValueException_whenTemperatureBelowMin() {
        when(sensorRepository.findById("s1")).thenReturn(Optional.of(sensor));

        assertThatThrownBy(() -> service.recordReading("s1", new SensorReadingRequest("temperature", -91.0)))
                .isInstanceOf(InvalidMetricValueException.class)
                .hasMessageContaining("temperature");

        verify(readingRepository, never()).save(any());
    }

    @Test
    void recordReading_throwsInvalidMetricValueException_whenWindSpeedIsNegative() {
        when(sensorRepository.findById("s1")).thenReturn(Optional.of(sensor));

        assertThatThrownBy(() -> service.recordReading("s1", new SensorReadingRequest("wind_speed", -1.0)))
                .isInstanceOf(InvalidMetricValueException.class)
                .hasMessageContaining("wind_speed");

        verify(readingRepository, never()).save(any());
    }

    @Test
    void recordReading_succeeds_whenValueIsAtBoundary() {
        when(sensorRepository.findById("s1")).thenReturn(Optional.of(sensor));
        when(readingRepository.save(any())).thenReturn(new SensorReading(sensor, "humidity", 100.0, Instant.now()));

        var response = service.recordReading("s1", new SensorReadingRequest("humidity", 100.0));

        assertThat(response.value()).isEqualTo(100.0);
        verify(readingRepository).save(any());
    }

    @Test
    void recordReading_succeeds_whenValueIsValidForKnownMetric() {
        when(sensorRepository.findById("s1")).thenReturn(Optional.of(sensor));
        when(readingRepository.save(any())).thenReturn(new SensorReading(sensor, "temperature", 22.5, Instant.now()));

        var response = service.recordReading("s1", new SensorReadingRequest("temperature", 22.5));

        assertThat(response.metric()).isEqualTo("temperature");
        assertThat(response.value()).isEqualTo(22.5);
        verify(readingRepository).save(any());
    }

    @Test
    void recordReading_skipsConstraintCheck_forCustomMetric() {
        when(sensorRepository.findById("s1")).thenReturn(Optional.of(sensor));
        when(readingRepository.save(any())).thenReturn(new SensorReading(sensor, "co2_level", 9999.0, Instant.now()));

        var response = service.recordReading("s1", new SensorReadingRequest("co2_level", 9999.0));

        assertThat(response.metric()).isEqualTo("co2_level");
        verify(readingRepository).save(any());
    }
}
