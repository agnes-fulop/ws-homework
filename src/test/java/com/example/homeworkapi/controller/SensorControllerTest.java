package com.example.homeworkapi.controller;

import com.example.homeworkapi.dto.SensorResponse;
import com.example.homeworkapi.exception.SensorAlreadyExistsException;
import com.example.homeworkapi.service.SensorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SensorController.class)
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SensorService sensorService;

    @Test
    void registerSensor_returns201WithSensorBody() throws Exception {
        when(sensorService.registerSensor(any()))
                .thenReturn(new SensorResponse("SN-001", "Hungary", "Budapest"));

        mockMvc.perform(post("/api/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": "SN-001", "country": "Hungary", "city": "Budapest"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("SN-001"))
                .andExpect(jsonPath("$.country").value("Hungary"))
                .andExpect(jsonPath("$.city").value("Budapest"));
    }

    @Test
    void registerSensor_returns201WithNullMetadata_whenMetadataOmitted() throws Exception {
        when(sensorService.registerSensor(any()))
                .thenReturn(new SensorResponse("SN-002", null, null));

        mockMvc.perform(post("/api/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": "SN-002"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("SN-002"))
                .andExpect(jsonPath("$.country").doesNotExist());
    }

    @Test
    void registerSensor_returns409_whenSensorAlreadyExists() throws Exception {
        when(sensorService.registerSensor(any()))
                .thenThrow(new SensorAlreadyExistsException("SN-001"));

        mockMvc.perform(post("/api/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": "SN-001"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Sensor already registered with id: SN-001"));
    }

    @Test
    void registerSensor_returns400_whenIdIsBlank() throws Exception {
        mockMvc.perform(post("/api/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("id: must not be blank"));
    }

    @Test
    void registerSensor_returns400_whenIdIsNull() throws Exception {
        mockMvc.perform(post("/api/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": null}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}
