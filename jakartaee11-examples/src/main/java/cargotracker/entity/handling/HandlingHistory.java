package cargotracker.entity.handling;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class HandlingHistory {
    public static final HandlingHistory EMPTY = new HandlingHistory(List.of());
    private static final Comparator<HandlingEvent> BY_COMPLETION_TIME_COMPARATOR = Comparator.comparing(HandlingEvent::getCompletionTime);
    private final List<HandlingEvent> handlingEvents;

    public HandlingHistory(Collection<HandlingEvent> handlingEvents) {
        this.handlingEvents = new ArrayList<>(requireNonNull(handlingEvents, "Handling events are required."));
    }

    public List<HandlingEvent> getDistinctEventsByCompletionTime() {
        List<HandlingEvent> ordered = new ArrayList<>(new HashSet<>(handlingEvents));
        ordered.sort(BY_COMPLETION_TIME_COMPARATOR);
        return Collections.unmodifiableList(ordered);
    }

    public HandlingEvent getMostRecentlyCompletedEvent() {
        List<HandlingEvent> distinctEvents = getDistinctEventsByCompletionTime();
        if (distinctEvents.isEmpty())
            return null;
        else
            return distinctEvents.getLast();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HandlingHistory that))
            return false;
        return Objects.equals(handlingEvents, that.handlingEvents);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(handlingEvents);
    }
}
