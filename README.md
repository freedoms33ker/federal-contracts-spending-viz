# federal-contracts-spending-viz

Spring Boot 4 (Java 17) service for federal contract spending data, backed by the [USAspending API](https://fedspendingtransparency.github.io).

## Run

```bash
./mvnw spring-boot:run
curl -X POST localhost:8080/api/v1/contracts/search \
  -H 'Content-Type: application/json' \
  -d '{"startDate":"2024-10-01","endDate":"2025-09-30","limit":5}'
```

## Layers (`com.fedspending.contracts`)

| Package | Responsibility |
| --- | --- |
| `controller` | HTTP endpoints, request validation, error mapping |
| `service` | Business logic; maps between API DTOs and USAspending models |
| `client` | Outbound HTTP to USAspending (`RestClient`, config, error wrapping) |
| `model.dto` | Request/response types exposed by this service |
| `model.usaspending` | Wire types for the USAspending API |

Dependencies flow one way: controller → service → client. Controllers never touch `model.usaspending`.
