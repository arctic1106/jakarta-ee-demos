package cargotracker.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

import cargotracker.entity.handling.HandlingEvent;

// JSON-B 3.0 (EE 10+) binds records directly; Bean Validation constraints on record
// components apply to the generated fields and accessors.
//
// {"completionTime":"2026-10-21T10:15:00","trackingId":"ABC123",
//  "eventType":"UNLOAD","unLoCode":"USNYC","voyageNumber":"0100S"}
public record HandlingReport(
        @NotNull(message = "Missing completion time.")
        @PastOrPresent(message = "Completion time cannot be in the future.")
        LocalDateTime completionTime,

        @NotBlank(message = "Missing tracking ID.")
        @Size(min = 4, message = "Tracking ID must be at least four characters.")
        String trackingId,

        @NotNull(message = "Missing event type (RECEIVE, LOAD, UNLOAD, CUSTOMS or CLAIM).")
        HandlingEvent.Type eventType,

        @NotBlank(message = "UN location code missing.")
        @Pattern(regexp = "[a-zA-Z]{2}[a-zA-Z2-9]{3}", message = "Invalid UN location code.")
        String unLoCode,

        @Size(min = 4, max = 5, message = "Voyage number must be four or five characters long.")
        String voyageNumber) {
}
