package cargotracker.microprofile;

import jakarta.enterprise.context.ApplicationScoped;

import java.lang.management.ManagementFactory;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

// /health/live: only checks the JVM itself. Never put dependency checks (database,
// remote services) into liveness: a database outage would make Kubernetes restart
// every pod instead of just taking them out of the load balancer.
@Liveness
@ApplicationScoped
public class CargoTrackerLiveness implements HealthCheck {
    private static final double MAX_HEAP_RATIO = 0.95;

    @Override
    public HealthCheckResponse call() {
        var heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double ratio = heap.getMax() > 0 ? (double) heap.getUsed() / heap.getMax() : 0;
        return HealthCheckResponse.named("cargo-tracker-heap")
                .withData("heapUsedRatio", String.format("%.2f", ratio))
                .status(ratio < MAX_HEAP_RATIO)
                .build();
    }
}
