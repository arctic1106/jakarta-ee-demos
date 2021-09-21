# Cargo Tracker on Jakarta EE 11 / Open Liberty 26.0.0.10 — Findings & Status

Session date: 2026-10-21. Runtime verified: Open Liberty **26.0.0.10**, Java **25**,
`liberty-maven-plugin` **3.12.1**, features `webProfile-11.0` + `microProfile-7.1`.

Every item under "Gotchas" was reproduced on the running server, not inferred.
Use this file as context when asking an AI to write Jakarta EE 11 code for Liberty.

---

## 1. Status

| Area | State |
|---|---|
| Build (`mvn package`) | ✅ compiles |
| App starts on Liberty | ✅ no errors (only harmless first-start DDL drop warnings) |
| All Faces pages render (GET) | ✅ verified with curl |
| Routing page shows candidate routes | ✅ verified (4 routes for DEF789) |
| REST API + security + validation | ✅ verified (`api-smoke` checks, see §5) |
| Audit trail (Jakarta Data record entity) | ✅ verified, entries written after commit |
| Scheduled overdue monitor | ✅ verified ("Overdue cargo: 0" logged each minute) |
| Health `/health/ready`, `/health/live`, OpenAPI `/openapi` | ✅ verified |
| Booking flow end-to-end in browser after fixes | ⚠️ **not re-verified** (fixed in code, last browser run was before the fix) |
| Assign route → show page (POST in browser) | ⚠️ **not re-verified** |
| Live map (SSE) and WebSocket push after a change | ⚠️ **not re-verified** |
| Event logger / change destination / change deadline submits | ⚠️ **not re-verified** |
| Unit tests | ❌ JUnit dependency added, **no tests written yet** |
| `VERSIONS.md` contradictions (says Liberty is EE 10, plugin 3.11.4) | ❌ not fixed yet — see §4 |
| Nothing committed | ❌ all changes are uncommitted in the working tree |

Run it: `mvn package liberty:run` → http://localhost:9080/
Demo users (dev only): `handler/handler` (role handler), `admin/admin` (roles admin, handler).

---

## 2. Bugs found in the original sample (fixed)

| # | Bug | Symptom | Fix |
|---|---|---|---|
| 1 | `<f:metadata>` placed in the **template** (`admin.xhtml`) and/or outside `<ui:define>` in clients (`route.xhtml`, `show.xhtml`) | View params and view actions never ran: cargo details page empty, **"No routes found" always** | Template has a bare `<ui:insert name="metadata"/>`; clients put `<f:metadata>` *inside* `<ui:define name="metadata">` |
| 2 | `selectItinerary.xhtml` duplicated `route.xhtml` without any route markup | Reroute link led to an empty page | Deleted; links point to `route.xhtml` |
| 3 | Shared static `Itinerary.EMPTY_ITINERARY` with `legs == null` | `getLegs()` NPE on booking; worked only because JPA had mutated the shared constant | Per-instance `Itinerary.empty()`, legs never null, `orphanRemoval` |
| 4 | `Cargo` constructor computed `Delivery` before setting the itinerary | Delivery derived from `null` itinerary | Order fixed |
| 5 | Shared static `HandlingActivity NO_ACTIVITY`, `Location.UNKNOWN` / `Voyage.NONE` as entity field defaults | Transient entities assigned to managed relationships | Removed from entity fields (kept only for display) |
| 6 | `Delivery.equals` called `.equals` on nullable fields | NPE when ETA / last event is null | `Objects.equals` |
| 7 | `Location` / `Voyage` equality used generated id + all fields (incl. lazy collection) | Transient vs managed copies unequal; expensive hashCode | Natural keys (UN/LOCODE, voyage number) |
| 8 | Booking flow never exited (`TODO` in `booking-flow.xml`), returned a view id | `@FlowScoped` bean leaked, URL stuck on flow page | Return `returnFromBookingFlow` (flow-return) + kept `FacesMessage` |
| 9 | Tracking view: last known location `null` before first event | NPE on tracking page | Null-safe adapter |
| 10 | Tracking text "In port X" used the **destination** | Wrong location shown | Uses last known location |
| 11 | `Event.fireAsync()` inside the transaction | SSE clients notified about changes that may roll back | Sync event + `@Observes(during = AFTER_SUCCESS)` → virtual-thread executor |
| 12 | `ApplicationSettingsRepository` with `dataStore = "jdbc/..."` for a JPA entity | Liberty builds a hidden 2nd persistence unit, ignoring `persistence.xml` | Removed; JPA-entity repositories use a `@PersistenceUnit` reference |
| 13 | REST errors (unknown cargo, missing voyage) | Generic 500 | `ExceptionMapper` → RFC 9457 problem JSON (404/400/409) |
| 14 | `initital-data.sql` typo, EclipseLink `FINEST` logging, dead MP config keys | Noise / confusion | Renamed, `INFO`, real config keys |
| 15 | Unpinned Liberty runtime | Builds could change server version silently | `runtimeArtifact` pinned to 26.0.0.10 |

---

## 3. Jakarta EE 11 / Open Liberty gotchas (verified — the things AI gets wrong)

### Jakarta Data (data-1.0)
1. **`dataStore` for JPA entities must be a persistence-unit reference**, declared on the repository interface:
   ```java
   @PersistenceUnit(name = "java:app/env/persistence/CargoTrackerUnitRef", unitName = "CargoTrackerUnit")
   @Repository(dataStore = "java:app/env/persistence/CargoTrackerUnitRef")
   ```
   `dataStore = "jdbc/MyDS"` makes Liberty create its own hidden persistence unit and tables.
2. **Repository beans are not passivation capable.** Injecting one into `@ViewScoped`/`@FlowScoped`/`@SessionScoped` fails deployment with `WELD-001413`. Wrap repositories in an `@ApplicationScoped` service (`ReferenceDataService`).
3. **Repositories use their own EntityManager.** Rows persisted through your `@PersistenceContext` in the same transaction are invisible to repository queries until you `flush()`.
4. **Java records as entities** work only with a **DataSource** `dataStore` (not a PU reference). Liberty generates `<Record>Entity`.
5. **Record entity with `UUID` id → `CREATE TABLE ... ID NUMBER(16)`**, rejected by HSQLDB. The failure is logged **only at trace level** (`eclipselink=all`, category `eclipselink.ps.ddl`); the first query then fails with "object not found". Use a `String` id.
6. JDQL works as documented: `@Query("where unLoCode.code = :code")`, `select count(this) where ...`, `@Find @OrderBy("id") Page<Cargo> findAll(PageRequest)`. Pages are **1-based**. Default methods in repositories are fine.
7. `<data createTables="true"/>` in `server.xml` applies only to DataSource-backed repositories.

### Security (appSecurity-6.0 / Jakarta Security 4.0)
8. `@InMemoryIdentityStoreDefinition` is **ignored unless** `<webAppSecurity allowInMemoryIdentityStores="true"/>` is in `server.xml` (result: 401 with correct credentials).
9. Liberty needs a **keystore password** to generate LTPA keys (`CWWKS4118E`). Without it, valid logins fail with `403 AuthenticationFailed`. A fresh server from the Maven plugin had no `server.env`; set `<liberty.env.keystore_password>` in the pom (dev only) or inject a secret.
10. Groups map to roles of the same name by default; no `application-bnd` needed.

### Server features
11. `jakartaee-11.0` starts an **ORB** (remote EJB) that needs a user registry (`CWWKS9660E`) **and** an SSL keystore (`CWWKS9582E`) or "no application will start". Use `webProfile-11.0` unless you need JMS/Batch/Mail/remote EJB.
12. `webProfile-11.0` on 26.0.0.10 includes: appSecurity-6.0, beanValidation-3.1, concurrent-3.1, **data-1.0**, enterpriseBeansLite-4.0, faces-4.1, jsonb-3.0, jsonp-2.1, pages-4.0, persistence-3.2, restfulWS-4.0, servlet-6.1, websocket-2.2 (read from the feature manifest).
13. MicroProfile 7.x has **no `/metrics`** (Telemetry replaced Metrics).
14. Open Liberty 26.0.0.10 **does** support Jakarta EE 11 — `VERSIONS.md` saying "EE 10 ⚠️" is wrong.

### Faces 4.1
15. `<f:metadata>` must be in the template client inside `<ui:define>`, never in the template file.
16. Leave a Faces Flow via its `<flow-return>` outcome, not a plain view id.
17. `faces-redirect=true` on `h:link` / `h:button` outcomes is meaningless (they are GETs).

### CDI 4.1 / Concurrency 3.1
18. `@Observes Startup` (CDI 4) replaces `@Observes @Initialized(ApplicationScoped.class)`.
19. Virtual threads + CDI injection: `@ManagedExecutorDefinition(name=..., qualifiers = VirtualThreads.class, virtual = true)` then `@Inject @VirtualThreads ManagedExecutorService`.
20. Scheduled CDI method (replaces EJB timer): `@Asynchronous(executor = "java:comp/DefaultManagedScheduledExecutorService", runAt = @Schedule(cron = "* * * * *")) void check(@Observes Startup s)`. 5-field cron. Never let an exception escape.
21. Use transactional observers (`AFTER_SUCCESS`) for side effects; `fireAsync` does not wait for commit.

### MicroProfile OpenAPI
22. Declaring any `@APIResponse` replaces the generated default; add the 200 explicitly (otherwise `CWWKO1651W`).

### Dev workflow
23. `liberty:dev` exits immediately when stdin is not interactive (CI, background shells); use `liberty:run` there.
24. `drop-and-create` on a fresh in-memory DB logs `EclipseLink-4002 ... DROP CONSTRAINT` warnings on first start — harmless.

---

## 4. Documentation issues in `jakarta-docs/docs` (not yet fixed)

- `VERSIONS.md`: Liberty listed as "EE 10 ⚠️" and plugin `3.11.4`; verified EE 11 on 26.0.0.10 with plugin 3.12.1.
- `AI.md`, `PROMPTING.md`, `ARCHITECTURE.md` cite Liberty **26.0.0.5**; runtime used is 26.0.0.10.
- `MIGRATION.md` shows `web.xml` 6.0 / `beans.xml` 4.0 as "EE 11"; EE 11 is `web-app 6.1`, `beans 4.1`.
- `ARCHITECTURE.md` / `AI.md` describe a scraping/Pro-tier platform that does not exist in this repo; they give the AI no code-level guidance. This file is meant to fill that gap.

---

## 5. What was added (EE 11 showcase)

| Feature | Where |
|---|---|
| Jakarta Data repositories (JDQL, paging, counts, default methods) | `repository/LocationRepository`, `VoyageRepository`, `CargoQueries` |
| Jakarta Data record entity | `audit/AuditEntry`, `AuditLog`, `AuditRecorder` |
| Concurrency 3.1 virtual threads + qualifier injection | `config/ConcurrencyConfig`, `VirtualThreads` |
| Concurrency 3.1 scheduled `@Asynchronous` | `service/OverdueCargoMonitor` |
| Jakarta Security 4.0 in-memory store + Basic, `@RolesAllowed` | `config/SecurityConfig`, `rest/HandlingReportService`, `rest/ShipmentResource` |
| Records as REST DTOs with Bean Validation (incl. cross-field `@AssertTrue`) | `rest/dto/*` |
| RFC 9457 problem responses | `rest/DomainExceptionMapper` |
| Transactional CDI observers → SSE + WebSocket push | `service/CargoUpdateNotifier`, `websocket/*` |
| MP Fault Tolerance (`@Timeout`, `@Fallback`), MP Config, OpenAPI, real health checks | `service/RoutingService`, `GraphTraversalService`, `microprofile/*`, `rest/RestApplication` |
| `@Version` optimistic locking, selective 2nd-level cache | `Cargo`, `Location`, `Voyage`, `persistence.xml` |
| Dashboard stat tiles, audit trail on cargo page | `admin/dashboard.xhtml`, `admin/show.xhtml` |

New REST endpoints: `GET /rest/shipments?page=&size=`, `GET /rest/shipments/{id}`,
`GET /rest/shipments/{id}/audit`, `POST /rest/shipments` (admin),
`POST /rest/handling/reports` (handler, ISO-8601 `completionTime`, enum `eventType` — **breaking change** from the old `M/d/yyyy HH:mm` string format).

Verified API results (fresh server):
401 no/bad auth · 400 invalid body · 404 unknown cargo · 400 LOAD without voyage · 202 valid LOAD (status → ONBOARD_CARRIER, audit entry written) · 403 booking as handler · 400 same origin/destination · 400 past deadline · 201 booking with `Location` header · 400 page size > 100.

---

## 6. Next steps

1. Re-verify in the browser: booking flow, assign route, change destination/deadline, event logger, live map (SSE), WebSocket.
2. Write JUnit tests for the domain model (`Itinerary`, `RouteSpecification.isSatisfiedBy`, `Delivery` status calculation).
3. Fix the doc contradictions listed in §4 and add a module README.
4. Review the diff and commit.
