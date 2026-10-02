package app.neuland.implementation.auditlogs;

import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogOperation;
import app.neuland.model.auditlog.AuditLogPage;
import app.neuland.ports.outbound.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static app.neuland.model.auditlog.AuditLogFixtures.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    AuditLogRepository auditLogRepository;

    private AuditLogService service;

    @BeforeEach
    void setUp() {
        service = new AuditLogService(auditLogRepository);
    }

    @Test
    void shouldListWithDefaultLimit() {
        AuditLogPage page = new AuditLogPage(List.of(sample(1L, AuditLogOperation.CREATE)), null);
        when(auditLogRepository.findAll(50L, null)).thenReturn(page);

        assertEquals(page, service.list(null, null));
        verify(auditLogRepository).findAll(eq(50L), isNull());
    }

    @Test
    void shouldListWithCustomLimitAndCursor() {
        String cursor = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("123:1".getBytes(StandardCharsets.UTF_8));
        AuditLogPage page = new AuditLogPage(List.of(sample(2L, AuditLogOperation.UPDATE)), "next");
        when(auditLogRepository.findAll(10L, cursor)).thenReturn(page);

        assertEquals(page, service.list(10L, cursor));
    }

    @Test
    void shouldRejectLimitBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> service.list(0L, null));
    }

    @Test
    void shouldRejectLimitAboveMax() {
        assertThrows(IllegalArgumentException.class, () -> service.list(101L, null));
    }

    @Test
    void shouldRejectInvalidCursor() {
        assertThrows(IllegalArgumentException.class, () -> service.list(10L, "%%%"));
    }

    @Test
    void shouldAppend() {
        AuditLogEntry entry = sample(null, AuditLogOperation.DELETE);
        AuditLogEntry saved = sample(3L, AuditLogOperation.DELETE);
        when(auditLogRepository.save(entry)).thenReturn(saved);

        assertEquals(saved, service.append(entry));
    }
}
