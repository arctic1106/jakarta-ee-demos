package cargotracker.web;

import cargotracker.service.HandlingEventService;
import cargotracker.entity.cargo.Cargo;
import cargotracker.repository.CargoRepository;
import cargotracker.entity.cargo.TrackingId;
import cargotracker.entity.handling.HandlingEvent;
import cargotracker.repository.LocationRepository;
import cargotracker.entity.location.UnLoCode;
import cargotracker.entity.voyage.VoyageNumber;
import cargotracker.repository.VoyageRepository;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import cargotracker.audit.AuditEntry;
import cargotracker.audit.AuditLog;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class CargoDetails {
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private HandlingEventService handlingEventService;
    @Inject
    private LocationRepository locationRepository;
    @Inject
    private VoyageRepository voyageRepository;
    @Inject
    private FacesContext facesContext;
    @Inject
    private AuditLog auditLog;
    private String trackingId;
    private Cargo cargo;
    private String eventType;
    private String locationCode;
    private String voyageNumber;

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
        if (cargo == null)
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Unknown cargo " + trackingId + ".", null));
    }

    public List<AuditEntry> getAuditTrail() {
        return trackingId == null ? List.of() : auditLog.history(trackingId);
    }

    public void select() {
        trackingId = facesContext.getExternalContext()
                .getRequestParameterMap()
                .get("trackingId");
        load();
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getVoyageNumber() {
        return voyageNumber;
    }

    public void setVoyageNumber(String voyageNumber) {
        this.voyageNumber = voyageNumber;
    }

    // LinkedHashMap keeps the enum order in the drop-down (Collectors.toMap does not).
    public Map<String, String> getEventTypes() {
        return Arrays.stream(HandlingEvent.Type.values())
                .collect(Collectors.toMap(Enum::name, Enum::name, (a, b) -> a, LinkedHashMap::new));
    }

    public List<cargotracker.entity.location.Location> getLocations() {
        return locationRepository.findAll();
    }

    public List<cargotracker.entity.voyage.Voyage> getVoyages() {
        return voyageRepository.findAll();
    }

    public boolean isRequiresVoyage() {
        return eventType != null && HandlingEvent.Type.valueOf(eventType).requiresVoyage();
    }

    public String submitStateChange() {
        try {
            cargo = cargoRepository.find(new TrackingId(trackingId));
            if (cargo == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cargo not found.", ""));
                return null;
            }
            UnLoCode unLoCode = new UnLoCode(locationCode);
            HandlingEvent.Type type = HandlingEvent.Type.valueOf(eventType);
            VoyageNumber voyageNum = type.requiresVoyage() ? new VoyageNumber(voyageNumber) : null;
            handlingEventService.registerHandlingEvent(
                    LocalDateTime.now(),
                    cargo.getTrackingId(),
                    voyageNum,
                    unLoCode,
                    type);
            cargo = cargoRepository.find(cargo.getTrackingId());
            facesContext.addMessage(null, new FacesMessage("Handling event registered successfully."));
        } catch (RuntimeException e) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed: " + e.getMessage(), ""));
        }
        return null;
    }
}
