package cargotracker.entity.cargo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;

import cargotracker.entity.location.Location;

@Embeddable
public class RouteSpecification {
    @ManyToOne
    @JoinColumn(name = "spec_origin_id", updatable = false)
    private Location origin;
    @ManyToOne
    @JoinColumn(name = "spec_destination_id")
    private Location destination;
    @Column(name = "spec_arrival_deadline")
    @NotNull
    private LocalDate arrivalDeadline;

    public RouteSpecification() {
    }

    public RouteSpecification(
            @NotNull Location origin,
            @NotNull Location destination,
            @NotNull LocalDate arrivalDeadline) {
        if (origin.equals(destination))
            throw new IllegalArgumentException(String.format("Origin and destination can't be the same: %s", origin));
        this.origin = origin;
        this.destination = destination;
        this.arrivalDeadline = arrivalDeadline;
    }

    public Location getOrigin() {
        return origin;
    }

    public Location getDestination() {
        return destination;
    }

    public LocalDate getArrivalDeadline() {
        return arrivalDeadline;
    }

    public boolean isSatisfiedBy(Itinerary itinerary) {
        return itinerary != null
                && getOrigin().equals(itinerary.getInitialDepartureLocation())
                && getDestination().equals(itinerary.getFinalArrivalLocation())
                && getArrivalDeadline().isAfter(itinerary.getFinalArrivalDate().toLocalDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RouteSpecification that))
            return false;
        return Objects.equals(origin, that.origin) && Objects.equals(destination, that.destination)
                && Objects.equals(arrivalDeadline, that.arrivalDeadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, destination, arrivalDeadline);
    }
}
