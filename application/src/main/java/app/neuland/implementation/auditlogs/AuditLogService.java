package app.neuland.implementation.auditlogs;

import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogPage;
import app.neuland.ports.inbound.AuditLogUseCase;
import app.neuland.ports.outbound.AuditLogRepository;

import java.util.Base64;

public class AuditLogService implements AuditLogUseCase {

    private static final long DEFAULT_LIMIT = 50;
    private static final long MAX_LIMIT = 100;

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLogPage list(Long limit, String cursor) {
        long effectiveLimit;

        if (limit == null) {
            effectiveLimit = DEFAULT_LIMIT;
        } else {
            effectiveLimit = limit;
        }

        if (effectiveLimit < 1 || effectiveLimit > MAX_LIMIT) {
            throw new IllegalArgumentException(
                    "Limit must be between 1 and " + MAX_LIMIT
            );
        }

        if (cursor != null && !isValidCursor(cursor)) {
            throw new IllegalArgumentException("Invalid cursor");
        }

        return auditLogRepository.findAll(effectiveLimit, cursor);
    }

    @Override
    public AuditLogEntry append(AuditLogEntry auditLog) {
        return auditLogRepository.save(auditLog);
    }

    private boolean isValidCursor(String cursor) {
        try {
            Base64.getUrlDecoder().decode(cursor);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}