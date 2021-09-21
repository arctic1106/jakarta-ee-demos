package cargotracker.web;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import cargotracker.service.BookingService;
import cargotracker.entity.cargo.Cargo;
import cargotracker.repository.CargoRepository;
import cargotracker.entity.cargo.Itinerary;
import cargotracker.entity.cargo.TrackingId;

@Named
@ViewScoped
public class ItinerarySelection implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    private BookingService bookingService;
    @Inject
    private CargoRepository cargoRepository;
    private List<Itinerary> routeCandidates;
    private String trackingId;
    private Cargo cargo;

    public List<Itinerary> getRouteCandidates() {
        return routeCandidates;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void load() {
        cargo = cargoRepository.find(new TrackingId(trackingId));
        routeCandidates = bookingService.requestPossibleRoutesForCargo(new TrackingId(trackingId));
    }

    public String assignItinerary(int routeIndex) {
        bookingService.assignCargoToRoute(routeCandidates.get(routeIndex), new TrackingId(trackingId));
        // POST-redirect-GET: the target page is bookmarkable and a refresh won't re-submit.
        return "/admin/show?faces-redirect=true&includeViewParams=false&trackingId=" + trackingId;
    }
}
