package com.example.homeworkapi.exception;

public class SensorNotFoundException extends RuntimeException {

    public SensorNotFoundException(String id) {
        super("Sensor not found with id: " + id);
    }
}
