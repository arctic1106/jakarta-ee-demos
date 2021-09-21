# quarkus — Quarkus REST + Hibernate ORM Panache

Same book domain, exposed as a **JSON REST API** (no JSP). H2 in-memory.

| Piece | Tech |
|---|---|
| Runtime | **Quarkus 3.40** |
| HTTP | **quarkus-rest** (RESTEasy Reactive) |
| Persistence | **Hibernate ORM + Panache** |
| DB | H2 (in-memory) |
| Validation | Bean Validation via `@Valid` |

## Run

```bash
mvn quarkus:dev
# → http://localhost:21054/
```

Port is `quarkus.http.port` in `application.properties` (random free port for this demo).

## API

| Method | Path | What |
|---|---|---|
| `GET` | `/books` | list all |
| `GET` | `/books/{isbn}` | one book (404 if missing) |
| `POST` | `/books` | create (201; 409 if ISBN exists) |
| `GET` | `/hello` | plain-text hello |

### Examples

```bash
curl http://localhost:21054/books
curl http://localhost:21054/books/ISBN-1
curl -X POST http://localhost:21054/books \
  -H 'content-type: application/json' \
  -d '{"isbn":"ISBN-9","name":"Book 9","author":"Author 9"}'
```

Seeded via `import.sql` (same 4 demo books as the other stacks).
