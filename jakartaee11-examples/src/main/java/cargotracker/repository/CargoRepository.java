package cargotracker.repository;

import java.util.List;

import cargotracker.entity.cargo.Cargo;
import cargotracker.entity.cargo.TrackingId;

public interface CargoRepository {

    Cargo find(TrackingId trackingId);

    List<Cargo> findAll();

    void store(Cargo cargo);

    TrackingId nextTrackingId();
}
