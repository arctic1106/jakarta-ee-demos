package cargotracker.entity.voyage;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cargotracker.entity.location.Location;

@Entity
@Cacheable
@NamedQuery(name = "Voyage.findByVoyageNumber", query = "Select v from Voyage v where v.voyageNumber = :voyageNumber")
@NamedQuery(name = "Voyage.findAll", query = "Select v from Voyage v order by v.voyageNumber")
public class Voyage {
    public static final Voyage NONE = new Voyage(new VoyageNumber(""), Schedule.EMPTY);
    @Id
    @GeneratedValue
    private Long id;
    @Embedded
    @NotNull(message = "Voyage number is required.")
    private VoyageNumber voyageNumber;
    @Embedded
    @NotNull(message = "Schedule is required.")
    private Schedule schedule;

    protected Voyage() {
    }

    private Voyage(VoyageNumber voyageNumber, Schedule schedule) {
        Objects.requireNonNull(voyageNumber, "Voyage number is required.");
        Objects.requireNonNull(schedule, "Schedule is required.");
        this.voyageNumber = voyageNumber;
        this.schedule = schedule;
    }

    public VoyageNumber getVoyageNumber() {
        return voyageNumber;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Voyage voyage))
            return false;
        return Objects.equals(voyageNumber, voyage.voyageNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(voyageNumber);
    }

    @Override
    public String toString() {
        return "Voyage " + voyageNumber;
    }

    public static class Builder {
        private final List<CarrierMovement> carrierMovements = new ArrayList<>();
        private VoyageNumber voyageNumber;
        private Location departureLocation;

        public Builder(String voyageNumber, Location departureLocation) {
            Objects.requireNonNull(voyageNumber, "Voyage number is required");
            Objects.requireNonNull(departureLocation, "Departure location is required");
            this.voyageNumber = new VoyageNumber(voyageNumber);
            this.departureLocation = departureLocation;
        }

        public Builder addMovement(Location arrivalLocation, LocalDateTime departureTime, LocalDateTime arrivalTime) {
            carrierMovements.add(new CarrierMovement(departureLocation, arrivalLocation, departureTime, arrivalTime));
            this.departureLocation = arrivalLocation;
            return this;
        }

        public Voyage build() {
            return new Voyage(voyageNumber, new Schedule(carrierMovements));
        }
    }
}
