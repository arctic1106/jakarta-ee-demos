package cargotracker.repository;

import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.persistence.PersistenceUnit;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import cargotracker.entity.voyage.Voyage;
import cargotracker.entity.voyage.VoyageNumber;

@PersistenceUnit(name = Persistence.UNIT_REF, unitName = Persistence.UNIT_NAME)
@Repository(dataStore = Persistence.UNIT_REF)
public interface VoyageRepository extends DataRepository<Voyage, Long> {

    @Query("where voyageNumber.number = :number")
    Optional<Voyage> findByNumber(String number);

    @Query("order by voyageNumber.number")
    List<Voyage> findAll();

    default Voyage find(VoyageNumber voyageNumber) {
        return findByNumber(voyageNumber.getIdString())
                .orElseThrow(() -> new NoSuchElementException("Unknown voyage " + voyageNumber));
    }
}
