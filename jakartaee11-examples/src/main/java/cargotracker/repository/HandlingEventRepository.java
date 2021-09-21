package cargotracker.repository;

import cargotracker.entity.cargo.TrackingId;
import cargotracker.entity.handling.HandlingEvent;
import cargotracker.entity.handling.HandlingHistory;

public interface HandlingEventRepository {

    void store(HandlingEvent event);

    HandlingHistory lookupHandlingHistoryOfCargo(TrackingId trackingId);
}
