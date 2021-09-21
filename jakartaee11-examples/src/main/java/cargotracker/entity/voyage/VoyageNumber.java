package cargotracker.entity.voyage;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

@Embeddable
public class VoyageNumber {
    @Column(name = "voyage_number")
    @NotEmpty(message = "Voyage number cannot be empty.")
    private String number;

    public VoyageNumber() {
    }

    public VoyageNumber(String number) {
        Objects.requireNonNull(number);
        this.number = number;
    }

    @Override
    public String toString() {
        return number;
    }

    public String getIdString() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof VoyageNumber that))
            return false;
        return Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}
