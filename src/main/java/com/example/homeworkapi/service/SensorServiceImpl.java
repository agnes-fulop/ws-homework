package com.example.homeworkapi.service;

import com.example.homeworkapi.dto.RegisterSensorRequest;
import com.example.homeworkapi.dto.SensorResponse;
import com.example.homeworkapi.entity.Sensor;
import com.example.homeworkapi.exception.SensorAlreadyExistsException;
import com.example.homeworkapi.repository.SensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SensorServiceImpl implements SensorService {

    private static final Logger log = LoggerFactory.getLogger(SensorServiceImpl.class);

    private final SensorRepository sensorRepository;

    public SensorServiceImpl(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    @Transactional
    public SensorResponse registerSensor(RegisterSensorRequest request) {
        log.info("Registering sensor: sensorId={}", request.sensorId());
        try {
            SensorResponse response = toResponse(sensorRepository.saveAndFlush(toEntity(request)));
            log.info("Sensor registered: id={}, sensorId={}", response.id(), response.sensorId());
            return response;
        } catch (DataIntegrityViolationException e) {
            log.warn("Rejected duplicate sensor registration: sensorId={}", request.sensorId());
            throw new SensorAlreadyExistsException(request.sensorId());
        }
    }

    private Sensor toEntity(RegisterSensorRequest request) {
        return new Sensor(request.sensorId(), request.country(), request.city());
    }

    private SensorResponse toResponse(Sensor sensor) {
        return new SensorResponse(sensor.getId(), sensor.getSensorId(), sensor.getCountry(), sensor.getCity());
    }
}
