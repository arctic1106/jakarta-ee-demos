package cargotracker.web.view;

import java.util.EnumMap;
import java.util.Map;

import cargotracker.entity.cargo.Cargo;
import cargotracker.entity.cargo.RoutingStatus;
import cargotracker.entity.cargo.TransportStatus;
import cargotracker.entity.location.Location;

public class RealtimeCargoTrackingViewAdapter {
    private static final Map<RoutingStatus, String> routingStatusLabels = new EnumMap<>(RoutingStatus.class);
    private static final Map<TransportStatus, String> transportStatusLabels = new EnumMap<>(TransportStatus.class);
    private final Cargo cargo;

    static {
        routingStatusLabels.put(RoutingStatus.NOT_ROUTED, "Not routed");
        routingStatusLabels.put(RoutingStatus.ROUTED, "Routed");
        routingStatusLabels.put(RoutingStatus.MISROUTED, "Misrouted");
        transportStatusLabels.put(TransportStatus.NOT_RECEIVED, "Not received");
        transportStatusLabels.put(TransportStatus.IN_PORT, "In port");
        transportStatusLabels.put(TransportStatus.ONBOARD_CARRIER, "Onboard carrier");
        transportStatusLabels.put(TransportStatus.CLAIMED, "Claimed");
        transportStatusLabels.put(TransportStatus.UNKNOWN, "Unknown");
    }

    public RealtimeCargoTrackingViewAdapter(Cargo cargo) {
        this.cargo = cargo;
    }

    public String getTrackingId() {
        return cargo.getTrackingId().getIdString();
    }

    public String getRoutingStatus() {
        return routingStatusLabels.get(cargo.getDelivery().getRoutingStatus());
    }

    public boolean isMisdirected() {
        return cargo.getDelivery().isMisdirected();
    }

    public String getTransportStatus() {
        return transportStatusLabels.get(cargo.getDelivery().getTransportStatus());
    }

    public boolean isAtDestination() {
        return cargo.getDelivery().isUnloadedAtDestination();
    }

    public LocationViewAdapter getOrigin() {
        return new LocationViewAdapter(cargo.getOrigin());
    }

    public LocationViewAdapter getDestination() {
        return new LocationViewAdapter(cargo.getRouteSpecification().getDestination());
    }

    public LocationViewAdapter getLastKnownLocation() {
        Location lastKnownLocation = cargo.getDelivery().getLastKnownLocation();
        return new LocationViewAdapter(lastKnownLocation == null ? Location.UNKNOWN : lastKnownLocation);
    }

    public LocationViewAdapter getLocation() {
        return cargo.getDelivery().getTransportStatus() == TransportStatus.NOT_RECEIVED ? getOrigin()
                : getLastKnownLocation();
    }

    public String getStatusCode() {
        var routingStatus = cargo.getDelivery().getRoutingStatus();
        if (routingStatus == RoutingStatus.NOT_ROUTED || routingStatus == RoutingStatus.MISROUTED)
            return routingStatus.toString();
        if (cargo.getDelivery().isMisdirected())
            return "MISDIRECTED";
        if (cargo.getDelivery().isUnloadedAtDestination())
            return "AT_DESTINATION";
        return cargo.getDelivery().getTransportStatus().toString();
    }
}
