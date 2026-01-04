# WEX Purchase Service

A  Spring Boot based application designed for the secure management and currency conversion of international purchase transactions.  
This service integrates directly with the **U.S. Treasury Fiscal Data API** to provide real-time, audited financial reporting.

---

## 🏗 Architectural Summary

### Financial Integrity
- Uses `BigDecimal` for all monetary calculations
- Enforces `RoundingMode.HALF_UP` to prevent floating‑point inaccuracies

### Audit
- **JPA Auditing**  
  Automatically tracks `createdAt`, `createdBy`, `updatedAt`, and `updatedBy` for every persisted entity.
- **Behavioral Auditing (AOP)**  
  A custom AspectJ-based audit layer captures:
  - Service execution latency
  - Exception and failure telemetry

### Security Strategy
- Stateless **JWT-based authentication**
- Environment-aware `SecurityFilterChain`
  - Swagger, H2 Console, and Actuator endpoints allowed only in non-production profiles
  - Strict authorization rules in QA/Production

---

## 🚀 Execution & Deployment

### Local Development (Maven)
The application defaults to the **dev** profile using an H2 file-based database.

```bash
mvn clean package
java -jar target/wex-purchase-1.0.0.jar
```

### Containerization (Docker)
A multi-stage `Dockerfile` and `docker-compose.yml` are provided for rapid environment orchestration.

```bash
docker compose up --build
```

---


## 🌍 Environment Profiles

We utilize Spring Profiles to segregate configuration concerns across the SDLC:

| Profile    | Database      | Security Level | Purpose                                      |
|------------|---------------|----------------|----------------------------------------------|
| dev        | H2 (File)     | Permissive     | Local rapid development & H2 Console access  |
| test       | PostgreSQL    | Strict         | Integration testing via Testcontainers       |
| qa / prod  | PostgreSQL    | Mandatory JWT  | Staging and Production environments          |





```bash
java -jar target/wex-purchase-1.0.0.jar --spring.profiles.active=prod
```

---

## 🧪 Quality Assurance

### Unit Testing
- Conversion rounding correctness
- Six-month exchange-rate policy enforcement

### Integration Testing
- Testcontainers-based PostgreSQL
- Flyway migration validation

```bash
mvn test
```

---

## 📡 API Documentation

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- H2 Console (dev only): http://localhost:8080/h2-console

### Endpoints

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | /api/v1/auth/login | JWT issuance |
| POST | /api/v1/transactions | Create purchase |
| GET | /api/v1/transactions | List purchases |
| GET | /api/v1/transactions/{id} | Convert currency |

---

## 🛠 Additional Considerations

- **Caching**: Caffeine cache on Treasury API calls (6h TTL)
- **Tracing**: X-Correlation-Id propagated via Log4j2 MDC
- **Migrations**: Flyway-enforced schema versioning
