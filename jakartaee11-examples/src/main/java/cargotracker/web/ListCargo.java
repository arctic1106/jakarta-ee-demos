package cargotracker.web;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

import cargotracker.entity.cargo.Cargo;
import cargotracker.repository.CargoRepository;
import cargotracker.entity.cargo.RoutingStatus;
import cargotracker.entity.cargo.TransportStatus;
import cargotracker.repository.CargoQueries;
import cargotracker.service.OverdueCargoMonitor;

@Named
@RequestScoped
public class ListCargo {
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private CargoQueries cargoQueries;
    @Inject
    private OverdueCargoMonitor overdueCargoMonitor;
    private List<Cargo> notRoutedCargos;
    private List<Cargo> routedUnclaimedCargos;
    private List<Cargo> claimedCargos;

    @PostConstruct
    public void init() {
        List<Cargo> cargos = cargoRepository.findAll();
        notRoutedCargos = cargos.stream()
                .filter(c -> c.getItinerary().isEmpty())
                .toList();
        routedUnclaimedCargos = cargos.stream()
                .filter(c -> !c.getItinerary().isEmpty()
                        && c.getDelivery().getTransportStatus() != TransportStatus.CLAIMED)
                .toList();
        claimedCargos = cargos.stream()
                .filter(c -> c.getDelivery().getTransportStatus() == TransportStatus.CLAIMED)
                .toList();
    }

    // Aggregates computed in the database by Jakarta Data JDQL queries.
    public long getOnboardCount() {
        return cargoQueries.countByTransportStatus(TransportStatus.ONBOARD_CARRIER);
    }

    public long getInPortCount() {
        return cargoQueries.countByTransportStatus(TransportStatus.IN_PORT);
    }

    public long getMisroutedCount() {
        return cargoQueries.countByRoutingStatus(RoutingStatus.MISROUTED);
    }

    public long getOverdueCount() {
        return overdueCargoMonitor.overdueCount();
    }

    public List<Cargo> getNotRoutedCargos() {
        return notRoutedCargos;
    }

    public List<Cargo> getRoutedUnclaimedCargos() {
        return routedUnclaimedCargos;
    }

    public List<Cargo> getClaimedCargos() {
        return claimedCargos;
    }
}
