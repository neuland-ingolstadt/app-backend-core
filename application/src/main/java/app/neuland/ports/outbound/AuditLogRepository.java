package app.neuland.ports.outbound;

import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogPage;

public interface AuditLogRepository {
    AuditLogPage findAll(Long limit, String cursor);

    AuditLogEntry save(AuditLogEntry auditLog);
}
