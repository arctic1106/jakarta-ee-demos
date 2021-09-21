package cargotracker.repository;

// Jakarta Data repositories for *JPA entities* must point at a persistence unit
// reference, not at the DataSource. With dataStore = "jdbc/..." Liberty builds its
// own hidden persistence unit for the repository's entities and creates tables for
// them itself, ignoring persistence.xml.
//
// The reference is declared with @PersistenceUnit on each repository interface
// (identical declarations of the same java:app name are merged by the container).
public final class Persistence {
    public static final String UNIT_NAME = "CargoTrackerUnit";
    public static final String UNIT_REF = "java:app/env/persistence/CargoTrackerUnitRef";

    private Persistence() {
    }
}
