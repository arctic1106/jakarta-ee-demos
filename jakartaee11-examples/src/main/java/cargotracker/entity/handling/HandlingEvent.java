package cargotracker.entity.handling;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import cargotracker.entity.cargo.Cargo;
import cargotracker.entity.location.Location;
import cargotracker.entity.voyage.Voyage;

@Entity
@NamedQuery(name = "HandlingEvent.findByTrackingId", query = "Select e from HandlingEvent e where e.cargo.trackingId = :trackingId")
public class HandlingEvent {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Type type;
    @ManyToOne
    @JoinColumn(name = "voyage_id")
    private Voyage voyage;
    @ManyToOne
    @JoinColumn(name = "location_id")
    @NotNull
    private Location location;
    @NotNull
    @Column(name = "completionTime")
    private LocalDateTime completionTime;
    @NotNull
    @Column(name = "registration")
    private LocalDateTime registrationTime;
    @ManyToOne
    @JoinColumn(name = "cargo_id")
    @NotNull
    private Cargo cargo;

    protected HandlingEvent() {
    }

    private HandlingEvent(
            @NotNull Cargo cargo,
            @NotNull LocalDateTime completionTime,
            @NotNull LocalDateTime registrationTime,
            @NotNull Type type,
            @NotNull Location location,
            Voyage voyage) {
        this.voyage = voyage;
        this.completionTime = completionTime.truncatedTo(ChronoUnit.SECONDS);
        this.registrationTime = registrationTime.truncatedTo(ChronoUnit.SECONDS);
        this.type = type;
        this.location = location;
        this.cargo = cargo;
    }

    public static HandlingEvent newHandlingEvent(
            @NotNull Cargo cargo,
            @NotNull LocalDateTime completionTime,
            @NotNull LocalDateTime registrationTime,
            @NotNull Type type,
            @NotNull Location location) {
        if (type.requiresVoyage())
            throw new IllegalArgumentException("Voyage is required for :" + type + " use newVoyageHandlingEvent()");
        return new HandlingEvent(cargo, completionTime, registrationTime, type, location, null);
    }

    public static HandlingEvent newVoyageHandlingEvent(
            @NotNull Cargo cargo,
            @NotNull LocalDateTime completionTime,
            @NotNull LocalDateTime registrationTime,
            @NotNull Type type,
            @NotNull Location location,
            @NotNull Voyage voyage) {
        if (!type.requiresVoyage())
            throw new IllegalArgumentException("Voyage is not allowed for :" + type + " use newHandlingEvent()");
        return new HandlingEvent(cargo, completionTime, registrationTime, type, location, voyage);
    }

    public Type getType() {
        return this.type;
    }

    public Voyage getVoyage() {
        return this.voyage != null ? this.voyage : Voyage.NONE;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public Location getLocation() {
        return this.location;
    }

    public Cargo getCargo() {
        return this.cargo;
    }

    public enum Type {
        LOAD(true),
        UNLOAD(true),
        RECEIVE(false),
        CLAIM(false),
        CUSTOMS(false);

        private final boolean voyageRequired;

        Type(boolean voyageRequired) {
            this.voyageRequired = voyageRequired;
        }

        public boolean requiresVoyage() {
            return voyageRequired;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HandlingEvent that))
            return false;
        return Objects.equals(id, that.id)
                && type == that.type
                && Objects.equals(voyage, that.voyage)
                && Objects.equals(location, that.location)
                && Objects.equals(completionTime, that.completionTime)
                && Objects.equals(registrationTime, that.registrationTime)
                && Objects.equals(cargo, that.cargo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, voyage, location, completionTime, registrationTime, cargo);
    }

    @Override
    public String toString() {
        return "HandlingEvent{" +
                "id=" + id +
                ", type=" + type +
                ", voyage=" + voyage +
                ", location=" + location +
                ", completionTime=" + completionTime +
                ", registrationTime=" + registrationTime +
                ", cargo=" + (cargo == null ? null : cargo.getTrackingId()) +
                '}';
    }
}
