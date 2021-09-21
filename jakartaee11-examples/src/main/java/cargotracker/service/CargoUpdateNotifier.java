package cargotracker.service;

import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;

import cargotracker.config.VirtualThreads;
import cargotracker.entity.cargo.Cargo;
import cargotracker.event.CargoUpdated;
import cargotracker.rest.RealtimeCargoTrackingService;
import cargotracker.web.view.RealtimeCargoTrackingViewAdapter;
import cargotracker.websocket.CargoSubscriptions;

// Pushes committed cargo changes to SSE (live map) and WebSocket subscribers.
//
// Why not Event.fireAsync() from the repository? Async observers run immediately
// on another thread, while the transaction may still roll back, so clients could
// see changes that never happened. A transactional observer runs after commit; the
// slow network I/O is then handed to a virtual-thread executor.
@ApplicationScoped
public class CargoUpdateNotifier {
    @Inject
    private RealtimeCargoTrackingService sse;
    @Inject
    private CargoSubscriptions webSockets;
    @Inject
    private Jsonb jsonb;
    @Inject
    @VirtualThreads
    private ManagedExecutorService executor;

    void onCargoUpdated(@Observes(during = TransactionPhase.AFTER_SUCCESS) @CargoUpdated Cargo cargo) {
        var trackingId = cargo.getTrackingId().getIdString();
        var json = jsonb.toJson(new RealtimeCargoTrackingViewAdapter(cargo));
        executor.execute(() -> {
            sse.broadcast(json);
            webSockets.publish(trackingId, json);
        });
    }
}
