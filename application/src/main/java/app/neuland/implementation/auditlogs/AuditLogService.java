package app.neuland.implementation.auditlog;

import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogPage;
import app.neuland.ports.inbound.AuditLogUseCase;
import app.neuland.ports.outbound.AuditLogRepository;

public class AuditLogService implements AuditLogUseCase {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLogPage list(Long limit, String cursor) {
        return auditLogRepository.findAll(limit, cursor);
    }

    @Override
    public AuditLogEntry append(AuditLogEntry auditLog) {
        return auditLogRepository.save(auditLog);
    }
}
