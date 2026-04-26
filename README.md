# Multi-Tenant SaaS Metadata Manager

Spring Boot REST API with schema-per-tenant isolation, audit logging, versioning, and paginated search.

## Stack
- Java 17 · Spring Boot 3.2 · Spring Data JPA · MySQL 8 · Flyway · HikariCP · Micrometer

## Architecture

```
Request → TenantResolutionFilter (X-Tenant-ID header)
        → TenantContext (ThreadLocal)
        → TenantAwareDataSource (AbstractRoutingDataSource)
        → Tenant Schema (tenant_acme / tenant_globex / ...)
        → MetadataEntity table (per schema)
        → AuditLog (async, REQUIRES_NEW transaction)
```

## Quick Start

### 1. Start MySQL
```bash
docker-compose up mysql -d
```

### 2. Run the app
```bash
mvn spring-boot:run
```
Flyway will auto-create and migrate all tenant schemas on startup.

### 3. Make requests (always include X-Tenant-ID header)

**Create metadata:**
```bash
curl -X POST http://localhost:8080/api/v1/metadata \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant_acme" \
  -d '{
    "key": "app.feature.dark_mode",
    "value": "true",
    "category": "features",
    "description": "Enables dark mode UI",
    "status": "ACTIVE",
    "tags": "ui,feature-flag"
  }'
```

**List with filters and pagination:**
```bash
curl "http://localhost:8080/api/v1/metadata?category=features&status=ACTIVE&page=0&size=20&sortBy=createdAt&sortDir=desc" \
  -H "X-Tenant-ID: tenant_acme"
```

**Search:**
```bash
curl "http://localhost:8080/api/v1/metadata?search=dark" \
  -H "X-Tenant-ID: tenant_acme"
```

**Get by key:**
```bash
curl http://localhost:8080/api/v1/metadata/key/app.feature.dark_mode \
  -H "X-Tenant-ID: tenant_acme"
```

**Update:**
```bash
curl -X PUT http://localhost:8080/api/v1/metadata/{id} \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant_acme" \
  -d '{ "key": "app.feature.dark_mode", "value": "false", "category": "features", "status": "ACTIVE" }'
```

**Delete:**
```bash
curl -X DELETE http://localhost:8080/api/v1/metadata/{id} \
  -H "X-Tenant-ID: tenant_acme"
```

## API Reference

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/v1/metadata | List all (paginated, filterable) |
| GET | /api/v1/metadata/{id} | Get by UUID |
| GET | /api/v1/metadata/key/{key} | Get by key |
| POST | /api/v1/metadata | Create |
| PUT | /api/v1/metadata/{id} | Update (bumps version) |
| DELETE | /api/v1/metadata/{id} | Delete |
| GET | /api/v1/metadata/tenant/info | Current tenant debug info |

### Query params for GET /api/v1/metadata
| Param | Type | Default | Description |
|-------|------|---------|-------------|
| category | string | - | Filter by category |
| status | enum | - | ACTIVE / INACTIVE / DEPRECATED |
| search | string | - | Full-text search on key + description |
| page | int | 0 | Page number |
| size | int | 20 | Page size (max 100) |
| sortBy | string | createdAt | Field to sort by |
| sortDir | string | desc | asc or desc |

## Adding a New Tenant

1. Add the schema to `init.sql`
2. Add the tenant ID to `multitenancy.tenants` in `application.yml`
3. Restart — Flyway migrates the new schema automatically

## Metrics (Prometheus)
```
GET /actuator/prometheus
```
