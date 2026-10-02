package app.neuland.model.auditlog;

import java.time.Instant;

public final class AuditLogFixtures {

    private AuditLogFixtures() {
    }

    public static AuditLogEntry sample(Long id, AuditLogOperation operation) {
        return new AuditLogEntry(
                id,
                "Announcement",
                42L,
                operation,
                "title",
                "user-1",
                Instant.parse("2026-01-01T12:00:00Z")
        );
    }
}
