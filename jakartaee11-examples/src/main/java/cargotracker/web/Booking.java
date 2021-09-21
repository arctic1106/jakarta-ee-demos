package cargotracker.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.flow.FlowScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import cargotracker.service.BookingService;
import cargotracker.service.ReferenceDataService;
import cargotracker.entity.location.Location;
import cargotracker.entity.location.UnLoCode;

@Named
@FlowScoped("booking")
public class Booking implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final long MIN_JOURNEY_DURATION = 1;
    public static final String EXIT_FLOW = "returnFromBookingFlow";
    @Inject
    private BookingService bookingService;
    @Inject
    private ReferenceDataService referenceData;
    @Inject
    private FacesContext context;
    private LocalDate today = null;
    private List<Location> locations;
    private String originUnlocode = "";
    private String originName;
    private String destinationName;
    private String destinationUnlocode = "";
    private LocalDate arrivalDeadline;
    private boolean bookable = false;
    private long duration = -1;

    @PostConstruct
    public void init() {
        today = LocalDate.now();
        locations = referenceData.locations().stream()
                .sorted(Comparator.comparing(l -> l.getUnLoCode().getCode()))
                .toList();
    }

    public List<Location> getLocations() {
        String toRemove = null;
        if (context.getViewRoot().getViewId().endsWith("destination.xhtml"))
            toRemove = originUnlocode;
        else if (destinationUnlocode != null)
            toRemove = destinationUnlocode;
        final String code = toRemove;
        if (code == null)
            return locations;
        return locations.stream()
                .filter(l -> !l.getUnLoCode().getCode().equalsIgnoreCase(code))
                .toList();
    }

    public String getOriginUnlocode() {
        return originUnlocode;
    }

    public void setOriginUnlocode(String originUnlocode) {
        this.originUnlocode = originUnlocode;
        this.originName = locations.stream()
                .filter(l -> l.getUnLoCode().getCode().equalsIgnoreCase(originUnlocode))
                .findAny().map(Location::getName).orElse(originUnlocode);
    }

    public String getOriginName() {
        return originName;
    }

    public String getDestinationUnlocode() {
        return destinationUnlocode;
    }

    public void setDestinationUnlocode(String destinationUnlocode) {
        this.destinationUnlocode = destinationUnlocode;
        this.destinationName = locations.stream()
                .filter(l -> l.getUnLoCode().getCode().equalsIgnoreCase(destinationUnlocode))
                .findAny().map(Location::getName).orElse(destinationUnlocode);
    }

    public String getDestinationName() {
        return destinationName;
    }

    public LocalDate getToday() {
        return today;
    }

    public LocalDate getArrivalDeadline() {
        return arrivalDeadline;
    }

    public void setArrivalDeadline(LocalDate arrivalDeadline) {
        this.arrivalDeadline = arrivalDeadline;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isBookable() {
        return bookable;
    }

    public void deadlineUpdated(AjaxBehaviorEvent event) {
        // The <f:ajax render="..."> attribute already re-renders the panels.
        duration = arrivalDeadline == null ? -1 : ChronoUnit.DAYS.between(today, arrivalDeadline);
        bookable = duration >= MIN_JOURNEY_DURATION;
    }

    // Faces Flows: leave the flow through its <flow-return> node so the
    // @FlowScoped bean is destroyed. Returning a plain view id (e.g.
    // "/admin/dashboard.xhtml") would navigate away but keep the flow alive.
    public String register() {
        if (originUnlocode.equals(destinationUnlocode)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Origin and destination cannot be the same.", null));
            return null;
        }
        var trackingId = bookingService.bookNewCargo(
                new UnLoCode(originUnlocode), new UnLoCode(destinationUnlocode), arrivalDeadline);
        context.addMessage(null, new FacesMessage("Booked cargo " + trackingId + ". Route it next."));
        // Survive the redirect issued by the flow return.
        context.getExternalContext().getFlash().setKeepMessages(true);
        return EXIT_FLOW;
    }

    // Referenced by booking-flow.xml.
    public String getReturnValue() {
        return "/admin/dashboard?faces-redirect=true";
    }
}
