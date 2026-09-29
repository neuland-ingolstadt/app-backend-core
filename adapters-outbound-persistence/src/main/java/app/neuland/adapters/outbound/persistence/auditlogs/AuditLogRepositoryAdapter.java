package app.neuland.adapters.outbound.persistence.auditlogs;

import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogPage;
import app.neuland.ports.outbound.AuditLogRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final AuditLogPanacheRepository repository;

    public AuditLogRepositoryAdapter(
            AuditLogPanacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditLogPage findAll(Long limit, String cursor) {
        Instant afterOccurredAt = null;
        Long afterId = null;

        if (cursor != null) {
            CursorPosition position = decodeCursor(cursor);
            afterOccurredAt = position.occurredAt();
            afterId = position.id();
        }

        List<AuditLogEntity> rows = repository.findPage(limit, afterOccurredAt, afterId);

        boolean hasMore = rows.size() > limit;
        List<AuditLogEntity> page = hasMore ? rows.subList(0, limit.intValue()) : rows;

        String nextCursor = hasMore ? encodeCursor(page.get(page.size() - 1)) : null;

        return new AuditLogPage(
                page.stream().map(this::toDomain).toList(),
                nextCursor
        );
    }

    @Override
    @Transactional
    public AuditLogEntry save(AuditLogEntry auditLog) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.entity = auditLog.entity();
        entity.entityId = auditLog.entityId();
        entity.operation = auditLog.operation();
        entity.name = auditLog.name();
        entity.userId = auditLog.userId();
        entity.occurredAt = auditLog.occurredAt();

        repository.persist(entity);

        return toDomain(entity);
    }

    private String encodeCursor(AuditLogEntity entity) {
        String raw = entity.occurredAt.toEpochMilli() + ":" + entity.id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private CursorPosition decodeCursor(String cursor) {
        try {
            String raw = new String(
                    Base64.getUrlDecoder().decode(cursor),
                    StandardCharsets.UTF_8
            );

            String[] parts = raw.split(":", 2);

            return new CursorPosition(
                    Instant.ofEpochMilli(Long.parseLong(parts[0])),
                    Long.parseLong(parts[1])
            );
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid cursor: " + cursor, e);
        }
    }

    private AuditLogEntry toDomain(AuditLogEntity entity) {
        return new AuditLogEntry(
                entity.id,
                entity.entity,
                entity.entityId,
                entity.operation,
                entity.name,
                entity.userId,
                entity.occurredAt
        );
    }

    private record CursorPosition(Instant occurredAt, Long id) {}
}
