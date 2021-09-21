package cargotracker.service;

import jakarta.enterprise.concurrent.Asynchronous;
import jakarta.enterprise.concurrent.Schedule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import cargotracker.entity.cargo.TransportStatus;
import cargotracker.repository.CargoQueries;

// Jakarta Concurrency 3.1 scheduled asynchronous method: a CDI replacement for the
// EJB @Schedule timer. The first invocation (here: the CDI Startup event) activates
// the schedule; the method then re-runs on every cron match. Cron uses 5 fields
// (minute hour day-of-month month day-of-week), so this runs every minute.
@ApplicationScoped
public class OverdueCargoMonitor {
    @Inject
    private Logger logger;
    @Inject
    private CargoQueries cargoQueries;

    private final AtomicLong overdue = new AtomicLong(-1);

    @Asynchronous(
            executor = "java:comp/DefaultManagedScheduledExecutorService",
            runAt = @Schedule(cron = "* * * * *"))
    void check(@Observes Startup startup) {
        try {
            long count = cargoQueries.countOverdue(LocalDate.now(), TransportStatus.CLAIMED);
            if (overdue.getAndSet(count) != count)
                logger.log(Level.INFO, "Overdue cargo: {0}", count);
        } catch (RuntimeException e) {
            // Never let an exception escape: it would end the schedule.
            logger.log(Level.WARNING, "Overdue check failed", e);
        }
    }

    // -1 until the first run completes.
    public long overdueCount() {
        return overdue.get();
    }
}
