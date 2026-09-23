package app.neuland.ports.inbound;

import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogPage;

public interface AuditLogUseCase {
    AuditLogPage list(Long limit, String cursor);

    AuditLogEntry append(AuditLogEntry auditLog);
}
