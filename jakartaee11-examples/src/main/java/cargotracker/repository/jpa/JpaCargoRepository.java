package cargotracker.repository.jpa;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.UUID;

import cargotracker.entity.cargo.Cargo;
import cargotracker.entity.cargo.TrackingId;
import cargotracker.event.CargoUpdated;
import cargotracker.repository.CargoRepository;

@ApplicationScoped
public class JpaCargoRepository implements CargoRepository {
    @PersistenceContext
    private EntityManager entityManager;

    // Fired synchronously; observers that talk to the outside world use
    // @Observes(during = TransactionPhase.AFTER_SUCCESS) so they never publish
    // state from a transaction that later rolls back.
    @Inject
    @CargoUpdated
    private Event<Cargo> cargoUpdated;

    @Override
    public Cargo find(TrackingId trackingId) {
        // getResultStream().findFirst() avoids NoResultException-driven control flow.
        return entityManager
                .createNamedQuery("Cargo.findByTrackingId", Cargo.class)
                .setParameter("trackingId", trackingId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Cargo> findAll() {
        return entityManager
                .createNamedQuery("Cargo.findAll", Cargo.class)
                .getResultList();
    }

    @Override
    public void store(Cargo cargo) {
        // Legs are cascaded from the itinerary; managed cargo is flushed on commit.
        if (!entityManager.contains(cargo))
            entityManager.persist(cargo);
        cargoUpdated.fire(cargo);
    }

    @Override
    public TrackingId nextTrackingId() {
        var random = UUID.randomUUID().toString().toUpperCase();
        return new TrackingId(random.substring(0, random.indexOf("-")));
    }
}
