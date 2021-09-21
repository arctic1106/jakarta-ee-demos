package cargotracker.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class SampleDataGenerator {
    @Inject
    private Logger logger;
    @Inject
    private SampleDataService sampleDataService;

    // CDI 4.0+: jakarta.enterprise.event.Startup replaces the older
    // @Observes @Initialized(ApplicationScoped.class) Object idiom.
    public void onStartup(@Observes Startup startup) {
        try {
            sampleDataService.loadSampleData();
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Sample data seeding failed", e);
        }
    }
}
