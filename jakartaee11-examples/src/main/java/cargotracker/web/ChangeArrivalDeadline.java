package cargotracker.web;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import cargotracker.service.BookingService;
import cargotracker.entity.cargo.Cargo;
import cargotracker.repository.CargoRepository;
import cargotracker.entity.cargo.TrackingId;

@Named
@ViewScoped
public class ChangeArrivalDeadline implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private BookingService bookingService;
    private String trackingId;
    private Cargo cargo;
    private LocalDate arrivalDeadline;

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public LocalDate getArrivalDeadline() {
        return arrivalDeadline;
    }

    public void setArrivalDeadline(LocalDate arrivalDeadline) {
        this.arrivalDeadline = arrivalDeadline;
    }

    public void load() {
        cargo = cargoRepository.find(new TrackingId(trackingId));
        if (cargo != null)
            arrivalDeadline = cargo.getRouteSpecification().getArrivalDeadline();
    }

    public String changeArrivalDeadline() {
        bookingService.changeDeadline(new TrackingId(trackingId), arrivalDeadline);
        return "/admin/dashboard?faces-redirect=true";
    }
}
