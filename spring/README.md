# spring — Spring MVC 7 on Tomcat 11 (no Boot)

Classic Spring MVC WAR: `DispatcherServlet` + `@Controller` + JSP. No Spring Boot.

| Piece | Tech |
|---|---|
| Framework | Spring MVC **7.0.9** |
| Container | Tomcat 11 (Servlet 6.1) |
| Views | JSP + Spring form tags + JSTL |
| Config | `WebAppInitializer` + `@EnableWebMvc` (no web.xml) |

## Run

```bash
mvn package cargo:run
# → http://localhost:8084/spring/
```

Or drop `target/spring.war` into Tomcat 11 `webapps/`.

## Endpoints

| Path | What |
|---|---|
| `/index.html` | home |
| `/viewBooks` | list books (JSP) |
| `/enterAddBook` | add form (Spring form tags) |
| `/addBook` | POST → redirect list |
| `/servlet` | plain `@WebServlet` hello |
