package com.example.homeworkapi.exception;

public class SensorAlreadyExistsException extends RuntimeException {

    public SensorAlreadyExistsException(String id) {
        super("Sensor already registered with id: " + id);
    }
}
