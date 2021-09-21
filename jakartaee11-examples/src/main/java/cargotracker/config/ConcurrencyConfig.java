package cargotracker.config;

import jakarta.enterprise.concurrent.ManagedExecutorDefinition;
import jakarta.enterprise.context.ApplicationScoped;

// Jakarta Concurrency 3.1 (EE 11):
//  - virtual = true runs tasks on Java 21+ virtual threads (falls back to platform
//    threads on older JDKs).
//  - qualifiers make the executor injectable with plain CDI:
//        @Inject @VirtualThreads ManagedExecutorService executor;
//    instead of @Resource(lookup = "java:app/concurrent/VirtualExecutor").
// Container context (CDI, naming, classloader) propagates to the tasks.
@ApplicationScoped
@ManagedExecutorDefinition(
        name = "java:app/concurrent/VirtualExecutor",
        qualifiers = VirtualThreads.class,
        virtual = true)
public class ConcurrencyConfig {
}
