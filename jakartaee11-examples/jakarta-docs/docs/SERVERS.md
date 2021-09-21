# Server Configuration Guide

Complete reference for running this Jakarta EE MVC application on different servers.

---

## Quick Reference

| Server | Version | EE Level | JPA Provider | Java 25 | Maven Plugin | Dev Mode |
|---|---|---|---|---|---|---|
| **GlassFish** | 8.0.3 | EE 11 | EclipseLink 5.0.0 | ✅ | Cargo | ❌ |
| **Payara** | 7.2026.5 | EE 11 | EclipseLink | ✅ | Cargo | ❌ |
| **WildFly** | 40.0.0.Final | EE 11 | Hibernate ORM | ⚠️ unverified | wildfly-maven-plugin 6.0.0.Final | ✅ `wildfly:dev` |
| **Open Liberty** | 26.0.0.5 | EE 10 | EclipseLink 4.0.6 | ⚠️ | liberty-maven-plugin 3.11.4 | ✅ `liberty:dev` |

---

## Persistence Configuration (persistence.xml)

`META-INF/persistence.xml` is the JPA configuration file bundled inside your WAR. It defines persistence units, JPA provider, database connection, and schema generation strategy. **All servers need it** when using JPA, but how it's interpreted depends on the persistence model.

### Two Persistence Models

| Model | `transaction-type` | Who manages the EntityManager? | Datasource source |
|---|---|---|---|
| **Application-Managed** | `RESOURCE_LOCAL` | Your code creates `EntityManager` via `EntityManagerFactory` | JDBC properties in `persistence.xml` |
| **Container-Managed** | `JTA` | Server injects `@PersistenceContext` EntityManager | JNDI datasource configured in server |

### Application-Managed (RESOURCE_LOCAL)

The app controls the entire JPA lifecycle. The `persistence.xml` contains the JDBC driver, URL, user, and password directly.

```xml
<persistence-unit name="myPU" transaction-type="RESOURCE_LOCAL">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <class>com.example.MyEntity</class>
    <properties>
        <property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/>
        <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:testdb"/>
        <property name="jakarta.persistence.jdbc.user" value="sa"/>
        <property name="jakarta.persistence.jdbc.password" value=""/>
        <property name="jakarta.persistence.schema-generation.database.action" value="drop-and-create"/>
    </properties>
</persistence-unit>
```

**Used by**: This demo project, standalone apps, microservices.

### Container-Managed (JTA)

The server manages transactions and provides the `EntityManager` via `@PersistenceContext` injection. The `persistence.xml` references a JNDI datasource name, and the actual database configuration lives in the server's config files.

```xml
<persistence-unit name="myPU" transaction-type="JTA">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <jta-data-source>java:comp/DefaultDataSource</jta-data-source>
    <class>com.example.MyEntity</class>
</persistence-unit>
```

**Used by**: Traditional EE apps on full application servers (GlassFish, Payara, WildFly, Liberty).

### Key Differences

| Aspect | RESOURCE_LOCAL | JTA |
|---|---|---|
| Transaction control | Manual (`begin`/`commit`/`rollback`) | Declarative (`@Transactional`) |
| DataSource config | In `persistence.xml` | In server config (`domain.xml`, `standalone.xml`, `server.xml`) |
| EntityManager injection | Not available | `@PersistenceContext` |
| Portability | High (works anywhere) | Depends on server JNDI setup |
| Best for | Microservices, testing | Traditional EE apps |

### Server-Specific Persistence Notes

| Server | Default JPA Provider | Datasource Config Location | Notes |
|---|---|---|---|
| **GlassFish** | EclipseLink 5.0.0 | `domain.xml` via `asadmin` | JNDI: `java:comp/DefaultDataSource` |
| **Payara** | EclipseLink | `domain.xml` via `asadmin` | Same as GlassFish |
| **WildFly** | Hibernate ORM | `standalone.xml` `<datasources>` | Use `<datasource>` element, not JNDI directly |
| **Liberty** | EclipseLink 4.0.6 | `server.xml` `<dataSource>` | Use `<properties.h2embedded>` or `<properties.derby.embedded>` |

### persistence.xml Location

Always at: **`src/main/resources/META-INF/persistence.xml`**

In the WAR: **`WEB-INF/classes/META-INF/persistence.xml`**

---

## GlassFish (Reference Implementation)

**Docs**: https://glassfish.org/documentation.html
**Current**: 8.0.3 (EE 11, Jun 2026)
**JPA Provider**: EclipseLink 5.0.0 (supports Java 25)

### Overview

GlassFish is the Jakarta EE reference implementation. Version 8.0.3 is the first to fully support EE 11 with Java 25 compatibility via EclipseLink 5.0.0.

| Artifact | GroupId:ArtifactId | Version |
|---|---|---|
| Server (zip) | `org.glassfish.main.distributions:glassfish` | 8.0.3 |
| Cargo container ID | `glassfish7x` | — |

> **Note**: GlassFish 8.0.3 is NOT on Maven Central (only up to 8.0.0-M12 milestones). It is downloaded from the GlassFish repository via `maven-dependency-plugin`.

### Maven Plugin

**No dedicated GlassFish Maven plugin.** Uses:
- `maven-dependency-plugin` — downloads and unpacks GlassFish
- `cargo-maven2-plugin` — manages server lifecycle (start/stop/deploy)

### Running the Application

```bash
# Package and download GlassFish
mvn package -Pglassfish

# Start server (foreground)
cd target/glassfish8 && bin/startserv

# Deploy WAR (in another terminal)
cd target/glassfish8 && bin/asadmin deploy --force=true target/jakartaee-mvc-sample.war

# Access app
open http://localhost:8080/jakartaee-mvc-sample/

# Stop server
cd target/glassfish8 && bin/stopserv
```

### Dev Mode

**Not available.** GlassFish does not have a hot-reload dev mode. You must redeploy manually after each change.

### Embedded / Self-Contained Mode

**Not available.** GlassFish is a full server installation. No embedded JAR mode.

### Deployment

1. Start GlassFish: `bin/startserv`
2. Deploy: `bin/asadmin deploy --force=true target/jakartaee-mvc-sample.war`
3. Or copy WAR to autodeploy: `cp target/jakartaee-mvc-sample.war glassfish/domains/domain1/autodeploy/`

### Key File Structure

```
target/glassfish8/
├── bin/
│   ├── startserv          # Start server
│   ├── stopserv           # Stop server
│   └── asadmin            # Admin CLI
├── glassfish/
│   ├── domains/
│   │   └── domain1/
│   │       ├── autodeploy/          # Drop WARs here for auto-deploy
│   │       ├── config/
│   │       │   ├── domain.xml       # Main server config
│   │       │   └── login.conf       # Security config
│   │       ├── lib/                 # Domain-level libs
│   │       └── applications/        # Deployed apps
│   ├── lib/                         # Server-level libs
│   └── modules/                     # OSGi modules
```

### Configuration Files

| File | Location | Purpose |
|---|---|---|
| `domain.xml` | `glassfish/domains/domain1/config/domain.xml` | Main server config (ports, resources, etc.) |
| `web.xml` | Your WAR: `WEB-INF/web.xml` | App deployment descriptor |
| `persistence.xml` | Your WAR: `META-INF/persistence.xml` | JPA config |
| JDBC Resources | `domain.xml` via `asadmin` | Datasource configuration |

### Commands Summary

| Command | Description |
|---|---|
| `mvn package -Pglassfish` | Build WAR + download GlassFish |
| `bin/startserv` | Start server (port 8080) |
| `bin/stopserv` | Stop server |
| `bin/asadmin deploy --force=true app.war` | Deploy WAR |
| `bin/asadmin undeploy app` | Undeploy app |
| `bin/asadmin list-applications` | List deployed apps |

---

## Payara

**Docs**: https://docs.payara.fish/
**Current**: 7.2026.5 (EE 11, May 2026)
**JPA Provider**: EclipseLink (supports Java 25)

### Overview

Payara is a fork of GlassFish with commercial support. Version 7 is the first GA release with full Jakarta EE 11 TCK certification. Payara 6 Community is EOL.

| Artifact | GroupId:ArtifactId | Version |
|---|---|---|
| Server (zip) | `fish.payara.distributions:payara` | 7.2026.5 |
| Micro (jar) | `fish.payara.extras:payara-micro` | 7.2026.5 |
| Embedded All | `fish.payara.extras:payara-embedded-all` | 7.2026.5 |
| Cargo container ID | `payara7x` | — |

### Maven Plugin

**No official Payara Maven plugin.** Uses:
- `maven-dependency-plugin` — downloads and unpacks Payara
- `cargo-maven2-plugin` — manages server lifecycle

### Running the Application

```bash
# Package and download Payara
mvn package -Ppayara

# Start server (foreground)
cd target/payara7 && bin/asadmin start-domain

# Deploy WAR
cd target/payara7 && bin/asadmin deploy --force=true target/jakartaee-mvc-sample.war

# Access app
open http://localhost:8080/jakartaee-mvc-sample/

# Stop server
cd target/payara7 && bin/asadmin stop-domain
```

### Dev Mode

**Not available.** Payara does not have a hot-reload dev mode. Redeploy manually.

### Embedded / Self-Contained Mode

**Payara Micro** provides a lightweight, self-contained mode:

```bash
# Download Payara Micro JAR
mvn dependency:copy -Dartifact=fish.payara.extras:payara-micro:7.2026.5 -DoutputDirectory=target/

# Run app with Payara Micro
java -jar target/payara-micro-7.2026.5.jar --deploy target/jakartaee-mvc-sample.war
```

No installation needed — single JAR + your WAR.

**Payara Embedded** for in-process testing:

```xml
<dependency>
    <groupId>fish.payara.extras</groupId>
    <artifactId>payara-embedded-all</artifactId>
    <version>7.2026.5</version>
    <scope>test</scope>
</dependency>
```

### Deployment

1. Start Payara: `bin/asadmin start-domain`
2. Deploy: `bin/asadmin deploy --force=true target/jakartaee-mvc-sample.war`
3. Or drop WAR in `domains/domain1/autodeploy/`

### Key File Structure

```
target/payara7/
├── bin/
│   ├── asadmin            # Admin CLI
│   ├── start-domain       # Start domain
│   └── stop-domain        # Stop domain
├── glassfish/
│   ├── domains/
│   │   └── domain1/
│   │       ├── autodeploy/          # Drop WARs here
│   │       ├── config/
│   │       │   ├── domain.xml       # Main server config
│   │       │   └── login.conf       # Security config
│   │       ├── lib/                 # Domain-level libs
│   │       └── applications/        # Deployed apps
│   ├── lib/                         # Server-level libs
│   └── modules/                     # OSGi modules
```

### Configuration Files

| File | Location | Purpose |
|---|---|---|
| `domain.xml` | `glassfish/domains/domain1/config/domain.xml` | Main server config |
| `web.xml` | Your WAR: `WEB-INF/web.xml` | App deployment descriptor |
| `persistence.xml` | Your WAR: `META-INF/persistence.xml` | JPA config |
| JDBC Resources | `domain.xml` via `asadmin` | Datasource configuration |

### Commands Summary

| Command | Description |
|---|---|
| `mvn package -Ppayara` | Build WAR + download Payara |
| `bin/asadmin start-domain` | Start server (port 8080) |
| `bin/asadmin stop-domain` | Stop server |
| `bin/asadmin deploy --force=true app.war` | Deploy WAR |
| `bin/asadmin undeploy app` | Undeploy app |
| `java -jar payara-micro.jar --deploy app.war` | Run with Payara Micro |

---

## WildFly

**Docs**: https://docs.wildfly.org/
**Current**: 40.0.0.Final (EE 11, May 2026)
**JPA Provider**: Hibernate ORM (NOT EclipseLink)

### Overview

WildFly 40 is the first version to include EE 11 in the standard distribution (previously only in WildFly Preview). It bundles Hibernate ORM as its JPA provider, which supports Java 25.

| Artifact | GroupId:ArtifactId | Version |
|---|---|---|
| Maven Plugin | `org.wildfly.plugins:wildfly-maven-plugin` | 6.0.0.Final |
| Krazo RESTEasy | `org.eclipse.krazo:krazo-resteasy` | 4.0.0 |

### Maven Plugin

**`wildfly-maven-plugin`** — first-party plugin with full lifecycle support:

| Goal | Description |
|---|---|
| `wildfly:dev` | Development mode with hot-reload |
| `wildfly:run` | Start server |
| `wildfly:deploy` | Deploy WAR to running server |
| `wildfly:undeploy` | Undeploy app |
| `wildfly:package` | Package provisioned server with app |

### Running the Application

```bash
# Quick start: build, provision server, deploy, and run
mvn clean package verify -Pwildfly
./target/server/bin/standalone.sh

# Access app
open http://localhost:8080/jakartaee-mvc-sample/
```

### Dev Mode

**Hot-reload available** — the best dev experience among all servers:

```bash
mvn clean wildfly:dev -Pwildfly
```

This provisions a WildFly server into `target/server/`, deploys your app, and watches for code changes. Edit a Java file or XHTML, and the app reloads automatically.

### Embedded / Self-Contained Mode

**WildFly Galleon provisioned server** — the server is downloaded and configured into `target/server/` as part of the Maven build. It's self-contained and portable:

```
target/server/
├── bin/
│   ├── standalone.sh       # Start server
│   └── standalone.bat
├── standalone/
│   └── configuration/
│       ├── standalone.xml  # Main server config
│       └── ...
└── modules/                # Server modules
```

### Deployment

1. Start server: `./target/server/bin/standalone.sh`
2. Deploy: `mvn wildfly:deploy -Pwildfly`
3. Or copy WAR to `target/server/standalone/deployments/`

### Key File Structure

```
target/server/                         # Provisioned by Galleon
├── bin/
│   ├── standalone.sh                  # Start server
│   └── standalone.bat
├── standalone/
│   ├── configuration/
│   │   ├── standalone.xml             # Main server config
│   │   ├── standalone-full.xml        # Full profile config
│   │   └── users.properties           # User credentials
│   ├── deployments/                   # Drop WARs here
│   └── data/                          # Runtime data
└── modules/                           # JBoss modules
```

### Configuration Files

| File | Location | Purpose |
|---|---|---|
| `standalone.xml` | `standalone/configuration/standalone.xml` | Main server config (subsystems, datasources, etc.) |
| `web.xml` | Your WAR: `WEB-INF/web.xml` | App deployment descriptor |
| `persistence.xml` | Your WAR: `META-INF/persistence.xml` | JPA config |
| `jboss-deployment-structure.xml` | Your WAR: `WEB-INF/jboss-deployment-structure.xml` | Module dependencies (optional) |
| `datasources` | `standalone.xml` `<datasources>` section | Datasource configuration |

### Commands Summary

| Command | Description |
|---|---|
| `mvn clean package verify -Pwildfly` | Build + provision server + deploy |
| `mvn clean wildfly:dev -Pwildfly` | Dev mode with hot-reload |
| `mvn wildfly:deploy -Pwildfly` | Deploy to running server |
| `mvn wildfly:undeploy -Pwildfly` | Undeploy app |
| `./target/server/bin/standalone.sh` | Start server manually |

---

## Open Liberty

**Docs**: https://openliberty.io/docs/
**Guides**: https://openliberty.io/guides/
**Current**: 26.0.0.5 (EE 10, May 2026)
**JPA Provider**: EclipseLink 4.0.6 (⚠️ does NOT support Java 25)

### Overview

Open Liberty is a lightweight, modular server. It has the best dev mode experience but currently only supports EE 10 — **Jakarta EE 11 features are NOT available** (`jakartaee-11.0` feature does not exist). EclipseLink 4.0.6 bundled with Liberty fails with Java 25: `Unsupported class file major version 69`.

| Artifact | GroupId:ArtifactId | Version |
|---|---|---|
| Maven Plugin | `io.openliberty.tools:liberty-maven-plugin` | 3.11.4 |
| H2 Database | `com.h2database:h2` | 2.4.240 |

> **⚠️ WARNING**: Liberty does NOT support Java 25 with its bundled EclipseLink. Use GlassFish or Payara for Java 25.

### Maven Plugin

**`liberty-maven-plugin`** — first-party plugin with full lifecycle support:

| Goal | Description |
|---|---|
| `liberty:dev` | Development mode with hot-reload |
| `liberty:run` | Start server |
| `liberty:devc` | Dev mode with container support |
| `liberty:package` | Package server with app |

### Running the Application

```bash
# Build WAR
mvn clean package -Popenliberty

# Run server
mvn liberty:run -Popenliberty

# Access app
open http://localhost:9080/jakartaee-mvc-sample/
```

### Dev Mode

**Hot-reload available** — excellent dev experience:

```bash
mvn liberty:dev -Popenliberty
```

Watches for changes in `src/main/java`, `src/main/resources`, and `src/main/webapp`. Java changes trigger recompilation and app reload. XHTML/properties changes are picked up instantly.

### Embedded / Self-Contained Mode

**Liberty kernel** — Liberty can run as an embedded server within your application:

```xml
<dependency>
    <groupId>io.openliberty</groupId>
    <artifactId>openliberty-runtime</artifactId>
    <version>26.0.0.5</version>
    <type>zip</type>
</dependency>
```

### Deployment

1. Start server: `mvn liberty:run -Popenliberty`
2. App is deployed automatically during `liberty:run`
3. Or manual: copy WAR to `wlp/usr/servers/defaultServer/dropins/`

### Key File Structure

```
target/wlp/
├── bin/
│   ├── server                # Server script
│   └── runfw                 # Run framework
├── usr/
│   ├── servers/
│   │   └── defaultServer/
│   │       ├── server.xml           # Main config
│   │       ├── dropins/             # Drop WARs here
│   │       ├── apps/                # Installed apps
│   │       ├── logs/                # Server logs
│   │       └── workarea/            # Runtime cache
│   └── shared/                      # Shared resources
└── etc/                             # Framework config
```

### Configuration Files

| File | Location | Purpose |
|---|---|---|
| `server.xml` | `usr/servers/defaultServer/server.xml` | Main server config (features, endpoints, datasources) |
| `web.xml` | Your WAR: `WEB-INF/web.xml` | App deployment descriptor |
| `persistence.xml` | Your WAR: `META-INF/persistence.xml` | JPA config |
| `bootstrap.properties` | `usr/servers/defaultServer/bootstrap.properties` | Server bootstrap config (optional) |
| `jvm.options` | `usr/servers/defaultServer/jvm.options` | JVM arguments |

### server.xml (this project)

```xml
<server description="Jakarta EE 10 MVC Server">
    <featureManager>
        <feature>jakartaee-10.0</feature>
    </featureManager>
    <basicRegistry id="basic" realm="BasicRealm"/>
    <httpEndpoint id="defaultHttpEndpoint" httpPort="9080" httpsPort="9443"/>
    <applicationManager autoExpand="true"/>
    <library id="h2JDBCLib">
        <fileset dir="${shared.resource.dir}" includes="h2*.jar"/>
    </library>
    <dataSource id="DefaultDataSource">
        <jdbcDriver libraryRef="h2JDBCLib"/>
        <properties.h2embedded databaseName="mem:taskdb"/>
    </dataSource>
</server>
```

### Commands Summary

| Command | Description |
|---|---|
| `mvn clean package -Popenliberty` | Build WAR |
| `mvn liberty:dev -Popenliberty` | Dev mode with hot-reload |
| `mvn liberty:run -Popenliberty` | Run server |
| `mvn liberty:package -Popenliberty` | Package server with app |

---

## Comparison: Which Server to Use?

| Criteria | GlassFish | Payara | WildFly | Liberty |
|---|---|---|---|---|
| **EE 11** | ✅ | ✅ | ✅ | ❌ (EE 10 only) |
| **Java 25** | ✅ | ✅ | ⚠️ unverified | ❌ (EclipseLink 4.0.6 fails) |
| **Dev Mode (hot-reload)** | ❌ | ❌ | ✅ `wildfly:dev` | ✅ `liberty:dev` |
| **Embedded Mode** | ❌ | ✅ Payara Micro | ✅ Galleon | ✅ Liberty kernel |
| **Maven Plugin** | Cargo (3rd party) | Cargo (3rd party) | wildfly-maven-plugin (1st party) | liberty-maven-plugin (1st party) |
| **Deployment Speed** | Slow (asadmin) | Slow (asadmin) | Fast (auto-deploy) | Fast (auto-deploy) |
| **JPA Provider** | EclipseLink 5.0.0 | EclipseLink | Hibernate ORM | EclipseLink 4.0.6 |
| **Best For** | EE 11 reference testing | Production EE 11 | EE 11 dev + production | EE 10 development |

### Recommended for this project

- **GlassFish 8.0.3** — primary target (reference impl, Java 25 support)
- **WildFly 40** — best dev experience with `wildfly:dev` hot-reload
- **Payara 7** — production-grade EE 11 with commercial support option
- **Liberty** — only for EE 10 testing (not recommended for this project)
