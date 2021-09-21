package cargotracker.audit;

import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import cargotracker.config.VirtualThreads;
import cargotracker.entity.cargo.Cargo;
import cargotracker.event.CargoUpdated;

@ApplicationScoped
public class AuditRecorder {
    @Inject
    private Logger logger;
    @Inject
    private AuditLog auditLog;
    @Inject
    @VirtualThreads
    private ManagedExecutorService executor;

    // Only committed changes are audited. The snapshot is taken on the observer
    // thread (the entity may be detached or lazily loaded later); the insert runs
    // on a virtual thread in its own transaction-less, auto-committed operation.
    void onCargoUpdated(@Observes(during = TransactionPhase.AFTER_SUCCESS) @CargoUpdated Cargo cargo) {
        var delivery = cargo.getDelivery();
        var lastKnown = delivery.getLastKnownLocation();
        var entry = new AuditEntry(
                UUID.randomUUID().toString(),
                cargo.getTrackingId().getIdString(),
                delivery.getTransportStatus().name(),
                delivery.getRoutingStatus().name(),
                delivery.isMisdirected(),
                lastKnown == null ? null : lastKnown.getUnLoCode().getCode(),
                Instant.now());
        executor.runAsync(() -> auditLog.record(entry))
                .exceptionally(failure -> {
                    logger.log(Level.WARNING, "Could not write audit entry " + entry, failure);
                    return null;
                });
    }
}
