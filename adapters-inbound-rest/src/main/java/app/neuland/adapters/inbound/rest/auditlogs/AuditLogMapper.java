package app.neuland.adapters.inbound.rest.auditlogs;

import app.neuland.model.auditlog.AuditLogPage;
import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogOperation;

import app.neuland.backend.core.api.v0.model.AuditLogListResponse;
import app.neuland.backend.core.api.v0.model.AuditLogOperationEnum;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

record AuditLogMapper() {

    static AuditLogListResponse mapToAuditLogListResponse(AuditLogPage auditLogPage) {
        AuditLogListResponse auditLogListResponse = new AuditLogListResponse(
            auditLogPage.auditLogs()
            .stream()
            .map(AuditLogMapper::mapToAuditLogEntry)
            .toList()
        );

        auditLogListResponse.setNextCursor(auditLogPage.nextCursor());

        return auditLogListResponse;
    }

    private static app.neuland.backend.core.api.v0.model.AuditLogEntry mapToAuditLogEntry(AuditLogEntry auditLogEntry) {
        app.neuland.backend.core.api.v0.model.AuditLogEntry apiAuditLogEntry =
                new app.neuland.backend.core.api.v0.model.AuditLogEntry(
                        auditLogEntry.id(),
                        auditLogEntry.entity(),
                        toAuditLogOperationEnum(auditLogEntry.operation()),
                        toOffsetDateTime(auditLogEntry.occurredAt())
                );

        apiAuditLogEntry.setEntityId(auditLogEntry.entityId());
        apiAuditLogEntry.setName(auditLogEntry.name());
        apiAuditLogEntry.setUserId(auditLogEntry.userId());

        return apiAuditLogEntry;
    }

    private static AuditLogOperationEnum toAuditLogOperationEnum(AuditLogOperation operation) {
        return AuditLogOperationEnum.valueOf(operation.name());
    }

    private static OffsetDateTime toOffsetDateTime(Instant occurredAt) {
        return OffsetDateTime.ofInstant(occurredAt, ZoneOffset.UTC);
    }
}
