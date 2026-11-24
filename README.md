# federal-contracts-spending-viz

Monorepo for federal contract spending data. The Spring Boot service in `backend/` is backed by the [USAspending API](https://fedspendingtransparency.github.io). The React app in `frontend/` lists award amounts from that service.

## Backend

```bash
cd backend
./mvnw spring-boot:run
```

```bash
curl -X POST localhost:8080/api/v1/contracts/search \
  -H 'Content-Type: application/json' \
  -d '{"startDate":"2024-10-01","endDate":"2025-09-30","limit":5}'
# Response includes totalPages for that page size.

# Companies ranked by contract spending, up to 100 per page
curl -X POST localhost:8080/api/v1/contracts/recipients/search \
  -H 'Content-Type: application/json' \
  -d '{"startDate":"2024-10-01","endDate":"2025-09-30","limit":100,"page":1}'
```

## Frontend

The dev server proxies `/api` to `http://localhost:8080`. Start the backend first.

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. Set `VITE_API_BASE_URL` (for example `http://localhost:8080`) when the UI is not using that proxy. The API allows browser calls from `http://localhost:5173`.

## Layers (`com.fedspending.contracts`)

| Package | Responsibility |
| --- | --- |
| `controller` | HTTP endpoints, request validation, error mapping |
| `service` | Business logic; maps between API DTOs and USAspending models |
| `client` | Outbound HTTP to USAspending (`RestClient`, config, error wrapping) |
| `model.dto` | Request/response types exposed by this service |
| `model.usaspending` | Wire types for the USAspending API |

Dependencies flow one way: controller → service → client. Controllers never touch `model.usaspending`.
