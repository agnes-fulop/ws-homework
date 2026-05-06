package com.example.homeworkapi.controller;

import com.example.homeworkapi.dto.RegisterSensorRequest;
import com.example.homeworkapi.dto.SensorResponse;
import com.example.homeworkapi.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping("/sensors")
    public List<SensorResponse> listSensors() {
        return sensorService.listSensors();
    }

    @PostMapping("/sensors")
    public ResponseEntity<SensorResponse> registerSensor(
            @RequestBody @Valid RegisterSensorRequest request) {
        SensorResponse response = sensorService.registerSensor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
