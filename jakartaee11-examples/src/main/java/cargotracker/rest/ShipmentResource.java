package cargotracker.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import cargotracker.audit.AuditEntry;
import cargotracker.audit.AuditLog;
import cargotracker.config.Roles;
import cargotracker.entity.cargo.TrackingId;
import cargotracker.entity.location.UnLoCode;
import cargotracker.repository.CargoQueries;
import cargotracker.repository.CargoRepository;
import cargotracker.rest.dto.BookingRequest;
import cargotracker.rest.dto.PageResponse;
import cargotracker.rest.dto.ShipmentSummary;
import cargotracker.service.BookingService;

@ApplicationScoped
@Path("/shipments")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Shipments")
public class ShipmentResource {
    @Inject
    private CargoQueries cargoQueries;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private BookingService bookingService;
    @Inject
    private AuditLog auditLog;

    @GET
    @Operation(summary = "List shipments, one page at a time (Jakarta Data pagination)")
    public PageResponse<ShipmentSummary> list(
            @QueryParam("page") @DefaultValue("1") @Min(1) long page,
            @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size) {
        // Jakarta Data pages are 1-based.
        return PageResponse.of(cargoQueries.findAll(PageRequest.ofPage(page).size(size)), ShipmentSummary::of);
    }

    @GET
    @Path("{trackingId}")
    @Operation(summary = "One shipment")
    // Declaring any @APIResponse replaces the generated default, so list 200 too.
    @APIResponse(responseCode = "200", description = "The shipment")
    @APIResponse(responseCode = "404", description = "Unknown tracking id")
    public ShipmentSummary get(@PathParam("trackingId") String trackingId) {
        var cargo = cargoRepository.find(new TrackingId(trackingId));
        if (cargo == null)
            throw new NoSuchElementException("Unknown cargo " + trackingId);
        return ShipmentSummary.of(cargo);
    }

    @GET
    @Path("{trackingId}/audit")
    @Operation(summary = "Committed status changes of one shipment, newest first")
    public List<AuditEntry> audit(@PathParam("trackingId") String trackingId) {
        return auditLog.history(trackingId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(Roles.ADMIN)
    @SecurityRequirement(name = "basicAuth")
    @Operation(summary = "Book a new shipment")
    @APIResponse(responseCode = "201", description = "Booked; Location header points to the shipment")
    @APIResponse(responseCode = "400", description = "Invalid booking")
    public Response book(@NotNull @Valid BookingRequest request, @Context UriInfo uriInfo) {
        var trackingId = bookingService.bookNewCargo(
                new UnLoCode(request.origin()),
                new UnLoCode(request.destination()),
                request.arrivalDeadline());
        var location = uriInfo.getAbsolutePathBuilder().path(trackingId.getIdString()).build();
        return Response.created(location).entity(get(trackingId.getIdString())).build();
    }
}
