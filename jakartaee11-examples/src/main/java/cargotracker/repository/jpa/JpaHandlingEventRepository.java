package cargotracker.repository.jpa;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import cargotracker.entity.cargo.TrackingId;
import cargotracker.entity.handling.HandlingEvent;
import cargotracker.repository.HandlingEventRepository;
import cargotracker.entity.handling.HandlingHistory;

@ApplicationScoped
public class JpaHandlingEventRepository implements HandlingEventRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void store(HandlingEvent event) {
        entityManager.persist(event);
    }

    @Override
    public HandlingHistory lookupHandlingHistoryOfCargo(TrackingId trackingId) {
        return new HandlingHistory(entityManager
                .createNamedQuery("HandlingEvent.findByTrackingId", HandlingEvent.class)
                .setParameter("trackingId", trackingId).getResultList()
        );
    }
}