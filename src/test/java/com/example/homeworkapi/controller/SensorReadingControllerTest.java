package com.example.homeworkapi.controller;

import com.example.homeworkapi.dto.MetricAverage;
import com.example.homeworkapi.dto.SensorQueryResponse;
import com.example.homeworkapi.service.SensorReadingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SensorReadingController.class)
class SensorReadingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SensorReadingService sensorReadingService;

    @Test
    void queryAverages_returns200_withSingleMetric() throws Exception {
        when(sensorReadingService.queryAverages(any()))
                .thenReturn(new SensorQueryResponse(List.of(new MetricAverage("temperature", 21.5))));

        mockMvc.perform(get("/api/readings/averages").param("metrics", "temperature"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averages[0].metric").value("temperature"))
                .andExpect(jsonPath("$.averages[0].average").value(21.5));
    }

    @Test
    void queryAverages_returns200_withAllParams() throws Exception {
        when(sensorReadingService.queryAverages(any()))
                .thenReturn(new SensorQueryResponse(List.of(
                        new MetricAverage("temperature", 21.5),
                        new MetricAverage("humidity", 64.3))));

        mockMvc.perform(get("/api/readings/averages")
                        .param("metrics", "temperature", "humidity")
                        .param("sensorIds", "sensor-berlin-01", "sensor-paris-01")
                        .param("from", "2025-04-01")
                        .param("to", "2025-04-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averages.length()").value(2));
    }

    @Test
    void queryAverages_returns200_withEmptyResult() throws Exception {
        when(sensorReadingService.queryAverages(any()))
                .thenReturn(new SensorQueryResponse(List.of()));

        mockMvc.perform(get("/api/readings/averages").param("metrics", "temperature"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averages").isEmpty());
    }

    @Test
    void queryAverages_returns400_whenMetricsParamMissing() throws Exception {
        mockMvc.perform(get("/api/readings/averages"))
                .andExpect(status().isBadRequest());
    }
}
