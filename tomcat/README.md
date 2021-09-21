# tomcat — Jakarta Servlet 6.1 + JSP (no Spring)

Pure Jakarta EE web stack on **Tomcat 11**. Same book demo as `../` (Spring Boot), zero Spring annotations.

| Piece | Tech |
|---|---|
| Container | Tomcat 11 (Servlet 6.1) |
| Views | JSP 3.1 + JSTL 3.x |
| App | `@WebServlet` + `HttpServlet` |
| DI | none (plain `new`) |

## Run

```bash
mvn package cargo:run
# → http://localhost:8083/tomcat/
```

Or drop `target/tomcat.war` into a Tomcat 11 `webapps/` folder.

## Endpoints

| Path | What |
|---|---|
| `/index.html` | home |
| `/viewBooks` | list books (JSP) |
| `/enterAddBook` | add form (JSP) |
| `/addBook` | POST → redirect list |
| `/servlet` | plain servlet hello |
