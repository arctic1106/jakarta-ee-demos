package cargotracker.rest.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record BookingRequest(
        @NotNull @Pattern(regexp = "[a-zA-Z]{2}[a-zA-Z2-9]{3}", message = "Invalid origin UN location code.")
        String origin,

        @NotNull @Pattern(regexp = "[a-zA-Z]{2}[a-zA-Z2-9]{3}", message = "Invalid destination UN location code.")
        String destination,

        @NotNull @Future(message = "Arrival deadline must be in the future.")
        LocalDate arrivalDeadline) {

    // Cross-field rule as a boolean "property"; Bean Validation reports it on 'distinct'.
    @AssertTrue(message = "Origin and destination must differ.")
    public boolean isDistinct() {
        return origin == null || !origin.equalsIgnoreCase(destination);
    }
}
