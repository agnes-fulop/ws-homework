# Component Diagram (C4 — Level 3)

Zoom into the **Backend API** container. Shows the logical components (Spring stereotypes) and how a request flows through them.

The Frontend SPA container is intentionally not expanded here — its components (`RegisterSensor`, `RecordReading`, `QueryAverages`, `ResponseDisplay`, `api.js`) are simple and listed in [architecture.md](architecture.md).

```mermaid
C4Component
    title Component Diagram — Backend API

    Person(user, "User")
    Container(frontend, "Frontend SPA", "React + nginx")
    ContainerDb(db, "Database", "H2 in-memory")

    Container_Boundary(backend, "Backend API — Spring Boot 4") {
        Component(healthCtrl, "HealthController", "@RestController", "GET /api/health")
        Component(sensorCtrl, "SensorController", "@RestController", "GET/POST /api/sensors")
        Component(readingCtrl, "SensorReadingController", "@RestController", "POST /api/sensors/{id}/readings, GET /api/readings/averages")

        Component(sensorSvc, "SensorService", "@Service (iface + impl)", "Registers and lists sensors; @Transactional")
        Component(readingSvc, "SensorReadingService", "@Service (iface + impl)", "Records readings, computes averages, validates date ranges")

        Component(metricVal, "MetricConstraints", "Enum", "Per-metric value range and unit (temperature, humidity, wind_speed, pressure)")

        Component(sensorRepo, "SensorRepository", "Spring Data JPA", "findBySensorId, save")
        Component(readingRepo, "SensorReadingRepository", "Spring Data JPA", "save; JPQL average aggregations → MetricAverage")

        Component(exHandler, "GlobalExceptionHandler", "@RestControllerAdvice", "Maps domain exceptions to JSON ErrorResponse")
    }

    Rel(user, frontend, "Uses", "HTTP")
    Rel(frontend, sensorCtrl, "Calls", "JSON/HTTP")
    Rel(frontend, readingCtrl, "Calls", "JSON/HTTP")
    Rel(frontend, healthCtrl, "Calls", "JSON/HTTP")

    Rel(sensorCtrl, sensorSvc, "Uses")
    Rel(readingCtrl, readingSvc, "Uses")

    Rel(readingSvc, metricVal, "Validates value with")
    Rel(sensorSvc, sensorRepo, "Reads/writes")
    Rel(readingSvc, sensorRepo, "Looks up sensor")
    Rel(readingSvc, readingRepo, "Reads/writes")

    Rel(sensorRepo, db, "JPA/JDBC")
    Rel(readingRepo, db, "JPA/JDBC")

    Rel_Back(exHandler, sensorCtrl, "Handles exceptions from")
    Rel_Back(exHandler, readingCtrl, "Handles exceptions from")
```
