# Jakarta EE 11 — Version Matrix

All versions verified against Maven Central (Jun 2026).

## Platform & Specs

| Spec | Artifact | Version |
|---|---|---|
| Platform (umbrella) | `jakarta.platform:jakarta.jakartaee-api` | **11.0.0** |
| Servlets | `jakarta.servlet:jakarta.servlet-api` | **6.1.0** |
| JSP / Facelets | `jakarta.faces:jakarta.faces-api` | **4.1.2** |
| JPA | `jakarta.persistence:jakarta.persistence-api` | **3.2.0** |
| EJB | `jakarta.ejb:jakarta.ejb-api` | **4.0.1** |
| CDI | `jakarta.enterprise:jakarta.enterprise.cdi-api` | **4.1.0** |
| Bean Validation | `jakarta.validation:jakarta.validation-api` | **3.1.1** |
| JAX-RS | `jakarta.ws.rs:jakarta.ws.rs-api` | **4.0.0** |
| MVC | `jakarta.mvc:jakarta.mvc-api` | **3.0.0** |
| EL | `jakarta.el:jakarta.el-api` | **6.0.1** |
| Annotations | `jakarta.annotation:jakarta.annotation-api` | **3.0.0** |
| Interceptors | `jakarta.interceptor:jakarta.interceptor-api` | **2.2.0** |
| JTA | `jakarta.transaction:jakarta.transaction-api` | **2.0.1** |
| JSON-P | `jakarta.json:jakarta.json-api` | **2.1.3** |
| JSON-B | `jakarta.json.bind:jakarta.json.bind-api` | **3.0.1** |
| WebSocket | `jakarta.websocket:jakarta.websocket-api` | **2.2.0** |
| Mail | `jakarta.mail:jakarta.mail-api` | **2.1.3** |
| JMS | `jakarta.jms:jakarta.jms-api` | **3.1.0** |

> The umbrella JAR `jakarta.jakartaee-api:11.0.0` bundles all core specs.
> MVC API is **not** in the umbrella — declare it separately.

## Krazo (MVC RI)

| Module | Version | Use with |
|---|---|---|
| `krazo-core` | **4.0.0** | Always required |
| `krazo-jersey` | **4.0.0** | GlassFish, Payara, Liberty |
| `krazo-resteasy` | **4.0.0** | WildFly |
| `krazo-cxf` | ~~2.0.2~~ | Dropped in 4.x (no EE 11) |

| Krazo | MVC API | Jakarta EE |
|---|---|---|
| 1.x | 1.x | EE 8 |
| 2.x | 2.0 | EE 9 |
| 3.x | 2.1 | EE 10 |
| **4.x** | **3.0** | **EE 11** |

## Servers

| Server | Docs | Latest on Maven Central | EE Level | Maven Plugin |
|---|---|---|---|---|
| **Open Liberty** | https://openliberty.io/docs/ | 25.x (external download) | EE 10 ⚠️ | `liberty-maven-plugin:3.11.4` |
| **WildFly** | https://docs.wildfly.org/ | 36.x (external download) | EE 11 ✅ | `wildfly-maven-plugin:5.1.2.Final` |
| **GlassFish** | https://glassfish.org/documentation.html | **7.0.25** (EE 10), **8.0.0-M12** (EE 11 milestone) | EE 10 ⚠️ | `cargo-maven2-plugin:3.2.1` |
| **Payara** | https://docs.payara.fish/ | **6.2025.5** (EE 10), **7.2025.1.Alpha1** (EE 11 alpha) | EE 10 ⚠️ | `cargo-maven2-plugin:3.2.1` |
| **TomEE** | https://tomee.apache.org/ | 9.x (EE 10) | EE 10 ❌ | — |

> **⚠️ Versioning warning**: GlassFish 8.0.3 appears on the official docs page as "Current Release" but does NOT exist on Maven Central. The latest Maven Central artifact is `8.0.0-JDK17-M12` (milestone). Always verify against Maven Central.

## Liberty server.xml

```xml
<featureManager>
    <feature>jakartaee-11.0</feature>
</featureManager>
```

## Compiler

Java **25** required. Use `<release>25</release>` (not source/target).
