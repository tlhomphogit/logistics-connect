# Known Issues & Audit Log

Findings from the pre-Phase-3 project audit. Each item has an ID (`LC-0xx`) that is referenced from [ROADMAP.md](ROADMAP.md) (Phase 2.5).

**Status key:** Open · Fixed (resolved in the audit pass) · Planned (intentionally not built yet, tracked in the roadmap)

**Severity key:** High (can cause data loss, leaks, or wrong behaviour) · Medium (should fix before Phase 3) · Low (cleanup)

## Summary

| ID | Area | Severity | Status | Summary |
|----|------|----------|--------|---------|
| LC-001 | Shipment API | High | Open | Invalid input returns HTTP 500 instead of 400 |
| LC-002 | Testing | Medium | Open | No tests for 404, 409 or 400; roadmap claimed a 404 test |
| LC-003 | Shipment design | Low | Planned | No service layer; controller calls the repository directly |
| LC-004 | Shipment design | Low | Open | `status` is a free-form `String` |
| LC-005 | Shipment API | Medium | Open | Every `DataIntegrityViolationException` is reported as "duplicate" (409) |
| LC-006 | Config/Secrets | High | Open | Credentials duplicated; `.env` is not read by the app or Makefile |
| LC-007 | Config/Secrets | Medium | Open | Artemis credentials hardcoded (`admin/admin`) |
| LC-008 | Config/Secrets | Medium | Open | `.env` was committed once before being untracked |
| LC-009 | Config | Medium | Open | Datasource host, port and DB name hardcoded |
| LC-010 | Build | Medium | Fixed | notification-service still depended on the MySQL driver |
| LC-011 | Build | Medium | Open | Inconsistent web starter names between modules |
| LC-012 | Build | Low | Open | Parent POM manages no shared dependencies; new modules lack test starters |
| LC-013 | CI | Low | Open | CI only waits for Postgres and relies on the shared dev database setup |
| LC-014 | Docs | Medium | Fixed | ARCHITECTURE/README/ROADMAP drifted from the code |
| LC-015 | Infra | Low | Planned | No Dockerfiles yet; compose runs infrastructure only |
| LC-016 | Testing | High | Open | Integration tests run against the dev database and call `deleteAll()` |
| LC-017 | Config | Low | Open | Dev-only JPA settings applied everywhere |

## Details

### LC-001: Invalid input returns 500, not 400
`Shipment`'s constructor throws `IllegalArgumentException` for blank fields or a non-positive weight, and nothing handles it, so a bad `POST` surfaces as a server error. `null` fields (e.g. a missing JSON property) are also rejected this way.
**Fix:** test first, then handle the exception in `GlobalExceptionHandler` and return a 400 with the same `{error, message}` shape as the 409 response. Optionally add Bean Validation on `CreateShipmentRequest`.

### LC-002: Missing controller tests
Existing tests cover the two happy paths only. The roadmap item "assert HTTP 404 using `TestRestTemplate`" was ticked, but no such test exists (the suite uses `RestTestClient`).
**Fix:** add tests for `GET` unknown id (404), duplicate tracking number (409), and invalid payloads (400). The roadmap item has been reopened.

### LC-003: No service layer
`ShipmentController` uses `ShipmentRepository` directly, and the `service/` package described in the architecture does not exist. This is acceptable at the current size but should be introduced before more business rules arrive.

### LC-004: `status` is a `String`
Values are unchecked and there is no defined lifecycle. **Fix:** introduce a `ShipmentStatus` enum (store with `@Enumerated(EnumType.STRING)`).

### LC-005: Over-broad 409 handler
`GlobalExceptionHandler` maps *any* `DataIntegrityViolationException` to "A record with this unique identifier already exists", including `NOT NULL` violations. **Fix:** check `existsByTrackingNumber` before saving (and keep the handler as a race-condition safety net), or inspect the violated constraint.

### LC-006: Credentials duplicated; `.env` not wired to the app
`.env` is read only by Docker Compose. The Makefile hardcodes `DB`, `USER` and `PASSWORD`, and `application.properties` falls back to the same literal values (`${POSTGRES_USER:logistics_user}` etc.). Consequences:
* Spring and Maven never read `.env`, so the app silently uses the fallback defaults.
* The `.env` values (and the CI GitHub Secrets) **must equal those defaults** or tests fail with authentication errors.
* Changing a credential requires edits in several places.

**Fix:** load `.env` in the Makefile, read credentials from environment variables in `application.properties` without password defaults, and document required variables in `.env.example` (added).

### LC-007: Artemis credentials hardcoded
`compose.yaml` sets `ARTEMIS_USER=admin` and `ARTEMIS_PASSWORD=admin`. The old roadmap mentioned `ACTIVEMQ_PASSWORD` in `.env`, but nothing reads it. **Fix:** move to `.env` and reference from compose; use the same variables in the Phase 3 `application.yml` files.

### LC-008: `.env` in git history
The commit log shows "remove .env from remote tracking and ignore it". The file may still exist in earlier commits and remote history. It is now ignored and CI uses GitHub Secrets. **Fix:** inspect history (`git log --all -- .env`); if any value was a real secret, rotate it. For local-dev-only throwaway values this is low risk, but do not reuse them elsewhere. The uploaded zip also contained `.env`; do not share archives of the working directory.

### LC-009: Hardcoded datasource URL
`jdbc:postgresql://localhost:5433/logistics_db` ignores `POSTGRES_DB` and blocks running inside Docker. **Fix:** externalize host, port and database name with sensible defaults for local development.

### LC-010: MySQL driver in notification-service (Fixed)
The scaffold originally targeted MySQL; the project later migrated to PostgreSQL, but only shipment-service was updated. `notification-service/pom.xml` now uses `org.postgresql:postgresql` (runtime). No other service code existed to change.

### LC-011: Inconsistent starter names
shipment-service uses `spring-boot-starter-web`; tracking-service uses `spring-boot-starter-webmvc`. Spring Boot 4 renamed the web starter, so one is likely the legacy alias. **Fix:** check the Spring Boot 4.1 reference, pick one name, and use it in all modules.

### LC-012: No shared dependency management
The parent POM only defines modules, the Java version and two Docker profiles. `ARCHITECTURE.md` previously claimed it manages dependencies. tracking-service and notification-service have no test dependencies. **Fix:** add `dependencyManagement`/common test dependencies (including Testcontainers) to the parent, and add `spring-boot-starter-test` to the new modules.

### LC-013: CI assumptions
`make up` starts Postgres and Artemis but only waits for Postgres health. Tests currently need the dev-style database on port 5433 with default credentials (see LC-006). Once tests use Testcontainers (LC-016) the `make up` step can be dropped. Also consider uploading surefire reports when a build fails.

### LC-014: Documentation drift (Fixed)
Docs listed files that do not exist yet without saying so, showed the service modules nested under `.mvn/` because of tree indentation, described MySQL/PostgreSQL inconsistently, and marked a 404 test as done. `ARCHITECTURE.md`, `README.md` and `ROADMAP.md` were rewritten to separate implemented from planned work.

### LC-015: No Dockerfiles yet
The tree describes a `Dockerfile` per service; none exist. This is intentional and scheduled with each service in Phase 3. `compose.yaml` currently starts infrastructure only.

### LC-016: Tests hit the dev database
`ShipmentControllerTest` calls `repository.deleteAll()` in `@BeforeEach`, and the tests use the same datasource as local development (`localhost:5433/logistics_db`). Running `make test` wipes local dev data. The roadmap's testing methodology calls for Testcontainers, but shipment-service does not use it yet.
**Fix:** Testcontainers PostgreSQL with `@ServiceConnection`, or at minimum a dedicated test database/profile.

### LC-017: Dev-only JPA settings everywhere
`ddl-auto=update` and `show-sql=true` are set globally, and an explicit Hibernate dialect is configured (Hibernate detects PostgreSQL automatically). **Fix:** move dev settings to a profile, and adopt Flyway later (see roadmap backlog).
