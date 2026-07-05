# MiniBank API

API REST bancaria construida con Spring Boot 3 que gestiona transferencias entre cuentas, con aprobación automática basada en monto y persistencia en PostgreSQL.

---

## Stack Tecnológico

| Tecnología          | Versión   | Rol                              |
|---------------------|-----------|----------------------------------|
| Java                | 21        | Lenguaje base (Virtual Threads)  |
| Spring Boot         | 3.4.1     | Framework principal              |
| Spring Data JPA     | -         | Persistencia ORM                 |
| PostgreSQL          | 16        | Base de datos relacional         |
| MapStruct           | 1.6.3     | Mapeo DTO ↔ Entity               |
| Lombok              | -         | Reducción de boilerplate         |
| SpringDoc / Swagger | 2.8.5     | Documentación OpenAPI            |
| Spring Kafka        | -         | Integración de mensajería (prep) |
| Docker / Compose    | -         | Contenerización                  |
| Maven               | 3.9.6     | Gestión de dependencias y build  |

---

## Arquitectura

El proyecto sigue una arquitectura en capas con separación clara de responsabilidades:

```
Controller (HTTP)
     │
     ▼
  Service (Lógica de negocio)
     │
     ▼
PersistenceAdapter (Componente de persistencia)
     │
     ▼
  Repository (Spring Data JPA)
     │
     ▼
  PostgreSQL
```

### Paquetes

```
com.minibank
├── controller/          # Interfaz REST + implementación
│   └── impl/
├── service/             # Lógica de negocio
│   └── impl/
├── component/           # Adaptador de persistencia
├── mapper/              # Conversión DTO ↔ Entity (MapStruct)
├── models/
│   ├── dto/             # Objetos de transferencia de datos
│   ├── entitys/         # Entidades JPA
│   └── enums/           # Estados de transferencia
├── repositories/        # Repositorios Spring Data JPA
├── error/               # Manejo global de excepciones
└── ApiApplication.java  # Punto de entrada
```

---

## Lógica de Negocio

### Procesamiento de Transferencias

El único endpoint disponible recibe una transferencia y aplica la siguiente regla de aprobación automática:

| Condición              | Estado asignado |
|------------------------|-----------------|
| `amount <= 5000`       | `APPROVED`      |
| `amount > 5000`        | `PENDING`       |

El estado `REJECTED` está definido en el enum pero no se asigna automáticamente en la versión actual (reservado para lógica futura).

### Flujo completo

1. El cliente envía `POST /transfer` con `origin`, `destination` y `amount`.
2. El `TransferControllerImpl` delega al `TransferServiceImpl`.
3. El servicio evalúa el monto y asigna el `StatusEnum` correspondiente.
4. El `TransferPersistenceAdapter` convierte el DTO a entidad via `TransferMapper` y persiste en BD.
5. La entidad guardada se convierte de vuelta a DTO y se retorna con `HTTP 200`.

---

## Modelo de Datos

### Tabla `transfers`

| Columna       | Tipo                  | Descripción                        |
|---------------|-----------------------|------------------------------------|
| `id`          | `BIGSERIAL` (PK)      | Identificador autoincremental      |
| `origin`      | `VARCHAR(50)`         | Cuenta de origen                   |
| `destination` | `VARCHAR(50)`         | Cuenta de destino                  |
| `amount`      | `NUMERIC(15,2)`       | Monto (debe ser > 0)               |
| `status`      | `VARCHAR(20)`         | Estado: `APPROVED`, `PENDING`, `REJECTED` |
| `created_at`  | `TIMESTAMP`           | Fecha de creación (automática)     |

Índices adicionales:
- `idx_transfers_status` — optimiza búsquedas por estado
- `idx_transfers_origin` — acelera conciliaciones por cuenta origen

---

## API REST

### `POST /transfer`

Procesa una nueva transferencia bancaria.

**Request Body:**
```json
{
  "origin": "123456",
  "destination": "789012",
  "amount": 1500.00
}
```

**Response `200 OK`:**
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

**Respuestas de error:**

| Código | Causa                              |
|--------|------------------------------------|
| `400`  | Campos inválidos o faltantes       |
| `500`  | Error de persistencia en base de datos |

---

## Manejo de Errores

`GlobalExceptionHandler` intercepta excepciones globalmente:

- `DataAccessException` → `500` con mensaje genérico (no expone detalles internos)
- `MethodArgumentNotValidException` → `400` con indicación de error de validación

---

## Configuración

La aplicación se configura via variables de entorno (con valores por defecto para desarrollo local):

| Variable                    | Default                                      |
|-----------------------------|----------------------------------------------|
| `SPRING_DATASOURCE_URL`     | `jdbc:postgresql://localhost:5432/minibank`  |
| `SPRING_DATASOURCE_USERNAME`| `emilio`                                     |
| `SPRING_DATASOURCE_PASSWORD`| `postgres`                                   |
| `SPRING_PROFILES_ACTIVE`    | -                                            |

El esquema de BD se inicializa automáticamente desde `classpath:postgre/schema.sql` al arrancar.

---

## Ejecución

### Con Docker Compose (recomendado)

```bash
docker-compose up --build
```

Levanta PostgreSQL y la API juntos. La API espera a que la BD esté saludable antes de iniciar.

### Local (sin Docker)

Requiere PostgreSQL corriendo en `localhost:5432` con la base de datos `minibank`.

```bash
./mvnw spring-boot:run
```

### Build del JAR

```bash
./mvnw clean package -DskipTests
```

---

## Documentación Interactiva (Swagger UI)

Una vez levantada la aplicación:

| Recurso        | URL                                        |
|----------------|--------------------------------------------|
| Swagger UI     | http://localhost:9000/swagger-ui.html      |
| OpenAPI JSON   | http://localhost:9000/api-docs             |

---

## Notas de Diseño

- **Virtual Threads (Java 21):** habilitados en el servidor para mayor throughput con I/O bloqueante.
- **Kafka:** dependencia incluida pero autoconfiguración excluida. Preparado para integración futura de eventos de transferencia.
- **DDL manual:** Hibernate no gestiona el esquema (`ddl-auto: none`); el control total es via `schema.sql`.
- **Dockerfile multi-stage:** imagen de runtime mínima (`eclipse-temurin:21-jre-alpine`) con usuario sin privilegios por seguridad.
