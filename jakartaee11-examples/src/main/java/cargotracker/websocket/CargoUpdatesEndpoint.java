package cargotracker.websocket;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cargotracker.entity.cargo.TrackingId;
import cargotracker.repository.CargoRepository;
import cargotracker.web.view.RealtimeCargoTrackingViewAdapter;

// ws://host/websocket/cargo/{trackingId}: sends the current state on connect, then
// a JSON message every time that cargo changes (pushed by CargoUpdateNotifier).
// Endpoints are CDI-enabled: field injection works like in any other bean.
@ServerEndpoint("/websocket/cargo/{trackingId}")
public class CargoUpdatesEndpoint {
    @Inject
    private Logger logger;
    @Inject
    private CargoSubscriptions subscriptions;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private Jsonb jsonb;

    @OnOpen
    public void open(Session session, @PathParam("trackingId") String trackingId) throws IOException {
        var cargo = cargoRepository.find(new TrackingId(trackingId));
        if (cargo == null) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Unknown cargo " + trackingId));
            return;
        }
        subscriptions.subscribe(trackingId, session);
        session.getAsyncRemote().sendText(jsonb.toJson(new RealtimeCargoTrackingViewAdapter(cargo)));
    }

    @OnMessage
    public String ping(String message) {
        return "pong";
    }

    @OnClose
    public void close(Session session, @PathParam("trackingId") String trackingId) {
        subscriptions.unsubscribe(trackingId, session);
    }

    @OnError
    public void error(Session session, @PathParam("trackingId") String trackingId, Throwable error) {
        logger.log(Level.FINE, "WebSocket error for cargo " + trackingId, error);
        subscriptions.unsubscribe(trackingId, session);
    }
}
