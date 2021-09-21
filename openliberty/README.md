# openliberty — Jakarta EE 11 on Open Liberty

Full **Jakarta EE 11** profile (`jakartaee-11.0`) on Open Liberty. CDI + Servlet + JSP. No Spring.

| Piece | Tech |
|---|---|
| Runtime | **Open Liberty** |
| Profile | **Jakarta EE 11** (`jakartaee-11.0`) |
| DI | **CDI** `@Inject` / `@ApplicationScoped` |
| Web | Servlet 6.1 `@WebServlet` |
| Views | JSP + JSTL |

## Run

```bash
mvn liberty:dev
# → http://localhost:20493/openliberty/
```

Port is set in `pom.xml` (`liberty.var.http.port`) — change it if it clashes.

Hot reload on save. Stop with `Ctrl+C`.

## Endpoints

| Path | What |
|---|---|
| `/index.html` | home |
| `/viewBooks` | list books (JSP) |
| `/enterAddBook` | add form |
| `/addBook` | POST → redirect list |
| `/servlet` | plain servlet hello |
