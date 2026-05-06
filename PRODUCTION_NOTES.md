# Production Notes

This document outlines considerations that would be addressed before taking this application to production.

---

## Authentication & Authorisation

The current API has no access control. In production:

- Introduce user groups with role-based access control (RBAC) — e.g. differentiating between sensor devices (write-only), analysts (read-only), and administrators (full access)
- Secure the API with an industry-standard authentication mechanism such as OAuth 2.0 / JWT, or API keys for machine-to-machine sensor ingestion
- Protect the H2 console and Actuator endpoints so they are not publicly accessible

---

## Input Validation

Backend validation is implemented via Jakarta Bean Validation on request DTOs. Value range constraints for known metrics (temperature, humidity, wind_speed, pressure) are enforced on both the backend (`MetricConstraints`) and the frontend (HTML5 `min`/`max` attributes and a pre-submit check in the form action).

What remains for production:

- **Frontend**: extend validation to all other fields — required field feedback, date range constraint checks, and sensor ID format rules — to reduce unnecessary API calls
- **Backend**: add stricter domain-level validation for remaining fields (e.g. metric name format rules, sensor ID character constraints) and consider enforcing constraints for custom/unknown metrics

---

## Database

The current setup uses an in-memory H2 database suitable for development and demonstration only — all data is lost on restart.

In production:

- **Sensor registrations** — a standard relational database (e.g. PostgreSQL) is appropriate: small dataset, written once, benefits from referential integrity
- **Sensor readings** — a time-series database would be the better fit for high-frequency, append-only writes and time-range aggregation queries. **TimescaleDB** (a PostgreSQL extension) is the most practical choice: it adds automatic time-based partitioning and optimised aggregation while remaining fully SQL-compatible, meaning the existing JPA/JDBC layer would need minimal changes

---

## Testing

The current test suite covers unit tests and web layer (`@WebMvcTest`) tests. A production-grade strategy would also include:

- **Integration tests** — spin up a real database (e.g. via Testcontainers) and test the full stack from HTTP request to DB and back, catching issues that mocks cannot surface
- **End-to-end tests** — automate browser-level flows against a running instance of the full application (e.g. Playwright) to verify the frontend and backend work correctly together
- **Contract tests** — ensure the API contract between frontend and backend does not break silently as the codebase evolves

---

## Logging & Monitoring

Basic logging and monitoring are in place:

- **Logging** — SLF4J/Logback logs key lifecycle events at `INFO` (sensor registered, reading recorded) and `WARN` (duplicate registration, sensor not found, invalid metric value); unexpected errors are logged at `ERROR` with a full stack trace in `GlobalExceptionHandler`
- **Monitoring** — Spring Boot Actuator exposes `/actuator/health`, `/actuator/metrics` (JVM, HTTP request counts, DB pool stats via Micrometer), and `/actuator/loggers` (runtime log-level management)

What would be added for production:

- **Structured logging** — switch to JSON log output so logs can be ingested by an aggregation platform (ELK stack, Datadog, Grafana Loki)
- **Distributed tracing** — add OpenTelemetry instrumentation to correlate requests across services when the architecture grows beyond a single backend
- **Alerting** — define alert rules on error rate, latency thresholds, and JVM memory pressure so on-call engineers are notified before users notice

---

## Code Quality & Engineering Standards

- **Linting and static analysis** — integrate tools such as Checkstyle, SpotBugs, or SonarQube into the CI pipeline to catch style violations and potential bugs automatically
- **Coding standards document** — define and document conventions for the codebase: naming, package structure, error handling patterns, and test coverage expectations
- **Contribution guide** — provide a `CONTRIBUTING.md` describing the branching strategy, commit message format, PR review process, and definition of done so the engineering team can work consistently
- **CI/CD pipeline** — automate build, test, lint, and deployment steps on every pull request; block merges if any check fails
- **Architecture Decision Records (ADRs)** — maintain a `docs/adr/` directory in the repository documenting the most significant architectural decisions: what was decided, why, and what alternatives were considered. This is especially valuable in the AI-assisted development era, where it can otherwise become difficult to trace back *why* a decision was made versus just *what* was implemented. Each ADR is a short, dated Markdown file (e.g. `001-use-timescaledb-for-readings.md`) and remains in the repo permanently — superseded decisions are marked as such rather than deleted, preserving the full decision history
