package cargotracker.repository;

import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.persistence.PersistenceUnit;

import java.time.LocalDate;

import cargotracker.entity.cargo.Cargo;
import cargotracker.entity.cargo.RoutingStatus;
import cargotracker.entity.cargo.TransportStatus;

// Read-side queries over the Cargo aggregate. Writes stay in CargoRepository
// (EntityManager based) because they also fire CDI events.
@PersistenceUnit(name = Persistence.UNIT_REF, unitName = Persistence.UNIT_NAME)
@Repository(dataStore = Persistence.UNIT_REF)
public interface CargoQueries extends DataRepository<Cargo, Long> {

    // Offset pagination: PageRequest.ofPage(1).size(10). Page carries totals.
    @Find
    @OrderBy("id")
    Page<Cargo> findAll(PageRequest pageRequest);

    @Query("select count(this) where delivery.transportStatus = :status")
    long countByTransportStatus(TransportStatus status);

    @Query("select count(this) where delivery.routingStatus = :status")
    long countByRoutingStatus(RoutingStatus status);

    @Query("""
            select count(this)
             where routeSpecification.arrivalDeadline < :today
               and delivery.transportStatus <> :claimed
            """)
    long countOverdue(LocalDate today, TransportStatus claimed);
}
