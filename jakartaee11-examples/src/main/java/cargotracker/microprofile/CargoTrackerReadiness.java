package cargotracker.microprofile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import cargotracker.entity.cargo.RoutingStatus;
import cargotracker.repository.CargoQueries;
import cargotracker.service.OverdueCargoMonitor;

// /health/ready: ready only when the database answers a real query.
@Readiness
@ApplicationScoped
public class CargoTrackerReadiness implements HealthCheck {
    @Inject
    private CargoQueries cargoQueries;
    @Inject
    private OverdueCargoMonitor overdueCargoMonitor;

    @Override
    public HealthCheckResponse call() {
        var response = HealthCheckResponse.named("cargo-tracker-database");
        try {
            return response
                    .withData("routedCargo", cargoQueries.countByRoutingStatus(RoutingStatus.ROUTED))
                    .withData("notRoutedCargo", cargoQueries.countByRoutingStatus(RoutingStatus.NOT_ROUTED))
                    .withData("overdueCargo", overdueCargoMonitor.overdueCount())
                    .up()
                    .build();
        } catch (RuntimeException e) {
            return response.withData("error", String.valueOf(e.getMessage())).down().build();
        }
    }
}
