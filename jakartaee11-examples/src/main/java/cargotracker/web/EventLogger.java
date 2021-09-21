package cargotracker.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

import cargotracker.util.DateConverter;
import cargotracker.service.HandlingEventService;
import cargotracker.service.ReferenceDataService;
import cargotracker.repository.CargoRepository;
import cargotracker.entity.cargo.TrackingId;
import cargotracker.entity.cargo.TransportStatus;
import cargotracker.entity.handling.HandlingEvent;
import cargotracker.entity.location.UnLoCode;
import cargotracker.entity.voyage.VoyageNumber;

@Named
@ViewScoped
public class EventLogger implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private ReferenceDataService referenceData;
    @Inject
    private HandlingEventService handlingEventService;
    @Inject
    private FacesContext context;
    private List<SelectItem> trackingIds;
    private List<SelectItem> locations;
    private List<SelectItem> voyages;
    private String trackingId = "";
    private String location = "";
    private String eventType = "";
    private String voyageNumber = "";
    private LocalDateTime completionTime;
    private String currentStep = "trackingIdTab";

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public List<SelectItem> getTrackingIds() {
        return trackingIds;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<SelectItem> getLocations() {
        return locations;
    }

    public List<String> getEventTypes() {
        return Arrays.stream(HandlingEvent.Type.values())
                .map(Enum::name)
                .toList();
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getVoyageNumber() {
        return voyageNumber;
    }

    public void setVoyageNumber(String voyageNumber) {
        this.voyageNumber = voyageNumber;
    }

    public List<SelectItem> getVoyages() {
        return voyages;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    public String getCompletionTimeValue() {
        return DateConverter.toString(completionTime);
    }

    @PostConstruct
    public void init() {
        var cargos = cargoRepository.findAll();
        trackingIds = new ArrayList<>(cargos.size());
        cargos.stream()
                .filter(cargo -> !cargo.getItinerary().getLegs().isEmpty()
                        && !(cargo.getDelivery().getTransportStatus().equals(TransportStatus.CLAIMED)))
                .map(cargo -> cargo.getTrackingId().getIdString())
                .forEachOrdered(trackingId -> trackingIds.add(new SelectItem(trackingId, trackingId)));

        var locations = referenceData.locations();
        this.locations = new ArrayList<>(locations.size());
        locations.forEach(location -> {
            String locationCode = location.getUnLoCode().getCode();
            this.locations.add(new SelectItem(locationCode, location.getName() + " (" + locationCode + ")"));
        });
        var voyages = referenceData.voyages();
        this.voyages = new ArrayList<>(voyages.size());
        voyages.forEach(voyage -> this.voyages
                .add(new SelectItem(voyage.getVoyageNumber().getIdString(), voyage.getVoyageNumber().getIdString())));
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public void nextStep(String newStep) {
        if (!validate(currentStep)) {
            return;
        }
        if ("dateTab".equals(newStep)) {
            completionTime = LocalDateTime.now();
        }
        currentStep = newStep;
    }

    private boolean validate(final String step) {
        if ("eventTypeTab".equals(step) && (eventType == null || eventType.isBlank())) {
            context.addMessage("eventForm:eventType",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Select an event type to continue.", ""));
            return false;
        }
        if ("voyageTab".equals(step)
                && HandlingEvent.Type.valueOf(eventType).requiresVoyage()
                && (voyageNumber == null || voyageNumber.isBlank())) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "When a cargo is LOADed or UNLOADed a Voyage should be selected, please fix errors to continue.",
                    "");
            context.addMessage(null, message);
            return false;
        }
        return true;
    }

    public void submit() {
        VoyageNumber voyage;
        TrackingId trackingId = new TrackingId(this.trackingId);
        UnLoCode location = new UnLoCode(this.location);
        HandlingEvent.Type type = HandlingEvent.Type.valueOf(eventType);
        if (type.requiresVoyage()) {
            voyage = new VoyageNumber(voyageNumber);
        } else {
            voyage = null;
        }
        try {
            handlingEventService.registerHandlingEvent(completionTime, trackingId, voyage, location, type);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Event submitted successfully.", ""));
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to register event: " + e.getMessage(), ""));
        }
        this.trackingId = "";
        this.location = "";
        this.eventType = "";
        this.voyageNumber = "";
        this.completionTime = null;
        this.currentStep = "trackingIdTab";
    }
}
