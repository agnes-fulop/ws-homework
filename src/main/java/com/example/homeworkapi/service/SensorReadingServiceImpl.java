package com.example.homeworkapi.service;

import com.example.homeworkapi.dto.MetricAverage;
import com.example.homeworkapi.dto.SensorQueryRequest;
import com.example.homeworkapi.dto.SensorQueryResponse;
import com.example.homeworkapi.dto.SensorReadingRequest;
import com.example.homeworkapi.dto.SensorReadingResponse;
import com.example.homeworkapi.entity.Sensor;
import com.example.homeworkapi.entity.SensorReading;
import com.example.homeworkapi.exception.InvalidDateRangeException;
import com.example.homeworkapi.exception.InvalidMetricValueException;
import com.example.homeworkapi.exception.SensorNotFoundException;
import com.example.homeworkapi.validation.MetricConstraints;
import com.example.homeworkapi.repository.SensorReadingRepository;
import com.example.homeworkapi.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SensorReadingServiceImpl implements SensorReadingService {

    private final SensorReadingRepository readingRepository;
    private final SensorRepository sensorRepository;

    public SensorReadingServiceImpl(SensorReadingRepository readingRepository,
                                    SensorRepository sensorRepository) {
        this.readingRepository = readingRepository;
        this.sensorRepository = sensorRepository;
    }

    @Override
    @Transactional
    public SensorReadingResponse recordReading(String sensorId, SensorReadingRequest request) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));
        MetricConstraints.forMetric(request.metric()).ifPresent(constraints -> {
            if (!constraints.isValid(request.value())) {
                throw new InvalidMetricValueException(
                        request.metric(), request.value(),
                        constraints.getMin(), constraints.getMax(), constraints.getUnit());
            }
        });
        SensorReading saved = readingRepository.save(
                new SensorReading(sensor, request.metric(), request.value(), Instant.now()));
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SensorQueryResponse queryAverages(SensorQueryRequest request) {
        if (request.sensorIds() != null && request.sensorIds().isEmpty()) {
            return new SensorQueryResponse(List.of());
        }

        DateRange range = resolveDateRange(request.from(), request.to());

        List<MetricAverage> averages = request.sensorIds() == null
                ? readingRepository.findAveragesByMetricsAndDateRange(request.metrics(), range.from(), range.to())
                : readingRepository.findAveragesBySensorIdsAndMetricsAndDateRange(request.sensorIds(), request.metrics(), range.from(), range.to());

        return new SensorQueryResponse(averages);
    }

    private DateRange resolveDateRange(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            Instant now = Instant.now();
            return new DateRange(now.minus(1, ChronoUnit.DAYS), now);
        }
        if (from == null || to == null) {
            throw new InvalidDateRangeException("Both 'from' and 'to' must be specified together");
        }
        if (from.isAfter(to)) {
            throw new InvalidDateRangeException("'from' must not be after 'to'");
        }
        if (ChronoUnit.DAYS.between(from, to) > 31) {
            throw new InvalidDateRangeException("Date range cannot exceed 31 days");
        }
        return new DateRange(
                from.atStartOfDay(ZoneOffset.UTC).toInstant(),
                to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    private SensorReadingResponse toResponse(SensorReading reading) {
        return new SensorReadingResponse(
                reading.getId(),
                reading.getSensor().getId(),
                reading.getMetric(),
                reading.getValue(),
                reading.getRecordedAt());
    }

    private record DateRange(Instant from, Instant to) {}
}
