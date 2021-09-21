package cargotracker.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import cargotracker.entity.cargo.Cargo;

// Never serialize entities directly: lazy relations, cycles and internal ids leak.
public record ShipmentSummary(
        String trackingId,
        String origin,
        String destination,
        LocalDate arrivalDeadline,
        String routingStatus,
        String transportStatus,
        boolean misdirected,
        String lastKnownLocation,
        LocalDateTime estimatedTimeOfArrival,
        int legs) {

    public static ShipmentSummary of(Cargo cargo) {
        var spec = cargo.getRouteSpecification();
        var delivery = cargo.getDelivery();
        var lastKnown = delivery.getLastKnownLocation();
        return new ShipmentSummary(
                cargo.getTrackingId().getIdString(),
                spec.getOrigin().getUnLoCode().getCode(),
                spec.getDestination().getUnLoCode().getCode(),
                spec.getArrivalDeadline(),
                delivery.getRoutingStatus().name(),
                delivery.getTransportStatus().name(),
                delivery.isMisdirected(),
                lastKnown == null ? null : lastKnown.getUnLoCode().getCode(),
                delivery.getEstimatedTimeOfArrival(),
                cargo.getItinerary().getLegs().size());
    }
}
