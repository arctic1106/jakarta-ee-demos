package cargotracker.audit;

import jakarta.data.Limit;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import java.util.List;

// dataStore is the DataSource JNDI name, so Liberty manages the table for the
// record entity itself (see <data createTables="..."/> in server.xml).
@Repository(dataStore = "jdbc/CargoTrackerDatabase")
public interface AuditLog {

    @Insert
    void record(AuditEntry entry);

    @Find
    @OrderBy(value = "recordedAt", descending = true)
    List<AuditEntry> latest(Limit limit);

    @Query("where trackingId = :trackingId order by recordedAt desc")
    List<AuditEntry> history(String trackingId);
}
