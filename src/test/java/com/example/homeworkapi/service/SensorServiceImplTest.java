package com.example.homeworkapi.service;

import com.example.homeworkapi.dto.RegisterSensorRequest;
import com.example.homeworkapi.dto.SensorResponse;
import com.example.homeworkapi.entity.Sensor;
import com.example.homeworkapi.exception.SensorAlreadyExistsException;
import com.example.homeworkapi.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorServiceImplTest {

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private SensorServiceImpl sensorService;

    @Test
    void registerSensor_savesAndReturnsResponse() {
        var request = new RegisterSensorRequest("SN-001", "Hungary", "Budapest");
        when(sensorRepository.existsById("SN-001")).thenReturn(false);
        when(sensorRepository.save(any(Sensor.class))).thenReturn(new Sensor("SN-001", "Hungary", "Budapest"));

        SensorResponse response = sensorService.registerSensor(request);

        assertThat(response.id()).isEqualTo("SN-001");
        assertThat(response.country()).isEqualTo("Hungary");
        assertThat(response.city()).isEqualTo("Budapest");
        verify(sensorRepository).save(any(Sensor.class));
    }

    @Test
    void registerSensor_withNullMetadata_returnsResponseWithNulls() {
        var request = new RegisterSensorRequest("SN-002", null, null);
        when(sensorRepository.existsById("SN-002")).thenReturn(false);
        when(sensorRepository.save(any(Sensor.class))).thenReturn(new Sensor("SN-002", null, null));

        SensorResponse response = sensorService.registerSensor(request);

        assertThat(response.id()).isEqualTo("SN-002");
        assertThat(response.country()).isNull();
        assertThat(response.city()).isNull();
    }

    @Test
    void registerSensor_throwsSensorAlreadyExistsException_whenIdAlreadyRegistered() {
        var request = new RegisterSensorRequest("SN-001", "Hungary", "Budapest");
        when(sensorRepository.existsById("SN-001")).thenReturn(true);

        assertThatThrownBy(() -> sensorService.registerSensor(request))
                .isInstanceOf(SensorAlreadyExistsException.class)
                .hasMessageContaining("SN-001");

        verify(sensorRepository, never()).save(any());
    }
}
