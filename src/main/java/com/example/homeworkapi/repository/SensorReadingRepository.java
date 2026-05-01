package com.example.homeworkapi.repository;

import com.example.homeworkapi.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    @Query("""
            SELECT r.metric, AVG(r.value)
            FROM SensorReading r
            WHERE r.sensorId IN :sensorIds
              AND r.metric IN :metrics
              AND r.recordedAt >= :from
              AND r.recordedAt < :to
            GROUP BY r.metric
            """)
    List<Object[]> findAveragesBySensorIdsAndMetricsAndDateRange(
            @Param("sensorIds") List<String> sensorIds,
            @Param("metrics") List<String> metrics,
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query("""
            SELECT r.metric, AVG(r.value)
            FROM SensorReading r
            WHERE r.metric IN :metrics
              AND r.recordedAt >= :from
              AND r.recordedAt < :to
            GROUP BY r.metric
            """)
    List<Object[]> findAveragesByMetricsAndDateRange(
            @Param("metrics") List<String> metrics,
            @Param("from") Instant from,
            @Param("to") Instant to);
}
