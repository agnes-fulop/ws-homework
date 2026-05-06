package com.example.homeworkapi.controller;

import com.example.homeworkapi.dto.SensorQueryRequest;
import com.example.homeworkapi.dto.SensorQueryResponse;
import com.example.homeworkapi.dto.SensorReadingRequest;
import com.example.homeworkapi.dto.SensorReadingResponse;
import com.example.homeworkapi.service.SensorReadingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SensorReadingController {

    private final SensorReadingService sensorReadingService;

    public SensorReadingController(SensorReadingService sensorReadingService) {
        this.sensorReadingService = sensorReadingService;
    }

    @PostMapping("/sensors/{sensorId}/readings")
    public ResponseEntity<SensorReadingResponse> recordReading(
            @PathVariable String sensorId,
            @RequestBody @Valid SensorReadingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sensorReadingService.recordReading(sensorId, request));
    }

    @GetMapping("/readings/averages")
    public SensorQueryResponse queryAverages(
            @RequestParam(required = false) List<String> sensorIds,
            @RequestParam List<String> metrics,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return sensorReadingService.queryAverages(new SensorQueryRequest(sensorIds, metrics, from, to));
    }
}
