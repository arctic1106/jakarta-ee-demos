package cargotracker.rest;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import cargotracker.entity.cargo.Cargo;
import cargotracker.entity.cargo.TrackingId;
import cargotracker.repository.CargoRepository;
import cargotracker.web.view.RealtimeCargoTrackingViewAdapter;

// Server-Sent Events hub for the live map. CargoUpdateNotifier calls broadcast()
// after each committed cargo change.
@ApplicationScoped
@Path("/cargo")
@Tag(name = "Live tracking")
public class RealtimeCargoTrackingService {
    @Inject
    private Logger logger;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private Jsonb jsonb;
    @Context
    private Sse sse;
    private SseBroadcaster broadcaster;

    @PostConstruct
    void init() {
        broadcaster = sse.newBroadcaster();
        broadcaster.onError((sink, failure) -> logger.log(Level.FINE, "SSE client dropped", failure));
    }

    @PreDestroy
    void close() {
        broadcaster.close();
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Operation(summary = "Stream all cargo: current state first, then every committed change")
    public void tracking(@Context SseEventSink eventSink) {
        CompletionStage<?> sends = CompletableFuture.completedFuture(null);
        for (Cargo cargo : cargoRepository.findAll()) {
            OutboundSseEvent event = toEvent(jsonb.toJson(new RealtimeCargoTrackingViewAdapter(cargo)));
            sends = sends.thenCompose(ignored -> eventSink.send(event));
        }
        sends.whenComplete((ignored, failure) -> {
            if (failure == null && !eventSink.isClosed())
                broadcaster.register(eventSink);
            else if (failure != null)
                logger.log(Level.FINE, "SSE client disconnected during initial state", failure);
        });
    }

    @GET
    @Path("{trackingId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Current tracking state of one cargo")
    public Response find(@PathParam("trackingId") String trackingId) {
        Cargo cargo = cargoRepository.find(new TrackingId(trackingId));
        if (cargo == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(new RealtimeCargoTrackingViewAdapter(cargo)).build();
    }

    public void broadcast(String json) {
        broadcaster.broadcast(toEvent(json));
    }

    private OutboundSseEvent toEvent(String json) {
        return sse.newEventBuilder()
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(String.class, json)
                .build();
    }
}
