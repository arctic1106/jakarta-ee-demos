package cargotracker.entity.voyage;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import cargotracker.entity.location.Location;

@Entity
@Table(name = "carrier_movement")
public class CarrierMovement {
    public static final CarrierMovement NONE = new CarrierMovement(Location.UNKNOWN, Location.UNKNOWN,
            LocalDateTime.MIN, LocalDateTime.MIN);
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "departure_location_id")
    @NotNull
    private Location departureLocation;
    @ManyToOne
    @JoinColumn(name = "arrival_location_id")
    @NotNull
    private Location arrivalLocation;
    @Column(name = "departure_time")
    @NotNull
    private LocalDateTime departureTime;
    @Column(name = "arrival_time")
    @NotNull
    private LocalDateTime arrivalTime;

    protected CarrierMovement() {
    }

    public CarrierMovement(
            @NotNull Location departureLocation,
            @NotNull Location arrivalLocation,
            @NotNull LocalDateTime departureTime,
            @NotNull LocalDateTime arrivalTime) {
        this.departureTime = departureTime.truncatedTo(ChronoUnit.SECONDS);
        this.arrivalTime = arrivalTime.truncatedTo(ChronoUnit.SECONDS);
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
    }

    public Location getDepartureLocation() {
        return departureLocation;
    }

    public Location getArrivalLocation() {
        return arrivalLocation;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CarrierMovement that))
            return false;
        return Objects.equals(id, that.id)
                && Objects.equals(departureLocation, that.departureLocation)
                && Objects.equals(arrivalLocation, that.arrivalLocation)
                && Objects.equals(departureTime, that.departureTime)
                && Objects.equals(arrivalTime, that.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, departureLocation, arrivalLocation, departureTime, arrivalTime);
    }
}
