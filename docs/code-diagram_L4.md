# Code Diagram (C4 — Level 4)

Class-level view of the sensor-reading slice — the most substantial flow in the codebase. Other slices (health check, plain sensor registration) are simpler and follow the same conventions.

C4 deliberately treats Level 4 as optional and easily out-of-date; consult the source for ground truth.

```mermaid
classDiagram
    direction LR

    class SensorReadingController {
        <<@RestController>>
        +recordReading(sensorId, SensorReadingRequest) ResponseEntity~SensorReadingResponse~
        +queryAverages(sensorIds, metrics, from, to) SensorQueryResponse
    }

    class SensorReadingService {
        <<interface>>
        +recordReading(sensorId, SensorReadingRequest) SensorReadingResponse
        +queryAverages(SensorQueryRequest) SensorQueryResponse
    }

    class SensorReadingServiceImpl {
        <<@Service>>
        -SensorRepository sensorRepository
        -SensorReadingRepository readingRepository
        +recordReading(...) SensorReadingResponse
        +queryAverages(...) SensorQueryResponse
        -resolveDateRange(from, to) DateRange
    }

    class SensorRepository {
        <<JpaRepository>>
        +findBySensorId(String) Optional~Sensor~
    }

    class SensorReadingRepository {
        <<JpaRepository>>
        +save(SensorReading) SensorReading
        +findAveragesByMetricsAndDateRange(...) List~MetricAverage~
        +findAveragesBySensorIdsAndMetricsAndDateRange(...) List~MetricAverage~
    }

    class Sensor {
        <<@Entity>>
        -Long id
        -String sensorId
        -String country
        -String city
    }

    class SensorReading {
        <<@Entity>>
        -Long id
        -Sensor sensor
        -String metric
        -Double value
        -Instant recordedAt
    }

    class MetricConstraints {
        <<enum>>
        TEMPERATURE
        HUMIDITY
        WIND_SPEED
        PRESSURE
        +forMetric(String)$ Optional~MetricConstraints~
        +isValid(double) boolean
    }

    class SensorReadingRequest {
        <<record>>
        +String metric
        +Double value
    }

    class SensorReadingResponse {
        <<record>>
        +Long id
        +String sensorId
        +String metric
        +Double value
        +Instant recordedAt
    }

    class SensorQueryRequest {
        <<record>>
        +List~String~ sensorIds
        +List~String~ metrics
        +LocalDate from
        +LocalDate to
    }

    class SensorQueryResponse {
        <<record>>
        +List~MetricAverage~ averages
    }

    class MetricAverage {
        <<record>>
        +String metric
        +Double average
    }

    SensorReadingController --> SensorReadingService : uses
    SensorReadingService <|.. SensorReadingServiceImpl
    SensorReadingServiceImpl --> SensorRepository
    SensorReadingServiceImpl --> SensorReadingRepository
    SensorReadingServiceImpl --> MetricConstraints : validates with

    SensorReadingController ..> SensorReadingRequest : accepts
    SensorReadingController ..> SensorReadingResponse : returns
    SensorReadingController ..> SensorQueryRequest : builds
    SensorReadingController ..> SensorQueryResponse : returns

    SensorReadingRepository ..> MetricAverage : projects to
    SensorRepository --> Sensor
    SensorReadingRepository --> SensorReading
    SensorReading "*" --> "1" Sensor : @ManyToOne LAZY
```
