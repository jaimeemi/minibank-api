# MiniBank API

[![CI - MiniBank Pipeline](https://github.com/jaimeemi/minibank-api/actions/workflows/ci.yml/badge.svg)](https://github.com/jaimeemi/minibank-api/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-informational)

## Overview

MiniBank is a production-ready RESTful banking API built with **Java 21** and **Spring Boot 3.4.1**. It solves the core problem of processing inter-account money transfers with automatic approval logic: transfers up to $5,000 are instantly `APPROVED`, while larger amounts are held as `PENDING` for further review. Every transaction is persisted to **PostgreSQL** with full audit timestamps.

The project demonstrates a clean layered architecture, compile-time DTO mapping via MapStruct, schema-controlled DDL, OpenAPI 3 documentation, and a GitHub Actions CI/CD pipeline that builds, tests, and publishes a Docker image to GitHub Container Registry (GHCR) on every push to `main`.

---

## Tech Stack

- **Java 21** — Virtual Threads enabled (`server.threads.virtual.enabled: true`) for high-throughput non-blocking I/O
- **Spring Boot 3.4.1** — Web, Data JPA, Validation, Actuator
- **Spring Data JPA + Hibernate** — ORM layer with `ddl-auto: none` (schema fully controlled by `schema.sql`)
- **PostgreSQL 16** — Relational persistence with `BIGSERIAL` PK, `CHECK` constraint on amount, and two performance indexes
- **MapStruct 1.6.3** — Compile-time DTO ↔ Entity mapping (zero reflection overhead)
- **Lombok** — Boilerplate reduction (`@Data`, `@Slf4j`, `@RequiredArgsConstructor`)
- **SpringDoc / Swagger UI 2.8.5** — Interactive OpenAPI 3 documentation
- **Spring Kafka** — Dependency included; autoconfiguration excluded (prepared for future event streaming)
- **Docker + Docker Compose** — Multi-stage image build with unprivileged runtime user (`minibank`)
- **GitHub Actions** — CI pipeline: build → test (real PostgreSQL service) → push image to GHCR
- **JUnit 5 + Mockito** — Unit testing with no external dependencies (no H2)
- **Maven 3.9.6** — Build and dependency management

---

## Architecture / System Flow

The application follows a strict **layered architecture** with unidirectional dependency flow. Each layer has a single responsibility and communicates only with the layer directly below it.

```
HTTP Client
    │
    ▼
┌─────────────────────────────┐
│  TransferController (REST)  │  ← Interface + Impl separation
└─────────────┬───────────────┘
              │ delegates to
              ▼
┌─────────────────────────────┐
│   TransferService (Logic)   │  ← Applies approval rule (amount ≤ 5000)
└─────────────┬───────────────┘
              │ delegates to
              ▼
┌──────────────────────────────────┐
│  TransferPersistenceAdapter      │  ← Converts DTO → Entity via MapStruct
└─────────────┬────────────────────┘
              │ calls
              ▼
┌─────────────────────────────┐
│   TransferRepository (JPA)  │  ← Spring Data interface
└─────────────┬───────────────┘
              │
              ▼
         PostgreSQL
```

```mermaid
sequenceDiagram
    participant Client
    participant Controller as TransferControllerImpl
    participant Service as TransferServiceImpl
    participant Adapter as TransferPersistenceAdapter
    participant Mapper as TransferMapper
    participant Repo as TransferRepository
    participant DB as PostgreSQL

    Client->>Controller: POST /transfer {origin, destination, amount}
    Controller->>Service: processTransfer(TransferDto)
    Service->>Service: setStatus() — amount ≤ 5000 → APPROVED, else PENDING
    Service->>Adapter: save(TransferDto)
    Adapter->>Mapper: toEntity(TransferDto)
    Mapper-->>Adapter: TransferEntity
    Adapter->>Repo: save(TransferEntity)
    Repo->>DB: INSERT INTO transfers
    DB-->>Repo: Saved row with generated id & created_at
    Repo-->>Adapter: TransferEntity (persisted)
    Adapter->>Mapper: toDto(TransferEntity)
    Mapper-->>Adapter: TransferDto
    Adapter-->>Service: TransferDto
    Service-->>Controller: TransferDto
    Controller-->>Client: 200 OK {id, origin, destination, amount, status, requestedDate}
```

### Error Handling Flow

```mermaid
flowchart TD
    A[Incoming Request] --> B{Bean Validation}
    B -- Invalid fields --> C[GlobalExceptionHandler]
    C --> D[400 Bad Request]
    B -- Valid --> E[Service + Persistence]
    E -- DataAccessException --> C
    C --> F[500 Internal Server Error]
    E -- Success --> G[200 OK]
```

---

## Prerequisites & Installation

### Requirements

| Tool            | Version  |
|-----------------|----------|
| Docker          | 24+      |
| Docker Compose  | 2.x      |
| Java (optional) | 21       |
| Maven (optional)| 3.9.6    |

### Run with Docker Compose (recommended)

```bash
git clone https://github.com/jaimeemi/minibank-api.git
cd minibank-api
docker-compose up --build
```

The API waits for PostgreSQL to pass its health check before starting. The database schema is initialized automatically from `src/main/resources/postgre/schema.sql`.

### Run locally (without Docker)

Requires a running PostgreSQL instance at `localhost:5432` with a database named `minibank`.

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/minibank
export SPRING_DATASOURCE_USERNAME=emilio
export SPRING_DATASOURCE_PASSWORD=<your_password>

./mvnw spring-boot:run
```

### Build the JAR

```bash
./mvnw clean package -DskipTests
```

### Run tests only

```bash
./mvnw test
```

---

## Core Features & Endpoints

### Automatic Approval Logic

| Condition       | Assigned Status |
|-----------------|-----------------|
| `amount ≤ 5000` | `APPROVED`      |
| `amount > 5000` | `PENDING`       |
| *(future)*      | `REJECTED`      |

### REST Endpoints

| Method | Path        | Description                                      | Success | Error          |
|--------|-------------|--------------------------------------------------|---------|----------------|
| `POST` | `/transfer` | Process a new transfer and persist the result    | `200`   | `400` / `500`  |

#### `POST /transfer` — Request Body

```json
{
  "origin": "123456",
  "destination": "789012",
  "amount": 1500.00
}
```

#### `POST /transfer` — Response `200 OK`

```json
{
  "id": 1,
  "origin": "123456",
  "destination": "789012",
  "amount": 1500.00,
  "status": "APPROVED",
  "requestedDate": "2025-01-15 10:30:00"
}
```

#### Error Responses

| Code  | Trigger                           | Body field `status` |
|-------|-----------------------------------|---------------------|
| `400` | Missing or invalid request fields | `BAD_REQUEST`       |
| `500` | Database persistence failure      | `ERROR`             |

### Interactive API Documentation

| Resource     | URL                                   |
|--------------|---------------------------------------|
| Swagger UI   | http://localhost:9000/swagger-ui.html |
| OpenAPI JSON | http://localhost:9000/api-docs        |

---

## Database Schema

```sql
CREATE TABLE IF NOT EXISTS transfers (
    id          BIGSERIAL,
    origin      VARCHAR(50)    NOT NULL,
    destination VARCHAR(50)    NOT NULL,
    amount      NUMERIC(15, 2) NOT NULL,
    status      VARCHAR(20)    NOT NULL,
    created_at  TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT pk_transfers PRIMARY KEY (id),
    CONSTRAINT chk_transfer_amount CHECK (amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_transfers_status ON transfers(status);
CREATE INDEX IF NOT EXISTS idx_transfers_origin ON transfers(origin);
```

- `idx_transfers_status` — optimizes queries filtering by `PENDING` or `APPROVED` status.
- `idx_transfers_origin` — accelerates reconciliation queries by source account.

---

## CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/ci.yml`) triggers on every push to `main`:

```mermaid
flowchart LR
    A[Push to main] --> B[Checkout Code]
    B --> C[Setup JDK 21 + Maven Cache]
    C --> D[Spin up PostgreSQL 16 Service]
    D --> E[mvn clean package — Build & Test]
    E --> F[Login to GHCR]
    F --> G[Build Docker Image]
    G --> H[Push to ghcr.io/jaimeemi/minibank-api:latest]
```

- Uses a **real PostgreSQL 16** service container during tests — no mocks, no H2.
- Maven dependency cache reduces build time on subsequent runs.
- Docker image published to **GitHub Container Registry** using `GITHUB_TOKEN` (no additional secrets required).

---

## Project Structure

```
com.minibank
├── controller/
│   ├── TransferController.java          # REST interface with OpenAPI annotations
│   └── impl/TransferControllerImpl.java # HTTP delegation to service
├── service/
│   ├── TransferService.java             # Business logic interface
│   └── impl/TransferServiceImpl.java    # Approval rule + orchestration
├── component/
│   └── TransferPersistenceAdapter.java  # Persistence facade (DTO ↔ Entity + save)
├── mapper/
│   └── TransferMapper.java              # MapStruct interface (compile-time generated)
├── models/
│   ├── dto/TransferDto.java
│   ├── entities/TransferEntity.java
│   └── enums/StatusEnum.java            # APPROVED | PENDING | REJECTED
├── repositories/
│   └── TransferRepository.java          # Spring Data JPA
├── error/
│   └── GlobalExceptionHandler.java      # @RestControllerAdvice — 400 / 500
└── ApiApplication.java
```

---

## DevOps & Future Enhancements

### 1. Kubernetes Deployment with Helm
Package the application as a Helm chart to deploy on Kubernetes. Add a `HorizontalPodAutoscaler` targeting CPU/memory thresholds and a `PodDisruptionBudget` for zero-downtime rolling updates. The existing Docker Compose health check maps directly to Kubernetes `readinessProbe` and `livenessProbe` definitions on port `9000`.

### 2. Observability Stack (Metrics + Tracing)
Integrate **Spring Boot Actuator** with **Micrometer** exporting metrics to **Prometheus**, visualized via **Grafana** dashboards. Add **OpenTelemetry** instrumentation for distributed tracing (e.g., Tempo or Jaeger) to trace each transfer request end-to-end across the controller, service, and persistence layers.

### 3. Kafka Event Streaming for Transfer Events
The Spring Kafka dependency is already included and autoconfiguration is intentionally excluded. The next step is to publish a `TransferProcessedEvent` to a Kafka topic after each successful persistence, enabling downstream consumers (e.g., a notification service or audit log) to react asynchronously without coupling to the core API.
