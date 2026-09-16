package app.neuland.model.auditlog;

import java.time.Instant;

public record AuditLogEntry (
        Long id,
        String entity,
        Long entityId,
        AuditLogOperation operation,
        String name,
        String userId,
        Instant occurredAt
) {}
