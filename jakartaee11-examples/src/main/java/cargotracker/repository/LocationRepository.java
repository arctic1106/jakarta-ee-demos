package cargotracker.repository;

import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.persistence.PersistenceUnit;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import cargotracker.entity.location.Location;
import cargotracker.entity.location.UnLoCode;

// Jakarta Data 1.0 repository: the container generates the implementation.
@PersistenceUnit(name = Persistence.UNIT_REF, unitName = Persistence.UNIT_NAME)
@Repository(dataStore = Persistence.UNIT_REF)
public interface LocationRepository extends DataRepository<Location, Long> {

    // JDQL (Jakarta Data Query Language): select/from are implied by the return type.
    @Query("where unLoCode.code = :code")
    Optional<Location> findByCode(String code);

    @Find
    @OrderBy("name")
    List<Location> findAll();

    // Default methods keep a domain-friendly API on top of generated queries.
    default Location find(UnLoCode unLoCode) {
        return findByCode(unLoCode.getCode())
                .orElseThrow(() -> new NoSuchElementException("Unknown location " + unLoCode));
    }
}
