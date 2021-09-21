package cargotracker.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import cargotracker.config.Roles;
import cargotracker.entity.cargo.TrackingId;
import cargotracker.entity.location.UnLoCode;
import cargotracker.entity.voyage.VoyageNumber;
import cargotracker.rest.dto.HandlingReport;
import cargotracker.service.HandlingEventService;

// curl -u handler:handler -H 'Content-Type: application/json' \
//   -d '{"completionTime":"2026-10-21T10:15:00","trackingId":"ABC123","eventType":"CUSTOMS","unLoCode":"USNYC"}' \
//   http://localhost:9080/rest/handling/reports
@ApplicationScoped
@Path("/handling")
@Tag(name = "Handling")
public class HandlingReportService {
    @Inject
    private HandlingEventService handlingEventService;

    @POST
    @Path("/reports")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(Roles.HANDLER)
    @SecurityRequirement(name = "basicAuth")
    @Operation(summary = "Register a handling event reported by a port or carrier")
    @APIResponse(responseCode = "202", description = "Event registered")
    @APIResponse(responseCode = "400", description = "Invalid report")
    @APIResponse(responseCode = "401", description = "Missing or wrong credentials")
    @APIResponse(responseCode = "404", description = "Unknown cargo, location or voyage")
    public Response submitReport(@NotNull(message = "Missing handling report.") @Valid HandlingReport report) {
        var voyageNumber = report.voyageNumber() == null ? null : new VoyageNumber(report.voyageNumber());
        // Transactions belong to the service layer (HandlingEventService is @Transactional).
        handlingEventService.registerHandlingEvent(
                report.completionTime(),
                new TrackingId(report.trackingId()),
                voyageNumber,
                new UnLoCode(report.unLoCode()),
                report.eventType());
        return Response.accepted().build();
    }
}
