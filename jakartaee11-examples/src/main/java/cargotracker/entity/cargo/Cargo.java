package cargotracker.entity.cargo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import cargotracker.entity.handling.HandlingHistory;
import cargotracker.entity.location.Location;

import static java.util.Objects.requireNonNull;

@Entity
@NamedQuery(name = "Cargo.findAll", query = "Select c from Cargo c")
@NamedQuery(name = "Cargo.findByTrackingId", query = "Select c from Cargo c where c.trackingId = :trackingId")
public class Cargo {
    @Id
    @GeneratedValue
    private Long id;
    // Optimistic locking: concurrent handling events for the same cargo fail fast
    // instead of silently overwriting each other's delivery snapshot.
    @Version
    private int version;
    @Embedded
    @NotNull(message = "Tracking ID is required.")
    private TrackingId trackingId;
    @ManyToOne
    @JoinColumn(name = "origin_id", updatable = false)
    @NotNull
    private Location origin;
    @Embedded
    @NotNull(message = "Route specification is required.")
    private RouteSpecification routeSpecification;
    @Embedded
    @NotNull
    private Itinerary itinerary;
    @Embedded
    @NotNull
    private Delivery delivery;

    protected Cargo() {
    }

    public Cargo(TrackingId trackingId, RouteSpecification routeSpecification) {
        requireNonNull(trackingId, "Tracking ID is required.");
        requireNonNull(routeSpecification, "Route specification is required.");
        this.trackingId = trackingId;
        this.origin = routeSpecification.getOrigin();
        this.routeSpecification = routeSpecification;
        this.itinerary = Itinerary.empty();
        this.delivery = Delivery.derivedFrom(this.routeSpecification, this.itinerary, HandlingHistory.EMPTY);
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public Location getOrigin() {
        return origin;
    }

    public RouteSpecification getRouteSpecification() {
        return routeSpecification;
    }

    @NotNull
    public Delivery getDelivery() {
        return delivery;
    }

    @NotNull
    public Itinerary getItinerary() {
        // A provider may hand back null for an embeddable whose columns are all null.
        if (this.itinerary == null)
            this.itinerary = Itinerary.empty();
        return this.itinerary;
    }

    public void specifyNewRoute(RouteSpecification routeSpecification) {
        requireNonNull(routeSpecification, "Route specification is required.");
        this.routeSpecification = routeSpecification;
        this.delivery = delivery.updateOnRouting(this.routeSpecification, getItinerary());
    }

    public void assignToRoute(Itinerary itinerary) {
        requireNonNull(itinerary, "Itinerary is required for assignment.");
        this.itinerary = itinerary;
        this.delivery = delivery.updateOnRouting(this.routeSpecification, this.itinerary);
    }

    public void deriveDeliveryProgress(HandlingHistory handlingHistory) {
        this.delivery = Delivery.derivedFrom(getRouteSpecification(), getItinerary(), handlingHistory);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        return (object instanceof Cargo cargo && trackingId.equals(cargo.trackingId));
    }

    @Override
    public int hashCode() {
        return trackingId.hashCode();
    }
}
