package cargotracker.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

import java.util.logging.Level;
import java.util.logging.Logger;

import cargotracker.repository.CargoRepository;
import cargotracker.entity.cargo.TrackingId;
import cargotracker.repository.HandlingEventRepository;

@ApplicationScoped
@Transactional
public class CargoInspectionService {
    @Inject
    private Logger logger;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private HandlingEventRepository handlingEventRepository;

    public void inspectCargo(TrackingId trackingId) {
        var cargo = cargoRepository.find(trackingId);
        if (cargo == null) {
            logger.log(Level.WARNING, "Can''t inspect non-existing cargo {0}", trackingId);
            return;
        }
        var handlingHistory = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId);
        cargo.deriveDeliveryProgress(handlingHistory);
        if (cargo.getDelivery().isMisdirected()) logger.log(Level.INFO, "Cargo {0} was misdirected.", cargo);
        if (cargo.getDelivery().isUnloadedAtDestination()) logger.log(Level.INFO, "Cargo {0} has arrived.", cargo);
        cargoRepository.store(cargo);
    }
}
