package cargotracker.entity.location;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

// Reference data, read constantly and almost never written: cache it
// (persistence.xml uses shared-cache-mode ENABLE_SELECTIVE).
@Entity
@Cacheable
@NamedQuery(name = "Location.findAll", query = "Select l from Location l")
@NamedQuery(name = "Location.findByUnLocode", query = "Select l from Location l where l.unLoCode = :unLocode")
public class Location {
    public static final Location UNKNOWN = new Location(new UnLoCode("XXXXX"), "Unknown location");
    @Id
    @GeneratedValue
    private Long id;
    @Embedded
    @NotNull
    @Valid
    private UnLoCode unLoCode;
    @NotEmpty
    private String name;

    protected Location() {
    }

    public Location(UnLoCode unLocode, String name) {
        requireNonNull(unLocode);
        requireNonNull(name);
        this.unLoCode = unLocode;
        this.name = name;
    }

    public UnLoCode getUnLoCode() {
        return unLoCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " [" + unLoCode + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Location location))
            return false;
        // Identity is the UN/LOCODE (natural key), never the generated id: transient,
        // detached and managed copies of the same location must compare equal.
        return Objects.equals(unLoCode, location.unLoCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(unLoCode);
    }
}
