package cargotracker.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

import cargotracker.entity.location.Location;
import cargotracker.entity.voyage.Voyage;
import cargotracker.repository.LocationRepository;
import cargotracker.repository.VoyageRepository;

// Facade over the Jakarta Data repositories for passivating beans
// (@ViewScoped, @FlowScoped, @SessionScoped, @ConversationScoped).
//
// Open Liberty registers repository implementations as synthetic beans that are
// NOT passivation capable, so injecting a repository straight into a passivating
// bean fails deployment with WELD-001413. A normal-scoped bean like this one is
// always passivation capable (it is injected as a serializable client proxy).
@ApplicationScoped
public class ReferenceDataService {
    @Inject
    private LocationRepository locationRepository;
    @Inject
    private VoyageRepository voyageRepository;

    public List<Location> locations() {
        return locationRepository.findAll();
    }

    public List<Voyage> voyages() {
        return voyageRepository.findAll();
    }
}
