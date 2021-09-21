# micronaut — Micronaut HTTP + Hibernate

Same book domain as a **JSON REST API**. Micronaut HTTP server + Hibernate/JPA + H2.

| Piece | Tech |
|---|---|
| Runtime | **Micronaut 5.2** |
| HTTP | **micronaut-http-server-netty** |
| Persistence | **micronaut-hibernate-jpa** (EntityManager) |
| DI | `@Singleton` / constructor inject |
| DB | H2 in-memory |

## Run

```bash
mvn mn:run
# or: mvn package -DskipTests && java -jar target/micronaut-0.0.1-SNAPSHOT-all.jar
# → http://localhost:22261/
```

Port is `micronaut.server.port` in `application.yml` (random free port for this demo).

## API

| Method | Path | What |
|---|---|---|
| `GET` | `/books` | list all |
| `GET` | `/books/{isbn}` | one book (404 if missing) |
| `POST` | `/books` | create (201; 409 if ISBN exists) |
| `GET` | `/hello` | plain-text hello |

### Examples

```bash
curl http://localhost:22261/books
curl http://localhost:22261/books/ISBN-1
curl -X POST http://localhost:22261/books \
  -H 'content-type: application/json' \
  -d '{"isbn":"ISBN-9","name":"Book 9","author":"Author 9"}'
```
