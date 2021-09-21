package cargotracker.entity.cargo;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cargotracker.entity.handling.HandlingEvent;
import cargotracker.entity.location.Location;

// An itinerary is a value object embedded in Cargo. Never share an instance
// between entities: JPA providers replace the legs collection in place while
// persisting, so a shared "EMPTY" constant would be mutated behind your back.
@Embeddable
public class Itinerary {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cargo_id")
    @OrderColumn(name = "leg_order")
    private List<Leg> legs = new ArrayList<>();

    public Itinerary() {
    }

    public Itinerary(List<Leg> legs) {
        Objects.requireNonNull(legs, "Legs are required.");
        if (legs.isEmpty())
            throw new IllegalArgumentException("An itinerary needs at least one leg.");
        this.legs = new ArrayList<>(legs);
    }

    public static Itinerary empty() {
        return new Itinerary();
    }

    public List<Leg> getLegs() {
        return legs == null ? List.of() : Collections.unmodifiableList(legs);
    }

    public boolean isEmpty() {
        return getLegs().isEmpty();
    }

    public boolean isExpected(HandlingEvent event) {
        if (isEmpty())
            return true;

        return switch (event.getType()) {
            case RECEIVE -> legs.getFirst().getLoadLocation().equals(event.getLocation());
            case LOAD -> legs.stream()
                    .anyMatch(leg -> leg.getLoadLocation().equals(event.getLocation())
                            && leg.getVoyage().equals(event.getVoyage()));
            case UNLOAD -> legs.stream()
                    .anyMatch(leg -> leg.getUnloadLocation().equals(event.getLocation())
                            && leg.getVoyage().equals(event.getVoyage()));
            case CLAIM -> getLastLeg().getUnloadLocation().equals(event.getLocation());
            case CUSTOMS -> true;
        };
    }

    Location getInitialDepartureLocation() {
        return isEmpty() ? null : legs.getFirst().getLoadLocation();
    }

    Location getFinalArrivalLocation() {
        return isEmpty() ? null : getLastLeg().getUnloadLocation();
    }

    LocalDateTime getFinalArrivalDate() {
        var lastLeg = getLastLeg();
        return lastLeg == null ? LocalDateTime.MAX : lastLeg.getUnloadTime();
    }

    Leg getLastLeg() {
        return isEmpty() ? null : legs.getLast();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Itinerary itinerary))
            return false;
        return Objects.equals(getLegs(), itinerary.getLegs());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLegs());
    }

    @Override
    public String toString() {
        return "Itinerary{legs=" + getLegs() + '}';
    }
}
