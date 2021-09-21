package cargotracker.web;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import cargotracker.service.BookingService;
import cargotracker.service.ReferenceDataService;
import cargotracker.entity.cargo.Cargo;
import cargotracker.repository.CargoRepository;
import cargotracker.entity.cargo.TrackingId;
import cargotracker.entity.location.Location;
import cargotracker.entity.location.UnLoCode;

@Named
@ViewScoped
public class ChangeDestination implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private BookingService bookingService;
    @Inject
    private ReferenceDataService referenceData;
    private String trackingId;
    private Cargo cargo;
    private List<Location> locations;
    private String destinationUnlocode;

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public List<Location> getPotentialDestinations() {
        if (cargo == null)
            return List.of();
        String originCode = cargo.getOrigin().getUnLoCode().getCode();
        String destCode = cargo.getRouteSpecification().getDestination().getUnLoCode().getCode();
        return locations.stream()
                .filter(l -> !l.getUnLoCode().getCode().equalsIgnoreCase(originCode)
                        && !l.getUnLoCode().getCode().equalsIgnoreCase(destCode))
                .toList();
    }

    public String getDestinationUnlocode() {
        return destinationUnlocode;
    }

    public void setDestinationUnlocode(String destinationUnlocode) {
        this.destinationUnlocode = destinationUnlocode;
    }

    public void load() {
        locations = referenceData.locations();
        cargo = cargoRepository.find(new TrackingId(trackingId));
    }

    public String changeDestination() {
        bookingService.changeDestination(new TrackingId(trackingId), new UnLoCode(destinationUnlocode));
        return "/admin/dashboard?faces-redirect=true";
    }
}
