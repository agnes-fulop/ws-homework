# Assignment description

A REST API for registering weather sensors and ingesting metric readings, with an endpoint to query average values across sensors and time ranges. Includes a React 19 demo frontend.

For production considerations (auth, database choice, testing strategy, and engineering standards) see [PRODUCTION_NOTES.md](PRODUCTION_NOTES.md).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0 |
| Persistence | Spring Data JPA + H2 (in-memory) |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Frontend | React 19 + Vite |
| Container | Docker + nginx |

> **Note:** The H2 database is in-memory — all data is lost on restart. It is only used for the purpose of this exercise.

---

## Data Model

### Sensor

Represents a physical sensor device. The database primary key (`id`) is auto-generated; the human-readable identifier (`sensorId`) is supplied by the client.

| Field | Type | Constraints |
|---|---|---|
| `id` | `Long` | Auto-generated primary key, not exposed in requests |
| `sensorId` | `String` | Required, unique, client-supplied |
| `country` | `String` | Optional |
| `city` | `String` | Optional |

### SensorReading

A single metric measurement recorded by a sensor.

| Field | Type | Constraints |
|---|---|---|
| `id` | `Long` | Auto-generated |
| `sensor` | `Sensor` | Required, FK on `sensors.id` (auto-generated PK) |
| `metric` | `String` | Required (e.g. `temperature`, `humidity`) |
| `value` | `Double` | Required |
| `recordedAt` | `Instant` | Set server-side at time of ingestion |

The `sensor_readings` table has indexes on `sensor_id` and `recorded_at` to support query performance.

---

## API Reference

All endpoints are prefixed with `/api`. Dates are ISO-8601 strings. Error responses follow RFC 7807 Problem Details format.

### Health

| Method | Path | Response |
|---|---|---|
| `GET` | `/api/health` | `200 OK` |

### List Sensors

`GET /api/sensors`

Returns all registered sensors.

**Response body:**
```json
[
  { "id": 1, "sensorId": "sensor-berlin-01", "country": "Germany", "city": "Berlin" },
  { "id": 2, "sensorId": "sensor-paris-01",  "country": "France",  "city": "Paris"  }
]
```

**Responses:**
- `200 OK` — empty array if no sensors registered

---

### Register Sensor

`POST /api/sensors`

**Request body:**
```json
{
  "sensorId": "sensor-berlin-01",
  "country": "Germany",
  "city": "Berlin"
}
```

| Field | Required |
|---|---|
| `sensorId` | Yes |
| `country` | No |
| `city` | No |

**Response body:**
```json
{
  "id": 1,
  "sensorId": "sensor-berlin-01",
  "country": "Germany",
  "city": "Berlin"
}
```

**Responses:**
- `201 Created` — sensor registered
- `400 Bad Request` — validation failure (missing `sensorId`)
- `409 Conflict` — sensor with that `sensorId` already exists

---

### Record Reading

`POST /api/sensors/{sensorId}/readings`

**Request body:**
```json
{
  "metric": "temperature",
  "value": 22.5
}
```

| Field | Required |
|---|---|
| `metric` | Yes |
| `value` | Yes |

The following metrics have enforced value ranges:

| Metric | Valid range | Unit |
|---|---|---|
| `temperature` | −90 to 60 | °C |
| `humidity` | 0 to 100 | % |
| `wind_speed` | 0 to 500 | km/h |
| `pressure` | 300 to 1100 | hPa |

Custom metric names are accepted without range constraints.

**Responses:**
- `201 Created` — reading recorded, returns reading with `id` and `recordedAt`
- `400 Bad Request` — validation failure, or value outside the valid range for a known metric
- `404 Not Found` — sensor does not exist

---

### Query Average Metrics

`POST /api/readings/query`

**Request body:**
```json
{
  "sensorIds": ["sensor-berlin-01", "sensor-paris-01"],
  "metrics": ["temperature", "humidity"],
  "from": "2025-04-01",
  "to": "2025-04-30"
}
```

| Field | Required | Notes |
|---|---|---|
| `sensorIds` | No | `null` or omitted = all sensors; `[]` = returns empty result immediately |
| `metrics` | Yes | At least one value required |
| `from` | No | ISO-8601 date; must be provided together with `to` |
| `to` | No | ISO-8601 date; must be provided together with `from` |

- When `from`/`to` are omitted the query defaults to the **last 24 hours**
- Maximum date range: **31 days**

**Response:**
```json
{
  "averages": [
    { "metric": "temperature", "average": 21.75 },
    { "metric": "humidity", "average": 64.3 }
  ]
}
```

**Responses:**
- `200 OK`
- `400 Bad Request` — missing metrics, partial date range, or range exceeds 31 days

---

## Project Structure

```
├── src/main/java/com/example/homeworkapi/
│   ├── controller/        REST controllers
│   ├── service/           Business logic (interface + implementation)
│   ├── repository/        Spring Data JPA repositories
│   ├── entity/            JPA entities (Sensor, SensorReading)
│   ├── dto/               Request/response records
│   ├── exception/         Domain exceptions + GlobalExceptionHandler
│   ├── validation/        MetricConstraints — valid ranges for known metrics
│   └── config/            OpenAPI and H2 console configuration
├── frontend/
│   ├── src/
│   │   ├── components/    RegisterSensor, RecordReading, QueryAverages
│   │   ├── api.js         Fetch helpers for all POST endpoints
│   │   └── App.jsx        Tab-based layout
│   ├── nginx.conf         Production proxy config (Docker only)
│   └── Dockerfile
├── Dockerfile             Backend image
├── docker-compose.yml
└── pom.xml
```

---

## Running Locally (without Docker)

### Prerequisites
- Java 25
- Maven 3.6+
- Node.js 18+

### 1. Start the backend

```bash
mvn spring-boot:run
```

The API starts on [http://localhost:8080](http://localhost:8080).

### 2. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on [http://localhost:5173](http://localhost:5173) and proxies `/api` requests to the backend automatically.

---

## Running with Docker

### Prerequisites
- Docker Desktop

### Start both services

```bash
docker compose up --build
```

| Service | URL |
|---|---|
| Frontend | [http://localhost:3000](http://localhost:3000) |
| Backend API | [http://localhost:8080](http://localhost:8080) |

To stop: `docker compose down`

> In Docker, nginx serves the production frontend build and proxies `/api/*` to the backend container. The Vite dev proxy is not used.

---

## Developer Tools

| Tool | URL | Notes |
|---|---|---|
| Swagger UI | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Interactive API documentation |
| Actuator Health | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | |
| Actuator Metrics | [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics) | Lists available metrics; append `/{metric.name}` for detail |
| Actuator Loggers | [http://localhost:8080/actuator/loggers](http://localhost:8080/actuator/loggers) | View and change log levels at runtime via POST |
