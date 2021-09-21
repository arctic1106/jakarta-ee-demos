# Jakarta EE 11 Migration Notes

Namespace change: `javax.*` → `jakarta.*`

## Import Mapping

| Old (EE 8/9) | New (EE 11) |
|---|---|
| `javax.ws.rs.*` | `jakarta.ws.rs.*` |
| `javax.inject.*` | `jakarta.inject.*` |
| `javax.enterprise.*` | `jakarta.enterprise.*` |
| `javax.mvc.*` | `jakarta.mvc.*` |
| `javax.persistence.*` | `jakarta.persistence.*` |
| `javax.validation.*` | `jakarta.validation.*` |
| `javax.annotation.*` | `jakarta.annotation.*` |
| `javax.ejb.*` | `jakarta.ejb.*` |
| `javax.servlet.*` | `jakarta.servlet.*` |

## MVC API Breaking Changes

| MVC 2.x (EE 10) | MVC 3.0 (EE 11) |
|---|---|
| `ParamError.getParamName()` | Same |
| `ParamError.getMessage()` | Same |
| `javax.mvc.binding.ParamError` | `jakarta.mvc.binding.ParamError` |
| `RedirectScoped` | Same (new package) |
| `MvcBinding` | Same (new package) |
| `CsrfProtected` | Same (new package) |

No API-level breaking changes between MVC 2.x and 3.0 — only the namespace migration.

## XML Namespace Mapping

### web.xml
```xml
<!-- Old -->
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="4.0">
<!-- New -->
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" version="6.0">
```

### persistence.xml
```xml
<!-- Old -->
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.2">
<!-- New -->
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.2">
```

### beans.xml
```xml
<!-- Old -->
<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="2.0">
<!-- New -->
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee" version="4.0">
```

### XHTML (Facelets)
```xml
<!-- Old -->
xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
xmlns:f="http://xmlns.jcp.org/jsf/core"
xmlns:h="http://xmlns.jcp.org/jsf/html"
xmlns:c="http://java.sun.com/jsp/jstl/core"
<!-- New -->
xmlns:ui="jakarta.faces.facelets"
xmlns:f="jakarta.faces.core"
xmlns:h="jakarta.faces.html"
xmlns:c="jakarta.tags.core"
```

## JPA Changes

- `@GeneratedValue(strategy = GenerationType.AUTO)` → consider `IDENTITY` or `SEQUENCE`
- `javax.persistence.schema-generation.database.action` → `jakarta.persistence.schema-generation.database.action`
- Metamodel classes (e.g. `Task_`) may need regeneration
