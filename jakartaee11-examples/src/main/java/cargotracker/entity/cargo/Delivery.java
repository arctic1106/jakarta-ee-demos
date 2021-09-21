package cargotracker.entity.cargo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import static cargotracker.entity.cargo.RoutingStatus.*;
import static cargotracker.entity.cargo.TransportStatus.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import cargotracker.entity.handling.HandlingEvent;
import cargotracker.entity.handling.HandlingHistory;
import cargotracker.entity.location.Location;
import cargotracker.entity.voyage.Voyage;

@Embeddable
public class Delivery {
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_status")
    @NotNull
    private TransportStatus transportStatus;
    @ManyToOne
    @JoinColumn(name = "last_known_location_id")
    private Location lastKnownLocation;
    @ManyToOne
    @JoinColumn(name = "current_voyage_id")
    private Voyage currentVoyage;
    private boolean misdirected;
    private LocalDateTime eta;
    @Embedded
    private HandlingActivity nextExpectedActivity;
    @Column(name = "unloaded_at_dest")
    private boolean isUnloadedAtDestination;
    @Enumerated(EnumType.STRING)
    @Column(name = "routing_status")
    @NotNull
    private RoutingStatus routingStatus;
    @Column(name = "calculated_at")
    @NotNull
    private LocalDateTime calculatedAt;
    @ManyToOne
    @JoinColumn(name = "last_event_id")
    private HandlingEvent lastEvent;

    protected Delivery() {
    }

    public Delivery(HandlingEvent lastEvent, Itinerary itinerary, RouteSpecification routeSpecification) {
        this.calculatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.lastEvent = lastEvent;
        this.misdirected = calculateMisdirectionStatus(itinerary);
        this.routingStatus = calculateRoutingStatus(itinerary, routeSpecification);
        this.transportStatus = calculateTransportStatus();
        this.lastKnownLocation = calculateLastKnownLocation();
        this.currentVoyage = calculateCurrentVoyage();
        this.eta = calculateEta(itinerary);
        this.nextExpectedActivity = calculateNextExpectedActivity(routeSpecification, itinerary);
        this.isUnloadedAtDestination = calculateUnloadedAtDestination(routeSpecification);
    }

    // Creates a new delivery snapshot based on the complete handling history of a
    // cargo, as well as its route specification and itinerary.
    static Delivery derivedFrom(@NotNull RouteSpecification routeSpecification, @NotNull Itinerary itinerary,
            HandlingHistory handlingHistory) {
        var lastEvent = handlingHistory.getMostRecentlyCompletedEvent();
        return new Delivery(lastEvent, itinerary, routeSpecification);
    }

    // Creates new delivery snapshot to reflect changes in routing,
    // i.e. when the route specs or itinerary have changed but no additional
    // handling of the cargo has been performed.
    Delivery updateOnRouting(@NotNull RouteSpecification routeSpecification, Itinerary itinerary) {
        return new Delivery(this.lastEvent, itinerary, routeSpecification);
    }

    public TransportStatus getTransportStatus() {
        return transportStatus;
    }

    public void setTransportStatus(TransportStatus transportStatus) {
        this.transportStatus = transportStatus;
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public void setLastEvent(HandlingEvent lastEvent) {
        this.lastEvent = lastEvent;
    }

    public Voyage getCurrentVoyage() {
        return currentVoyage;
    }

    // A cargo is misdirected if it is in a location that's not in the itinerary.
    // A cargo with no itinerary can not be misdirected.
    // A cargo that has received no handling events can not be misdirected.
    public boolean isMisdirected() {
        return misdirected;
    }

    public void setMisdirected(boolean misdirected) {
        this.misdirected = misdirected;
    }

    public LocalDateTime getEstimatedTimeOfArrival() {
        return eta;
    }

    public HandlingActivity getNextExpectedActivity() {
        return nextExpectedActivity;
    }

    // True if the cargo has been unloaded at the final destination. */
    public boolean isUnloadedAtDestination() {
        return isUnloadedAtDestination;
    }

    public void setUnloadedAtDestination(boolean isUnloadedAtDestination) {
        this.isUnloadedAtDestination = isUnloadedAtDestination;
    }

    public RoutingStatus getRoutingStatus() {
        return routingStatus;
    }

    public void setRoutingStatus(RoutingStatus routingStatus) {
        this.routingStatus = routingStatus;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    private TransportStatus calculateTransportStatus() {
        if (lastEvent == null)
            return NOT_RECEIVED;

        return switch (lastEvent.getType()) {
            case LOAD -> ONBOARD_CARRIER;
            case UNLOAD, RECEIVE, CUSTOMS -> IN_PORT;
            case CLAIM -> CLAIMED;
            default -> UNKNOWN;
        };
    }

    private Location calculateLastKnownLocation() {
        if (lastEvent != null)
            return lastEvent.getLocation();
        else
            return null;
    }

    private Voyage calculateCurrentVoyage() {
        if (getTransportStatus().equals(ONBOARD_CARRIER) && lastEvent != null)
            return lastEvent.getVoyage();
        else
            return null;
    }

    private boolean calculateMisdirectionStatus(Itinerary itinerary) {
        if (lastEvent == null || itinerary == null)
            return false;
        else
            return !itinerary.isExpected(lastEvent);
    }

    private LocalDateTime calculateEta(Itinerary itinerary) {
        if (onTrack())
            // Workaround to Hibernate issue. when `LocalDateTime` is persisted and
            // retrieved, values differ by nanoseconds.
            return itinerary.getFinalArrivalDate().truncatedTo(ChronoUnit.SECONDS);
        else
            return null;
    }

    private HandlingActivity calculateNextExpectedActivity(RouteSpecification routeSpecification, Itinerary itinerary) {
        if (!onTrack())
            return new HandlingActivity();
        if (lastEvent == null)
            return new HandlingActivity(HandlingEvent.Type.RECEIVE, routeSpecification.getOrigin());

        return switch (lastEvent.getType()) {
            case LOAD -> {
                for (var leg : itinerary.getLegs()) {
                    if (leg.getLoadLocation().equals(lastEvent.getLocation())) {
                        yield new HandlingActivity(HandlingEvent.Type.UNLOAD, leg.getUnloadLocation(), leg.getVoyage());
                    }
                }
                yield new HandlingActivity();
            }
            case UNLOAD -> {
                for (var iterator = itinerary.getLegs().iterator(); iterator.hasNext();) {
                    var leg = iterator.next();
                    if (leg.getUnloadLocation().equals(lastEvent.getLocation())) {
                        if (iterator.hasNext()) {
                            var nextLeg = iterator.next();
                            yield new HandlingActivity(HandlingEvent.Type.LOAD, nextLeg.getLoadLocation(),
                                    nextLeg.getVoyage());
                        } else
                            yield new HandlingActivity(HandlingEvent.Type.CLAIM, leg.getUnloadLocation());
                    }
                }
                yield new HandlingActivity();
            }
            case RECEIVE -> {
                var firstLeg = itinerary.getLegs().iterator().next();
                yield new HandlingActivity(HandlingEvent.Type.LOAD, firstLeg.getLoadLocation(), firstLeg.getVoyage());
            }
            default -> new HandlingActivity();
        };
    }

    private RoutingStatus calculateRoutingStatus(Itinerary itinerary, RouteSpecification routeSpecification) {
        if (itinerary == null || itinerary.isEmpty())
            return NOT_ROUTED;
        else {
            if (routeSpecification.isSatisfiedBy(itinerary))
                return ROUTED;
            else
                return MISROUTED;
        }
    }

    private boolean calculateUnloadedAtDestination(RouteSpecification routeSpecification) {
        return lastEvent != null && HandlingEvent.Type.UNLOAD.equals(lastEvent.getType())
                && routeSpecification.getDestination().equals(lastEvent.getLocation());
    }

    private boolean onTrack() {
        return routingStatus.equals(ROUTED) && !misdirected;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                transportStatus,
                lastKnownLocation,
                currentVoyage,
                misdirected,
                eta,
                nextExpectedActivity,
                isUnloadedAtDestination,
                routingStatus,
                calculatedAt,
                lastEvent);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Delivery delivery))
            return false;
        return misdirected == delivery.misdirected
                && isUnloadedAtDestination == delivery.isUnloadedAtDestination
                && transportStatus == delivery.transportStatus
                && routingStatus == delivery.routingStatus
                && Objects.equals(lastKnownLocation, delivery.lastKnownLocation)
                && Objects.equals(currentVoyage, delivery.currentVoyage)
                && Objects.equals(eta, delivery.eta)
                && Objects.equals(nextExpectedActivity, delivery.nextExpectedActivity)
                && Objects.equals(calculatedAt, delivery.calculatedAt)
                && Objects.equals(lastEvent, delivery.lastEvent);
    }
}
