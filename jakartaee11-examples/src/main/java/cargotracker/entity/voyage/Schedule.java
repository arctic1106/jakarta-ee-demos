package cargotracker.entity.voyage;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Schedule {
    public static final Schedule EMPTY = new Schedule();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "voyage_id")
    @OrderColumn(name = "movement_order")
    @NotNull
    @Size(min = 1)
    private List<CarrierMovement> carrierMovements = new ArrayList<>();

    public Schedule() {
    }

    Schedule(List<CarrierMovement> carrierMovements) {
        if (Objects.requireNonNull(carrierMovements).contains(null) || carrierMovements.isEmpty())
            throw new IllegalArgumentException("Carrier Movements contains null");
        this.carrierMovements = new ArrayList<>(carrierMovements);
    }

    public List<CarrierMovement> getCarrierMovements() {
        return Collections.unmodifiableList(carrierMovements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Schedule schedule))
            return false;
        return Objects.equals(carrierMovements, schedule.carrierMovements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(carrierMovements);
    }
}
