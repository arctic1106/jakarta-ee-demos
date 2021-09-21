package cargotracker.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cargotracker.entity.cargo.Cargo;
import cargotracker.entity.cargo.TrackingId;
import cargotracker.entity.handling.HandlingEvent;
import cargotracker.entity.location.Location;
import cargotracker.repository.HandlingEventRepository;
import cargotracker.entity.location.UnLoCode;
import cargotracker.entity.voyage.Voyage;
import cargotracker.entity.voyage.VoyageNumber;
import cargotracker.repository.CargoRepository;
import cargotracker.repository.LocationRepository;
import cargotracker.repository.VoyageRepository;

@ApplicationScoped
@Transactional
public class HandlingEventService {
    @Inject
    private Logger logger;
    @Inject
    private CargoInspectionService cargoInspectionService;
    @Inject
    private HandlingEventRepository handlingEventRepository;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private VoyageRepository voyageRepository;
    @Inject
    private LocationRepository locationRepository;

    public void registerHandlingEvent(
            LocalDateTime completionTime,
            TrackingId trackingId,
            VoyageNumber voyageNumber,
            UnLoCode unLocode,
            HandlingEvent.Type type) {
        var registrationTime = LocalDateTime.now();
        var event = createEvent(
                registrationTime,
                completionTime,
                trackingId,
                voyageNumber,
                unLocode,
                type);
        handlingEventRepository.store(event);
        cargoInspectionService.inspectCargo(trackingId);
        logger.log(Level.INFO, "Registered {0} event for cargo {1}", new Object[] { type, trackingId });
    }

    public HandlingEvent createEvent(
            LocalDateTime registrationTime,
            LocalDateTime completionTime,
            TrackingId trackingId,
            VoyageNumber voyageNumber,
            UnLoCode unLocode,
            HandlingEvent.Type type) {
        Cargo cargo = cargoRepository.find(trackingId);
        if (cargo == null)
            throw new NoSuchElementException("Unknown cargo " + trackingId);
        Voyage voyage = voyageNumber == null ? null : voyageRepository.find(voyageNumber);
        Location location = locationRepository.find(unLocode);

        if (voyage == null) {
            return HandlingEvent.newHandlingEvent(cargo, completionTime, registrationTime, type, location);
        }
        return HandlingEvent.newVoyageHandlingEvent(
                cargo, completionTime, registrationTime, type, location, voyage);
    }
}
