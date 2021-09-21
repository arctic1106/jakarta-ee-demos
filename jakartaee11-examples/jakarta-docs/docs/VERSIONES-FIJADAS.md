# Fuentes Oficiales y Versiones Verificadas — Junio 2026

---

## 1. Fuentes Oficiales Jakarta EE

| Recurso | URL |
|---|---|
| Jakarta EE Platform | https://jakarta.ee/specifications/platform/11/ |
| Jakarta EE Web Profile | https://jakarta.ee/specifications/webprofile/11/ |
| Jakarta EE Core Profile | https://jakarta.ee/specifications/coreprofile/11/ |
| Todas las especificaciones | https://jakarta.ee/specifications/ |
| Productos compatibles | https://jakarta.ee/committees/compatibility/ |
| MVC 3.0 | https://jakarta.ee/specifications/mvc/3.0/ |

---

## 2. Fuentes Oficiales de Servidores

| Servidor | URL Oficial | Última Versión (web) | EE 11 |
|---|---|---|---|
| **Open Liberty** | https://openliberty.io/ | **26.0.0.5** (May 2026) | ✅ |
| **WildFly** | https://wildfly.org/ | **40.0.0.Final** (May 2026) | ✅ |
| **GlassFish** | https://glassfish.org/ | **8.0.3** (Jun 2026) | ✅ |
| **Payara** | https://www.payara.fish/ | **7.2026.5** (EE 11 GA) | ✅ |

> **Discrepancia Maven Central vs Web oficial**:
> - Liberty 26.x NO está en Maven Central (solo hasta 25.0.0.6). Se descarga del repositorio de OpenLiberty.
> - GlassFish 8.0.3 NO está en Maven Central (solo hasta 8.0.0-M12). Se descarga desde glassfish.org.
> - WildFly 40 se descarga desde wildfly.org/downloads.

---

## 3. Jakarta EE 11 — APIs y Versiones (verificadas en Maven Central)

| Especificación | GroupId:ArtifactId | Versión |
|---|---|---|
| **Platform (umbrella)** | `jakarta.platform:jakarta.jakartaee-api` | **11.0.0** |
| **Servlet** | `jakarta.servlet:jakarta.servlet-api` | **6.1.0** |
| **Faces (JSF)** | `jakarta.faces:jakarta.faces-api` | **4.1.2** |
| **CDI** | `jakarta.enterprise:jakarta.enterprise.cdi-api` | **4.1.0** |
| **JAX-RS** | `jakarta.ws.rs:jakarta.ws.rs-api` | **4.0.0** |
| **JPA** | `jakarta.persistence:jakarta.persistence-api` | **3.2.0** |
| **Bean Validation** | `jakarta.validation:jakarta.validation-api` | **3.1.1** |
| **EJB** | `jakarta.ejb:jakarta.ejb-api` | **4.0.1** |
| **EL** | `jakarta.el:jakarta.el-api` | **6.0.1** |
| **Annotations** | `jakarta.annotation:jakarta.annotation-api` | **3.0.0** |
| **Interceptors** | `jakarta.interceptor:jakarta.interceptor-api` | **2.2.0** |
| **JTA** | `jakarta.transaction:jakarta.transaction-api` | **2.0.1** |
| **JSON-P** | `jakarta.json:jakarta.json-api` | **2.1.3** |
| **JSON-B** | `jakarta.json.bind:jakarta.json.bind-api` | **3.0.1** |
| **WebSocket** | `jakarta.websocket:jakarta.websocket-api` | **2.2.0** |
| **Mail** | `jakarta.mail:jakarta.mail-api` | **2.1.3** |
| **JMS** | `jakarta.jms:jakarta.jms-api` | **3.1.0** |
| **MVC** | `jakarta.mvc:jakarta.mvc-api` | **3.0.0** |

> **Nota**: `jakarta.jakartaee-api:11.0.0` es un umbrella JAR que incluye la mayoría.
> MVC API **NO** está incluida — declarar por separado.

---

## 4. Jakarta EE 11 — Profiles

### Full Platform (jakartaee-11.0)
Incluye **todas** las especificaciones Jakarta EE:

| Categoría | Especificaciones |
|---|---|
| Web | Servlet 6.1, Faces 4.1, JSP 4.0, WebSocket 2.2, JSON-P 2.1, JSON-B 3.0 |
| Web Services | JAX-RS 4.0, JAXB 4.0, JAX-WS 4.0, SOAP with Attachments 3.0, RESTful WS 4.0 |
| Core | CDI 4.1, EL 6.0, Annotations 3.0, Interceptors 2.2, Dependency Injection 2.0 |
| Persistence | JPA 3.2, Criteria API 3.2 |
| Transactions | JTA 2.0 |
| Messaging | JMS 3.1, MDB 4.0 |
| Security | Security API 3.0, Authorization 2.1, Authentication 3.0 |
| Enterprise | EJB 4.0, Bean Validation 3.1, Concurrency 3.0, Batch 2.1 |
| Management | JMX 2.0, Connectors 2.1 |
| Other | Mail 2.1, Persistence Events 3.2, XML Binding 4.0, XML WS 4.0 |

### Web Profile (webProfile-11.0)
Subconjunto para aplicaciones web. **Incluye**:

| Categoría | Especificaciones |
|---|---|
| Web | Servlet 6.1, Faces 4.1, JSP 4.0, WebSocket 2.2, JSON-P 2.1, JSON-B 3.0 |
| REST | JAX-RS 4.0 |
| Core | CDI 4.1, EL 6.0, Annotations 3.0, Interceptors 2.2, DI 2.0 |
| Persistence | JPA 3.2, Criteria API 3.2 |
| Transactions | JTA 2.0 |
| Security | Security API 3.0, Authorization 2.1, Authentication 3.0 |
| Enterprise | EJB 4.0 (lite), Bean Validation 3.1, Concurrency 3.0 |
| Other | Mail 2.1 |

**NO incluye**: JMS, MDB, JAX-WS, JAXB, Batch, Connectors, JMX.

### Core Profile (coreProfile-11.0)
Mínimo para microservimientos. **Incluye**:

| Categoría | Especificaciones |
|---|---|
| Web | Servlet 6.1, JSON-P 2.1, JSON-B 3.0 |
| REST | JAX-RS 4.0 |
| Core | CDI 4.1, EL 6.0, Annotations 3.0, Interceptors 2.2, DI 2.0 |
| Persistence | JPA 3.2 (sin criteria) |
| Transactions | JTA 2.0 |
| Enterprise | Bean Validation 3.1 |

**NO incluye**: Faces, JSP, WebSocket, JMS, EJB, Mail, Security, Batch, JAX-WS, JAXB.

---

## 5. Implementaciones (Maven Central)

| Componente | GroupId:ArtifactId | Versión | Notas |
|---|---|---|---|
| **Krazo Core** | `org.eclipse.krazo:krazo-core` | **4.0.0** | MVC 3.0 RI |
| **Krazo Jersey** | `org.eclipse.krazo:krazo-jersey` | **4.0.0** | Para GlassFish/Liberty |
| **Krazo RESTEasy** | `org.eclipse.krazo:krazo-resteasy` | **4.0.0** | Para WildFly |
| **Mojarra** | `org.glassfish:jakarta.faces` | **4.1.4** | Faces impl |
| **EclipseLink** | `org.eclipse.persistence:eclipselink` | **4.0.8** | JPA impl (⚠️ no soporta Java 25) |
| **H2 Database** | `com.h2database:h2` | **2.4.240** | Embedded/in-memory DB |

---

## 6. Maven Plugins

| Plugin | GroupId:ArtifactId | Versión | Para |
|---|---|---|---|
| **Liberty** | `io.openliberty.tools:liberty-maven-plugin` | **3.11.4** | Open Liberty |
| **WildFly** | `org.wildfly.plugins:wildfly-maven-plugin` | **6.0.0.Final** | WildFly |
| **Cargo** | `org.codehaus.cargo:cargo-maven2-plugin` | **3.2.1** | GlassFish |
| **Compiler** | `org.apache.maven.plugins:maven-compiler-plugin` | **3.13.0** | Java 25 |
| **WAR** | `org.apache.maven.plugins:maven-war-plugin` | **3.4.0** | Packaging |

---

## 7. Java

| Requisito | Versión |
|---|---|
| Compilación del proyecto | **Java 25** (`<release>25</release>`) |
| GlassFish 8.x | JDK 21+ (soporta 25) |
| WildFly 40 | JDK 21+ (soporta 25) |
| Liberty 26.x | JDK 21+ (soporta 25) |

---

## 8. Dependencias para este proyecto (pom.xml)

```xml
<properties>
    <maven.compiler.release>25</maven.compiler.release>
    <jakartaee-api.version>11.0.0</jakartaee-api.version>
    <krazo.version>4.0.0</krazo.version>
    <faces.version>4.1.4</faces.version>
    <liberty-maven-plugin.version>3.11.4</liberty-maven-plugin.version>
    <wildfly-maven-plugin.version>5.1.2.Final</wildfly-maven-plugin.version>
    <cargo-maven2-plugin.version>3.2.1</cargo-maven2-plugin.version>
</properties>

<dependencies>
    <!-- Jakarta EE 11 API (umbrella) -->
    <dependency>
        <groupId>jakarta.platform</groupId>
        <artifactId>jakarta.jakartaee-api</artifactId>
        <version>${jakartaee-api.version}</version>
        <scope>provided</scope>
    </dependency>

    <!-- Jakarta MVC API (NOT in umbrella) -->
    <dependency>
        <groupId>jakarta.mvc</groupId>
        <artifactId>jakarta.mvc-api</artifactId>
        <version>3.0.0</version>
    </dependency>

    <!-- Eclipse Krazo (MVC Implementation) -->
    <dependency>
        <groupId>org.eclipse.krazo</groupId>
        <artifactId>krazo-core</artifactId>
        <version>${krazo.version}</version>
    </dependency>
    <dependency>
        <groupId>org.eclipse.krazo</groupId>
        <artifactId>krazo-jersey</artifactId>
        <version>${krazo.version}</version>
    </dependency>

    <!-- H2 Database (embedded, in-memory) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.4.240</version>
    </dependency>

    <!-- Jakarta Faces (NO incluir si el server lo provee) -->
    <!--
    <dependency>
        <groupId>org.glassfish</groupId>
        <artifactId>jakarta.faces</artifactId>
        <version>${faces.version}</version>
    </dependency>
    -->
</dependencies>
```

---

## 9. Reglas de Verificación

1. **Primero DuckDuckGo**: `https://html.duckduckgo.com/html/?q=latest+version+product-name`
2. **Después Maven Central**: encontrar coordenadas Maven correctas
3. **Si no coincide** → documentar discrepancia
4. **Nunca asumir** que Maven Central tiene la última versión de un producto
