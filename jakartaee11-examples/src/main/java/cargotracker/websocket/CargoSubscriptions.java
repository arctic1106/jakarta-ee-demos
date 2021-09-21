package cargotracker.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// WebSocket sessions grouped by the tracking id they subscribed to.
@ApplicationScoped
public class CargoSubscriptions {
    private final Map<String, Set<Session>> sessionsByTrackingId = new ConcurrentHashMap<>();

    public void subscribe(String trackingId, Session session) {
        sessionsByTrackingId.computeIfAbsent(trackingId, id -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void unsubscribe(String trackingId, Session session) {
        sessionsByTrackingId.computeIfPresent(trackingId, (id, sessions) -> {
            sessions.remove(session);
            return sessions.isEmpty() ? null : sessions;
        });
    }

    public void publish(String trackingId, String json) {
        sessionsByTrackingId.getOrDefault(trackingId, Set.of()).stream()
                .filter(Session::isOpen)
                .forEach(session -> session.getAsyncRemote().sendText(json));
    }
}
