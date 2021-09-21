# Open Liberty Features — Latest Versions

Source: Maven Central (`io.openliberty.features`) + Open Liberty docs  
Runtime: **26.0.0.5** (Jun 2026)

---

## Platform Features

| Feature | Latest |
|---------|--------|
| `jakartaee` | **11.0** |
| `webProfile` | **11.0** |
| `microProfile` | **7.1** |
| `jakartaeeClient` | **11.0** |

---

## Jakarta EE Core (EE 11)

| Feature | Latest |
|---------|--------|
| `servlet` | **6.1** |
| `cdi` | **4.1** |
| `faces` | **4.1** |
| `restfulWS` | **4.0** |
| `restfulWSClient` | **4.0** |
| `persistence` | **3.2** |
| `persistenceContainer` | **3.2** |
| `enterpriseBeans` | **4.0** |
| `enterpriseBeansLite` | **4.0** |
| `enterpriseBeansHome` | **4.0** |
| `enterpriseBeansPersistentTimer` | **4.0** |
| `enterpriseBeansRemote` | **4.0** |
| `mdb` | **4.0** |
| `managedBeans` | **2.0** |
| `jsonb` | **3.0** |
| `jsonp` | **2.1** |
| `batch` | **2.1** |
| `concurrent` | **3.1** |
| `websocket` | **2.2** |
| `expressionLanguage` | **6.0** |
| `pages` | **4.0** |
| `xmlBinding` | **4.0** |
| `xmlWS` | **4.0** |
| `xmlWSClient` | **4.0** |
| `messaging` | **3.1** |
| `messagingClient` | **3.0** |
| `messagingServer` | **3.0** |
| `messagingSecurity` | **3.0** |
| `connectors` | **2.1** |
| `connectorsInboundSecurity` | **2.0** |
| `data` | **1.0** |
| `dataContainer` | **1.0** |
| `jdbc` | **4.3** |
| `mail` | **2.1** |
| `validation` | **3.1** |
| `appSecurity` | **6.0** |
| `appAuthentication` | **3.1** |
| `appAuthorization` | **3.0** |
| `appClientSupport` | **2.0** |
| `ssl` | 1.0 |
| `transportSecurity` | 1.0 |
| `jndi` | 1.0 |
| `transaction` | 2.0 |
| `openapi` | **3.1** |

---

## MicroProfile

| Feature | Latest |
|---------|--------|
| `mpConfig` | **3.1** |
| `mpFaultTolerance` | **4.1** |
| `mpHealth` | **4.0** |
| `mpJwt` | **2.1** |
| `mpMetrics` | **5.1** |
| `mpOpenAPI` | **4.1** |
| `mpRestClient` | **4.0** |
| `mpContextPropagation` | **1.3** |
| `mpReactiveMessaging` | **3.0** |
| `mpReactiveStreams` | **3.0** |
| `mpGraphQL` | **2.0** |
| `mpTelemetry` | **2.1** |
| `mpOpenTracing` | **3.0** |

---

## Liberty-Specific

| Feature | Latest |
|---------|--------|
| `adminCenter` | 1.0 |
| `audit` | **2.0** |
| `crac` | **1.4** |
| `grpc` | **1.0** |
| `grpcClient` | **1.0** |
| `springBoot` | **4.0** |
| `opentracing` | **2.0** |
| `mongodb` | 2.0 |
| `distributedMap` | 1.0 |
| `sessionCache` | 1.0 |
| `sessionDatabase` | 1.0 |
| `ldapRegistry` | 3.0 |
| `passwordUtilities` | **1.1** |
| `eventLogging` | 1.0 |
| `requestTiming` | 1.0 |
| `monitor` | 1.0 |
| `logstashCollector` | 1.0 |
| `restConnector` | 2.0 |
| `localConnector` | 1.0 |
| `bells` | 1.0 |
| `federatedRegistry` | 1.0 |
| `cloudant` | 1.0 |
| `couchdb` | 1.0 |
| `webCache` | 1.0 |
| `sipServlet` | 1.1 |
| `batchManagement` | 1.0 |

---

## Legacy (javax.*) — Still Available

| Feature | Latest |
|---------|--------|
| `ejb` | 3.2 |
| `ejbLite` | 3.2 |
| `ejbHome` | 3.2 |
| `ejbRemote` | 3.2 |
| `ejbPersistentTimer` | 3.2 |
| `jsf` | 2.3 |
| `jsfContainer` | 2.3 |
| `jsp` | 2.3 |
| `el` | 3.0 |
| `jpa` | 2.2 |
| `jpaContainer` | 2.2 |
| `jaxrs` | 2.1 |
| `jaxrsClient` | 2.1 |
| `jaxws` | 2.2 |
| `jaxwsClient` | 2.2 |
| `jaxb` | 2.2 |
| `jms` | 2.0 |
| `jca` | 1.7 |
| `json` | 1.0 |
| `javaee` | 8.0 |
| `javaeeClient` | 8.0 |
| `websocket-1.0` / `websocket-1.1` | 1.1 |

---

## Quick Reference: jakartaee-11.0 Includes

When you declare `<feature>jakartaee-11.0</feature>`, Liberty installs all of these:
`servlet-6.1`, `cdi-4.1`, `faces-4.1`, `restfulWS-4.0`, `restfulWSClient-4.0`,
`persistence-3.2`, `persistenceContainer-3.2`, `enterpriseBeans-4.0`,
`enterpriseBeansLite-4.0`, `enterpriseBeansHome-4.0`, `enterpriseBeansPersistentTimer-4.0`,
`enterpriseBeansRemote-4.0`, `mdb-4.0`, `managedBeans-2.0`, `jsonb-3.0`, `jsonp-2.1`,
`batch-2.1`, `concurrent-3.1`, `websocket-2.2`, `expressionLanguage-6.0`, `pages-4.0`,
`xmlBinding-4.0`, `xmlWS-4.0`, `xmlWSClient-4.0`, `messaging-3.1`, `messagingClient-3.0`,
`messagingServer-3.0`, `messagingSecurity-3.0`, `connectors-2.1`, `connectorsInboundSecurity-2.0`,
`data-1.0`, `dataContainer-1.0`, `jdbc-4.2`, `mail-2.1`, `validation-3.1`, `appSecurity-6.0`,
`appAuthentication-3.1`, `appAuthorization-3.0`, `appClientSupport-2.0`, `ssl-1.0`,
`transportSecurity-1.0`, `jndi-1.0`, `transaction-2.0`, `openapi-3.1`
