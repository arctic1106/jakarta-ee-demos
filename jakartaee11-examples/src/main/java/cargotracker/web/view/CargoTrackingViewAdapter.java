package cargotracker.web.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cargotracker.util.DateConverter;
import cargotracker.entity.cargo.Cargo;
import cargotracker.entity.handling.HandlingEvent;
import cargotracker.entity.location.Location;

public class CargoTrackingViewAdapter {
    private final Cargo cargo;
    private final List<HandlingEventViewAdapter> events;

    public CargoTrackingViewAdapter(Cargo cargo, List<HandlingEvent> handlingEvents) {
        this.cargo = cargo;
        this.events = new ArrayList<>(handlingEvents.size());
        handlingEvents.stream().map(HandlingEventViewAdapter::new).forEach(events::add);
    }

    public String getTrackingId() {
        return cargo.getTrackingId().getIdString();
    }

    public String getOriginName() {
        return cargo.getRouteSpecification().getOrigin().getName();
    }

    public String getOriginCode() {
        return cargo.getRouteSpecification().getOrigin().getUnLoCode().getCode();
    }

    public Coordinates getOriginCoordinates() {
        return CoordinatesFactory.find(cargo.getRouteSpecification().getOrigin());
    }

    public String getDestinationName() {
        return cargo.getRouteSpecification().getDestination().getName();
    }

    public String getDestinationCode() {
        return cargo.getRouteSpecification().getDestination().getUnLoCode().getCode();
    }

    public Coordinates getDestinationCoordinates() {
        return CoordinatesFactory.find(cargo.getRouteSpecification().getDestination());
    }

    // Null until the first handling event is registered.
    private Location lastKnownLocation() {
        var location = cargo.getDelivery().getLastKnownLocation();
        return location == null ? Location.UNKNOWN : location;
    }

    public String getLastKnownLocationName() {
        return lastKnownLocation() == Location.UNKNOWN ? "Unknown" : lastKnownLocation().getName();
    }

    public String getLastKnownLocationCode() {
        return lastKnownLocation().getUnLoCode().getCode();
    }

    public Coordinates getLastKnownLocationCoordinates() {
        return CoordinatesFactory.find(lastKnownLocation());
    }

    public String getStatusCode() {
        if (cargo.getItinerary().getLegs().isEmpty())
            return "NOT_ROUTED";
        if (cargo.getDelivery().isUnloadedAtDestination())
            return "AT_DESTINATION";
        if (cargo.getDelivery().isMisdirected())
            return "MISDIRECTED";
        return cargo.getDelivery().getTransportStatus().name();
    }

    public String getStatusText() {
        var delivery = cargo.getDelivery();
        return switch (delivery.getTransportStatus()) {
            case IN_PORT -> "In port " + getLastKnownLocationName();
            case ONBOARD_CARRIER -> delivery.getCurrentVoyage() == null ? "Onboard carrier"
                    : "Onboard voyage " + delivery.getCurrentVoyage().getVoyageNumber().getIdString();
            case CLAIMED -> "Claimed";
            case NOT_RECEIVED -> "Not received";
            case UNKNOWN -> "Unknown";
        };
    }

    public boolean isMisdirected() {
        return cargo.getDelivery().isMisdirected();
    }

    public String getEta() {
        var eta = cargo.getDelivery().getEstimatedTimeOfArrival();
        if (eta == null)
            return "?";
        else
            return DateConverter.toString(eta);
    }

    public String getNextExpectedActivity() {
        var activity = cargo.getDelivery().getNextExpectedActivity();
        if ((activity == null) || (activity.isEmpty()))
            return "";
        var text = "Next expected activity is to ";
        var type = activity.getType();

        return switch (type) {
            case LOAD -> text
                    + type.name().toLowerCase()
                    + " cargo onto voyage "
                    + activity.getVoyage().getVoyageNumber()
                    + " in "
                    + activity.getLocation().getName();
            case UNLOAD -> text
                    + type.name().toLowerCase()
                    + " cargo off of "
                    + activity.getVoyage().getVoyageNumber()
                    + " in "
                    + activity.getLocation().getName();
            default -> text + type.name().toLowerCase() + " cargo in " + activity.getLocation().getName();
        };
    }

    public List<HandlingEventViewAdapter> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public class HandlingEventViewAdapter {
        private final HandlingEvent handlingEvent;

        public HandlingEventViewAdapter(HandlingEvent handlingEvent) {
            this.handlingEvent = handlingEvent;
        }

        public String getTime() {
            return DateConverter.toString(handlingEvent.getCompletionTime());
        }

        public boolean isExpected() {
            return cargo.getItinerary().isExpected(handlingEvent);
        }

        public String getDescription() {
            return switch (handlingEvent.getType()) {
                case LOAD -> "Loaded onto voyage "
                        + handlingEvent.getVoyage().getVoyageNumber().getIdString()
                        + " in "
                        + handlingEvent.getLocation().getName();
                case UNLOAD -> "Unloaded off voyage "
                        + handlingEvent.getVoyage().getVoyageNumber().getIdString()
                        + " in "
                        + handlingEvent.getLocation().getName();
                case RECEIVE -> "Received in " + handlingEvent.getLocation().getName();
                case CLAIM -> "Claimed in " + handlingEvent.getLocation().getName();
                case CUSTOMS -> "Cleared customs in " + handlingEvent.getLocation().getName();
            };
        }
    }
}
