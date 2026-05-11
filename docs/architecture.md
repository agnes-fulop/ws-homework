# Architecture

System diagram of the Weather Sensor API.

```mermaid
flowchart TB
    Browser["🌐 Browser"]

    subgraph Frontend["frontend container — nginx :3000"]
        Nginx["nginx<br/>SPA fallback<br/>/api/* → backend:8080"]
        subgraph React["React 19 + Vite (built)"]
            App["App.jsx<br/>3-tab sidebar"]
            Reg["RegisterSensor.jsx"]
            Rec["RecordReading.jsx"]
            Qry["QueryAverages.jsx"]
            ApiJs["api.js<br/>fetch wrappers"]
            App --> Reg & Rec & Qry
            Reg & Rec & Qry --> ApiJs
        end
        Nginx -.serves.-> React
    end

    subgraph Backend["backend container — Spring Boot 4 :8080 (Java 25)"]
        direction TB

        subgraph Ctrl["controller/ — @RestController, /api"]
            HealthC["HealthController<br/>GET /api/health"]
            SensorC["SensorController<br/>GET  /api/sensors<br/>POST /api/sensors"]
            ReadingC["SensorReadingController<br/>POST /api/sensors/{id}/readings<br/>GET  /api/readings/averages"]
        end

        subgraph Svc["service/ — interfaces + @Transactional impls"]
            SensorS["SensorService<br/>SensorServiceImpl"]
            ReadingS["SensorReadingService<br/>SensorReadingServiceImpl"]
        end

        subgraph Val["validation/"]
            MC["MetricConstraints (enum)<br/>temperature / humidity /<br/>wind_speed / pressure"]
        end

        subgraph Repo["repository/ — Spring Data JPA"]
            SensorR["SensorRepository<br/>findBySensorId"]
            ReadingR["SensorReadingRepository<br/>JPQL avg queries → MetricAverage"]
        end

        subgraph Ex["exception/"]
            GEH["GlobalExceptionHandler<br/>@RestControllerAdvice"]
            ExList["SensorAlreadyExists<br/>SensorNotFound<br/>InvalidDateRange<br/>InvalidMetricValue"]
            ExList --> GEH
        end

        subgraph Cfg["config/"]
            OAI["OpenApiConfig<br/>/swagger-ui.html"]
        end

        SensorC --> SensorS
        ReadingC --> ReadingS
        ReadingS --> MC
        SensorS --> SensorR
        ReadingS --> SensorR
        ReadingS --> ReadingR
    end

    subgraph DB["H2 in-memory — jdbc:h2:mem:sensorsdb"]
        direction LR
        Sensors[("sensors<br/>id PK auto<br/>sensor_id UNIQUE<br/>country, city")]
        Readings[("sensor_readings<br/>id PK auto<br/>sensor_id FK → sensors.id<br/>metric, reading_value<br/>recorded_at<br/>idx: sensor_id, recorded_at")]
        Sensors -.@ManyToOne LAZY.- Readings
    end

    Browser -- "http :3000" --> Nginx
    ApiJs -. "/api/*" .-> Nginx
    Nginx -- "proxy /api/*" --> Ctrl
    SensorR -- "Hibernate" --> Sensors
    ReadingR -- "Hibernate" --> Readings

    classDef container fill:#1e3a5f,stroke:#4a90e2,color:#fff
    classDef db fill:#5b3a1e,stroke:#d4943c,color:#fff
    classDef ext fill:#2d4a2d,stroke:#5cb85c,color:#fff
    class Frontend,Backend container
    class DB db
    class Browser ext
```

## Request flow

### Record a reading

```
Browser ──POST /api/sensors/{id}/readings──► SensorReadingController
       ──► SensorReadingService.recordReading
              ├─ SensorRepository.findBySensorId  → 404 if missing
              ├─ MetricConstraints.forMetric      → 400 if out of range
              └─ SensorReadingRepository.save     → 201 + body
```

### Query averages

```
Browser ──GET /api/readings/averages?metrics=…&sensorIds=…──►
   SensorReadingController
       ──► SensorReadingService.queryAverages
              ├─ resolveDateRange (default last 24h, max 31d)
              └─ Repository JPQL GROUP BY metric → MetricAverage rows
```
