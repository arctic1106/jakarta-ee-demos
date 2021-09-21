package cargotracker.entity.location;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Embeddable
public class UnLoCode {
    private static final java.util.regex.Pattern VALID_PATTERN = java.util.regex.Pattern
            .compile("[a-zA-Z]{2}[a-zA-Z2-9]{3}");
    @NotEmpty(message = "Location code must not be empty.")
    @Pattern(regexp = "[a-zA-Z]{2}[a-zA-Z2-9]{3}")
    private String code;

    protected UnLoCode() {
    }

    public UnLoCode(String countryAndLocation) {
        requireNonNull(countryAndLocation, "Country and location may not be null.");
        if (!VALID_PATTERN.matcher(countryAndLocation).matches())
            throw new IllegalArgumentException(
                    countryAndLocation + " is not a valid UN/LOCODE (does not match pattern)");
        code = countryAndLocation.toUpperCase();
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UnLoCode unLoCode))
            return false;
        return Objects.equals(code, unLoCode.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
