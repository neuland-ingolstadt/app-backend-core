package app.neuland.model.auditlog;

import java.util.List;

public record AuditLogPage(
        List<AuditLogEntry> auditLogs,
        String nextCursor
) {}
