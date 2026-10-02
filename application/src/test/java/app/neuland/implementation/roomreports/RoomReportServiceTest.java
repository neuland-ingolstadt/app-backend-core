package app.neuland.implementation.roomreports;

import app.neuland.model.roomreport.RoomReport;
import app.neuland.model.roomreport.RoomReportCategory;
import app.neuland.ports.outbound.RoomReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomReportServiceTest {

    @Mock
    RoomReportRepository roomReportRepository;

    private RoomReportService service;

    @BeforeEach
    void setUp() {
        service = new RoomReportService(roomReportRepository);
    }

    @Test
    void shouldListReports() {
        when(roomReportRepository.findAll()).thenReturn(List.of(
                new RoomReport(1L, "A101", RoomReportCategory.WRONG_DESCRIPTION, "desc", null),
                new RoomReport(2L, "B202", RoomReportCategory.WRONG_LOCATION, null, Instant.parse("2026-01-01T00:00:00Z"))
        ));

        List<RoomReport> reports = service.list();

        assertEquals(2, reports.size());
        assertEquals(RoomReportCategory.WRONG_DESCRIPTION, reports.get(0).reason());
        assertEquals(RoomReportCategory.WRONG_LOCATION, reports.get(1).reason());
    }

    @Test
    void shouldCreateReport() {
        when(roomReportRepository.save(any())).thenAnswer(invocation -> {
            RoomReport report = invocation.getArgument(0);
            return new RoomReport(9L, report.room(), report.reason(), report.description(), report.resolvedAt());
        });

        Long id = service.create(
                new RoomReport(null, "C303", RoomReportCategory.NOT_EXISTING, "gone", Instant.now())
        );

        assertEquals(9L, id);
        verify(roomReportRepository).save(argThat(report ->
                report.id() == null
                        && report.room().equals("C303")
                        && report.reason() == RoomReportCategory.NOT_EXISTING
                        && report.description().equals("gone")
                        && report.resolvedAt() == null
        ));
    }

    @Test
    void shouldResolveReport() {
        when(roomReportRepository.findById(3L)).thenReturn(Optional.of(
                new RoomReport(3L, "A101", RoomReportCategory.MISSING, "beamer", null)
        ));
        when(roomReportRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Long id = service.resolve(3L, true);

        assertEquals(3L, id);
        verify(roomReportRepository).save(argThat(report ->
                report.id().equals(3L)
                        && report.reason() == RoomReportCategory.MISSING
                        && report.resolvedAt() != null
        ));
    }

    @Test
    void shouldUnresolveReport() {
        when(roomReportRepository.findById(4L)).thenReturn(Optional.of(
                new RoomReport(4L, "A101", RoomReportCategory.OTHER, "misc", Instant.parse("2026-01-01T00:00:00Z"))
        ));
        when(roomReportRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Long id = service.resolve(4L, false);

        assertEquals(4L, id);
        verify(roomReportRepository).save(argThat(report ->
                report.id().equals(4L) && report.resolvedAt() == null
        ));
    }

    @Test
    void shouldThrowWhenResolvingMissingReport() {
        when(roomReportRepository.findById(99L)).thenReturn(Optional.empty());

        RoomReportNotFoundException exception = assertThrows(
                RoomReportNotFoundException.class,
                () -> service.resolve(99L, true)
        );

        assertEquals("Room report not found: 99", exception.getMessage());
    }
}
