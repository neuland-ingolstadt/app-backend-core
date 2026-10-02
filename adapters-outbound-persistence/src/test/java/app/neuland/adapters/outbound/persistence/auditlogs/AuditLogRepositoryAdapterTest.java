package app.neuland.adapters.outbound.persistence.auditlogs;

import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogOperation;
import app.neuland.model.auditlog.AuditLogPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import static app.neuland.adapters.outbound.persistence.auditlogs.AuditLogEntityFixtures.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogRepositoryAdapterTest {

    @Mock
    AuditLogPanacheRepository repository;

    private AuditLogRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AuditLogRepositoryAdapter(repository);
    }

    @Test
    void shouldFindFirstPageWithoutCursor() {
        when(repository.findPage(eq(2L), isNull(), isNull())).thenReturn(List.of(
                entity(3L, Instant.parse("2026-01-03T00:00:00Z")),
                entity(2L, Instant.parse("2026-01-02T00:00:00Z"))
        ));

        AuditLogPage page = adapter.findAll(2L, null);

        assertEquals(2, page.auditLogs().size());
        assertNull(page.nextCursor());
    }

    @Test
    void shouldReturnNextCursorWhenMoreRowsExist() {
        when(repository.findPage(eq(1L), isNull(), isNull())).thenReturn(List.of(
                entity(3L, Instant.parse("2026-01-03T00:00:00Z")),
                entity(2L, Instant.parse("2026-01-02T00:00:00Z"))
        ));

        AuditLogPage page = adapter.findAll(1L, null);

        assertEquals(1, page.auditLogs().size());
        assertEquals(3L, page.auditLogs().getFirst().id());
        String expectedCursor = Base64.getUrlEncoder().withoutPadding()
                .encodeToString((Instant.parse("2026-01-03T00:00:00Z").toEpochMilli() + ":3")
                        .getBytes(StandardCharsets.UTF_8));
        assertEquals(expectedCursor, page.nextCursor());
    }

    @Test
    void shouldDecodeCursorAndPage() {
        Instant occurredAt = Instant.parse("2026-01-02T00:00:00Z");
        String cursor = Base64.getUrlEncoder().withoutPadding()
                .encodeToString((occurredAt.toEpochMilli() + ":2").getBytes(StandardCharsets.UTF_8));
        when(repository.findPage(1L, occurredAt, 2L)).thenReturn(List.of(
                entity(1L, Instant.parse("2026-01-01T00:00:00Z"))
        ));

        AuditLogPage page = adapter.findAll(1L, cursor);

        assertEquals(1, page.auditLogs().size());
        verify(repository).findPage(1L, occurredAt, 2L);
    }

    @Test
    void shouldRejectNullLimit() {
        assertThrows(NullPointerException.class, () -> adapter.findAll(null, null));
    }

    @Test
    void shouldRejectInvalidCursor() {
        assertThrows(IllegalArgumentException.class, () -> adapter.findAll(10L, "not-base64!!!"));
    }

    @Test
    void shouldRejectCursorWithoutSeparator() {
        String cursor = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("noseparator".getBytes(StandardCharsets.UTF_8));

        assertThrows(IllegalArgumentException.class, () -> adapter.findAll(10L, cursor));
    }

    @Test
    void shouldSave() {
        doAnswer(invocation -> {
            AuditLogEntity entity = invocation.getArgument(0);
            entity.id = 50L;
            return null;
        }).when(repository).persist(any(AuditLogEntity.class));

        AuditLogEntry saved = adapter.save(new AuditLogEntry(
                null,
                "RoomReport",
                7L,
                AuditLogOperation.CREATE,
                "A101",
                "user-7",
                Instant.parse("2026-01-01T12:00:00Z")
        ));

        assertEquals(50L, saved.id());
        assertEquals(AuditLogOperation.CREATE, saved.operation());
        verify(repository).persist(any(AuditLogEntity.class));
    }
}
