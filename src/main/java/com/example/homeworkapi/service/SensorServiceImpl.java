package com.example.homeworkapi.service;

import com.example.homeworkapi.dto.RegisterSensorRequest;
import com.example.homeworkapi.dto.SensorResponse;
import com.example.homeworkapi.entity.Sensor;
import com.example.homeworkapi.exception.SensorAlreadyExistsException;
import com.example.homeworkapi.repository.SensorRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;

    public SensorServiceImpl(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    @Transactional
    public SensorResponse registerSensor(RegisterSensorRequest request) {
        try {
            return toResponse(sensorRepository.saveAndFlush(toEntity(request)));
        } catch (DataIntegrityViolationException e) {
            throw new SensorAlreadyExistsException(request.id());
        }
    }

    private Sensor toEntity(RegisterSensorRequest request) {
        return new Sensor(request.id(), request.country(), request.city());
    }

    private SensorResponse toResponse(Sensor sensor) {
        return new SensorResponse(sensor.getId(), sensor.getCountry(), sensor.getCity());
    }
}
