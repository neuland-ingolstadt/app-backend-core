package app.neuland.adapters.outbound.persistence.auditlogs;

import app.neuland.model.auditlog.AuditLogOperation;

import java.time.Instant;

final class AuditLogEntityFixtures {

    private AuditLogEntityFixtures() {
    }

    static AuditLogEntity entity(Long id, Instant occurredAt) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.id = id;
        entity.entity = "Announcement";
        entity.entityId = id;
        entity.operation = AuditLogOperation.UPDATE;
        entity.name = "name-" + id;
        entity.userId = "user-" + id;
        entity.occurredAt = occurredAt;
        return entity;
    }
}
