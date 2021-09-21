package cargotracker.web;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import cargotracker.repository.CargoRepository;
import cargotracker.entity.cargo.TrackingId;
import cargotracker.repository.HandlingEventRepository;
import cargotracker.web.view.CargoTrackingViewAdapter;

@Named
@ViewScoped
public class Track implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private HandlingEventRepository handlingEventRepository;
    private String trackingId;
    private CargoTrackingViewAdapter cargo;

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        if (trackingId != null) {
            trackingId = trackingId.trim();
        }
        this.trackingId = trackingId;
    }

    public CargoTrackingViewAdapter getCargo() {
        return cargo;
    }

    public List<String> getTrackingIds() {
        return cargoRepository.findAll().stream()
                .map(domainCargo -> domainCargo.getTrackingId().getIdString())
                .sorted()
                .toList();
    }

    public void onTrackById(String trackingId) {
        setTrackingId(trackingId);
        onTrackById();
    }

    public void onTrackById() {
        var cargoId = new TrackingId(trackingId);
        var domainCargo = cargoRepository.find(cargoId);
        if (domainCargo != null) {
            var events = handlingEventRepository
                    .lookupHandlingHistoryOfCargo(cargoId)
                    .getDistinctEventsByCompletionTime();
            this.cargo = new CargoTrackingViewAdapter(domainCargo, events);
        } else {
            this.cargo = null;
        }
    }
}
