package cargotracker.audit;

import java.time.Instant;

// A Java record used as a Jakarta Data entity. Jakarta Persistence itself does not
// allow records as entities; Open Liberty's built-in Jakarta Data provider does,
// as long as the repository's dataStore is a DataSource (not a persistence unit).
// No annotations: the "id" component is the id, other components are columns.
// Liberty generates a JPA entity named AuditEntryEntity and creates its table.
//
// The id is a String (UUID text), not java.util.UUID: for record entities the
// built-in provider generated "ID NUMBER(16)" for UUID, which HSQLDB rejects. The
// CREATE TABLE failure is only visible with trace eclipselink=all
// (eclipselink.ps.ddl), the first query then fails with "object not found".
public record AuditEntry(
        String id,
        String trackingId,
        String transportStatus,
        String routingStatus,
        boolean misdirected,
        String lastKnownLocation,
        Instant recordedAt) {
}
