package app.neuland.auditlogs;

import app.neuland.adapters.outbound.persistence.auditlogs.AuditLogEntity;
import app.neuland.adapters.outbound.persistence.auditlogs.AuditLogPanacheRepository;
import app.neuland.model.auditlog.AuditLogOperation;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class AuditLogPanacheRepositoryTest {

    @Inject
    AuditLogPanacheRepository repository;

    @BeforeEach
    @Transactional
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    @Transactional
    void shouldFindFirstPageWithoutCursor() {
        persist("2026-01-01T00:00:00Z");
        persist("2026-01-02T00:00:00Z");
        persist("2026-01-03T00:00:00Z");

        List<AuditLogEntity> page = repository.findPage(2L, null, null);

        assertEquals(3, page.size());
        assertTrue(page.get(0).occurredAt.isAfter(page.get(1).occurredAt)
                || page.get(0).occurredAt.equals(page.get(1).occurredAt));
    }

    @Test
    @Transactional
    void shouldFindPageAfterCursor() {
        AuditLogEntity first = persist("2026-01-01T00:00:00Z");
        AuditLogEntity second = persist("2026-01-02T00:00:00Z");
        AuditLogEntity third = persist("2026-01-03T00:00:00Z");

        List<AuditLogEntity> page = repository.findPage(10L, third.occurredAt, third.id);

        assertEquals(2, page.size());
        assertEquals(second.id, page.get(0).id);
        assertEquals(first.id, page.get(1).id);
    }

    private AuditLogEntity persist(String occurredAt) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.entity = "Announcement";
        entity.entityId = 1L;
        entity.operation = AuditLogOperation.CREATE;
        entity.name = "title";
        entity.userId = "user-1";
        entity.occurredAt = Instant.parse(occurredAt);
        repository.persist(entity);
        return entity;
    }
}
