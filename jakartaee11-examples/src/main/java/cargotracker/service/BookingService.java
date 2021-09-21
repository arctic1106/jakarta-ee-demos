package cargotracker.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cargotracker.entity.cargo.*;
import cargotracker.repository.CargoRepository;
import cargotracker.repository.LocationRepository;
import cargotracker.entity.location.UnLoCode;

@ApplicationScoped
@Transactional
public class BookingService {
    @Inject
    private Logger logger;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private LocationRepository locationRepository;
    @Inject
    private RoutingService routingService;

    public TrackingId bookNewCargo(UnLoCode originUnLocode, UnLoCode destinationUnLocode, LocalDate arrivalDeadline) {
        var trackingId = cargoRepository.nextTrackingId();
        var origin = locationRepository.find(originUnLocode);
        var destination = locationRepository.find(destinationUnLocode);
        var routeSpecification = new RouteSpecification(origin, destination, arrivalDeadline);
        var cargo = new Cargo(trackingId, routeSpecification);
        cargoRepository.store(cargo);
        logger.log(Level.INFO, "Booked new cargo with tracking ID {0}", cargo.getTrackingId().getIdString());
        return cargo.getTrackingId();
    }

    // Read-only: no need to start a transaction just to compute candidates.
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Itinerary> requestPossibleRoutesForCargo(TrackingId trackingId) {
        var cargo = cargoRepository.find(trackingId);
        if (cargo == null)
            return Collections.emptyList();
        return routingService.fetchRoutesForSpecification(cargo.getRouteSpecification());
    }

    public void assignCargoToRoute(Itinerary itinerary, TrackingId trackingId) {
        var cargo = existingCargo(trackingId);
        cargo.assignToRoute(itinerary);
        cargoRepository.store(cargo);
        logger.log(Level.INFO, "Assigned cargo {0} to new route", trackingId);
    }

    public void changeDestination(TrackingId trackingId, UnLoCode unLocode) {
        var cargo = existingCargo(trackingId);
        var newDestination = locationRepository.find(unLocode);
        var routeSpecification = new RouteSpecification(cargo.getOrigin(), newDestination,
                cargo.getRouteSpecification().getArrivalDeadline());
        cargo.specifyNewRoute(routeSpecification);
        cargoRepository.store(cargo);
        logger.log(Level.INFO, "Changed destination for cargo {0} to {1}",
                new Object[] { trackingId, routeSpecification.getDestination() });
    }

    public void changeDeadline(TrackingId trackingId, LocalDate newDeadline) {
        var cargo = existingCargo(trackingId);
        var routeSpecification = new RouteSpecification(cargo.getOrigin(),
                cargo.getRouteSpecification().getDestination(), newDeadline);
        cargo.specifyNewRoute(routeSpecification);
        cargoRepository.store(cargo);
        logger.log(Level.INFO, "Changed deadline for cargo {0} to {1}", new Object[] { trackingId, newDeadline });
    }

    private Cargo existingCargo(TrackingId trackingId) {
        var cargo = cargoRepository.find(trackingId);
        if (cargo == null)
            throw new NoSuchElementException("Unknown cargo " + trackingId);
        return cargo;
    }
}
