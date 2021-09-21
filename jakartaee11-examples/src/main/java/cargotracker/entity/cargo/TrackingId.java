package cargotracker.entity.cargo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

@Embeddable
public class TrackingId {
    @Column(name = "tracking_id", unique = true, updatable = false)
    @NotEmpty(message = "Tracking ID cannot be empty.")
    private String id;

    public TrackingId() {
    }

    public TrackingId(@NotEmpty String id) {
        this.id = id;
    }

    public String getIdString() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackingId that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
