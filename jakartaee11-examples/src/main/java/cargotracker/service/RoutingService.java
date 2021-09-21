package cargotracker.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;

import cargotracker.entity.cargo.Itinerary;
import cargotracker.entity.cargo.Leg;
import cargotracker.entity.cargo.RouteSpecification;
import cargotracker.entity.location.UnLoCode;
import cargotracker.entity.voyage.VoyageNumber;
import cargotracker.pathfinder.TransitEdge;
import cargotracker.pathfinder.TransitPath;
import cargotracker.repository.LocationRepository;
import cargotracker.repository.VoyageRepository;

// Anti-corruption layer between the booking domain and the path finder.
// The path finder is treated like a remote service: MicroProfile Fault Tolerance
// bounds its latency and degrades to "no routes" instead of failing the page.
@ApplicationScoped
public class RoutingService {
    @Inject
    private Logger logger;
    @Inject
    private GraphTraversalService graphTraversalService;
    @Inject
    private LocationRepository locationRepository;
    @Inject
    private VoyageRepository voyageRepository;

    @Timeout(value = 3, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "noRoutes")
    public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
        var origin = routeSpecification.getOrigin().getUnLoCode().getCode();
        var destination = routeSpecification.getDestination().getUnLoCode().getCode();
        var deadline = routeSpecification.getArrivalDeadline().format(DateTimeFormatter.BASIC_ISO_DATE);

        List<TransitPath> transitPaths = graphTraversalService.findShortestPath(origin, destination, deadline);

        return transitPaths.stream()
                .map(this::toItinerary)
                .filter(itinerary -> {
                    boolean satisfied = routeSpecification.isSatisfiedBy(itinerary);
                    if (!satisfied)
                        logger.log(Level.FINE, "Rejected itinerary {0} for {1}", new Object[] { itinerary, routeSpecification });
                    return satisfied;
                })
                .toList();
    }

    // Fallback signature must match the guarded method (same parameters and return type).
    List<Itinerary> noRoutes(RouteSpecification routeSpecification) {
        logger.log(Level.WARNING, "Path finder unavailable, no routes for {0}", routeSpecification);
        return List.of();
    }

    private Itinerary toItinerary(TransitPath transitPath) {
        return new Itinerary(transitPath.getTransitEdges().stream().map(this::toLeg).toList());
    }

    private Leg toLeg(TransitEdge edge) {
        return new Leg(
                voyageRepository.find(new VoyageNumber(edge.getVoyageNumber())),
                locationRepository.find(new UnLoCode(edge.getFromUnLocode())),
                locationRepository.find(new UnLoCode(edge.getToUnLocode())),
                edge.getFromDate(),
                edge.getToDate());
    }
}
