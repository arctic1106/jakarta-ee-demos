package cargotracker.entity.cargo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import cargotracker.entity.location.Location;
import cargotracker.entity.voyage.Voyage;

@Entity
public class Leg {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "voyage_id")
    @NotNull
    private Voyage voyage;
    @ManyToOne
    @JoinColumn(name = "load_location_id")
    @NotNull
    private Location loadLocation;
    @ManyToOne
    @JoinColumn(name = "unload_location_id")
    @NotNull
    private Location unloadLocation;
    @Column(name = "load_time")
    @NotNull
    private LocalDateTime loadTime;
    @Column(name = "unload_time")
    @NotNull
    private LocalDateTime unloadTime;

    public Leg() {
    }

    public Leg(@NotNull Voyage voyage,
            @NotNull Location loadLocation,
            @NotNull Location unloadLocation,
            @NotNull LocalDateTime loadTime,
            @NotNull LocalDateTime unloadTime) {
        this.voyage = voyage;
        this.loadLocation = loadLocation;
        this.unloadLocation = unloadLocation;
        // Workaround to a Hibernate issue.
        this.loadTime = loadTime.truncatedTo(ChronoUnit.SECONDS);
        this.unloadTime = unloadTime.truncatedTo(ChronoUnit.SECONDS);
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public Location getLoadLocation() {
        return loadLocation;
    }

    public Location getUnloadLocation() {
        return unloadLocation;
    }

    public LocalDateTime getLoadTime() {
        return this.loadTime;
    }

    public LocalDateTime getUnloadTime() {
        return this.unloadTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Leg leg))
            return false;
        return Objects.equals(id, leg.id) && Objects.equals(voyage, leg.voyage)
                && Objects.equals(loadLocation, leg.loadLocation) && Objects.equals(unloadLocation, leg.unloadLocation)
                && Objects.equals(loadTime, leg.loadTime) && Objects.equals(unloadTime, leg.unloadTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, voyage, loadLocation, unloadLocation, loadTime, unloadTime);
    }

    @Override
    public String toString() {
        return "Leg{" +
                "id=" + id +
                ", voyage=" + voyage +
                ", loadLocation=" + loadLocation +
                ", unloadLocation=" + unloadLocation +
                ", loadTime=" + loadTime +
                ", unloadTime=" + unloadTime +
                '}';
    }
}
