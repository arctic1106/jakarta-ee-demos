package cargotracker.entity.cargo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

import cargotracker.entity.handling.HandlingEvent;
import cargotracker.entity.location.Location;
import cargotracker.entity.voyage.Voyage;

@Embeddable
public class HandlingActivity {
    @Enumerated(EnumType.STRING)
    @Column(name = "next_expected_handling_event_type")
    @NotNull(message = "Handling event type is required.")
    private HandlingEvent.Type type;
    @ManyToOne
    @JoinColumn(name = "next_expected_location_id")
    @NotNull(message = "Location is required.")
    private Location location;
    @ManyToOne
    @JoinColumn(name = "next_expected_voyage_id")
    private Voyage voyage;

    public HandlingActivity() {
    }

    public HandlingActivity(@NotNull HandlingEvent.Type type, @NotNull Location location) {
        this.type = type;
        this.location = location;
    }

    public HandlingActivity(@NotNull HandlingEvent.Type type, @NotNull Location location, @NotNull Voyage voyage) {
        this.type = type;
        this.location = location;
        this.voyage = voyage;
    }

    public HandlingEvent.Type getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public boolean isEmpty() {
        if (type != null)
            return false;
        if (location != null)
            return false;
        return voyage == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HandlingActivity that))
            return false;
        return type == that.type && Objects.equals(location, that.location) && Objects.equals(voyage, that.voyage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, location, voyage);
    }
}
