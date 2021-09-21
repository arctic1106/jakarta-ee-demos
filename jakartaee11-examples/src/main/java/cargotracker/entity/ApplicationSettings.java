package cargotracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// Persisted application settings used by the demo environment.
@Entity
public class ApplicationSettings {
    @Id
    private Long id;
    private boolean sampleLoaded = false;

    public boolean isSampleLoaded() {
        return sampleLoaded;
    }

    public void setSampleLoaded(boolean sampleLoaded) {
        this.sampleLoaded = sampleLoaded;
    }
}
