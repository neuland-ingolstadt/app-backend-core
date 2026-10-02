package app.neuland.adapters.outbound.persistence.roomreports;

import app.neuland.model.roomreport.RoomReport;
import app.neuland.model.roomreport.RoomReportCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static app.neuland.adapters.outbound.persistence.roomreports.RoomReportEntityFixtures.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomReportRepositoryAdapterTest {

    @Mock
    RoomReportPanacheRepository repository;

    private RoomReportRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RoomReportRepositoryAdapter(repository);
    }

    @Test
    void shouldFindById() {
        RoomReportEntity entity = entity(1L, "A101", RoomReportCategory.MISSING, "desc", null);
        when(repository.findById(1L)).thenReturn(entity);

        Optional<RoomReport> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(new RoomReport(1L, "A101", RoomReportCategory.MISSING, "desc", null), result.get());
    }

    @Test
    void shouldReturnEmptyWhenMissing() {
        when(repository.findById(99L)).thenReturn(null);

        assertTrue(adapter.findById(99L).isEmpty());
    }

    @Test
    void shouldFindAll() {
        when(repository.listAll()).thenReturn(List.of(
                entity(1L, "A101", RoomReportCategory.OTHER, null, Instant.parse("2026-01-01T00:00:00Z"))
        ));

        List<RoomReport> reports = adapter.findAll();

        assertEquals(1, reports.size());
        assertEquals(RoomReportCategory.OTHER, reports.getFirst().reason());
    }

    @Test
    void shouldPersistNewReport() {
        doAnswer(invocation -> {
            RoomReportEntity entity = invocation.getArgument(0);
            entity.id = 11L;
            return null;
        }).when(repository).persist(any(RoomReportEntity.class));

        RoomReport saved = adapter.save(
                new RoomReport(null, "B202", RoomReportCategory.WRONG_DESCRIPTION, "text", null)
        );

        assertEquals(11L, saved.id());
        assertEquals("B202", saved.room());
        ArgumentCaptor<RoomReportEntity> captor = ArgumentCaptor.forClass(RoomReportEntity.class);
        verify(repository).persist(captor.capture());
        assertEquals(RoomReportCategory.WRONG_DESCRIPTION, captor.getValue().reason);
    }

    @Test
    void shouldUpdateExistingReport() {
        RoomReportEntity existing = entity(5L, "Old", RoomReportCategory.MISSING, "old", null);
        when(repository.findById(5L)).thenReturn(existing);

        RoomReport saved = adapter.save(
                new RoomReport(5L, "New", RoomReportCategory.NOT_EXISTING, "new", Instant.parse("2026-02-01T00:00:00Z"))
        );

        assertEquals(5L, saved.id());
        assertEquals("New", saved.room());
        assertEquals(RoomReportCategory.NOT_EXISTING, saved.reason());
        assertEquals("new", existing.description);
    }

    @Test
    void shouldThrowWhenUpdatingMissingReport() {
        when(repository.findById(99L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> adapter.save(
                new RoomReport(99L, "A101", RoomReportCategory.MISSING, null, null)
        ));
    }
}
