package app.neuland.producer;

import app.neuland.implementation.roomreports.RoomReportService;
import app.neuland.ports.inbound.AuditLogUseCase;
import app.neuland.ports.inbound.RoomReportUseCase;
import app.neuland.ports.outbound.RoomReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class RoomReportProducerTest {

    @Mock
    RoomReportRepository roomReportRepository;

    @Mock
    AuditLogUseCase auditLogUseCase;

    @Test
    void shouldProduceRoomReportService() {
        RoomReportUseCase useCase = new RoomReportProducer()
                .roomReportUseCase(roomReportRepository, auditLogUseCase);

        assertInstanceOf(RoomReportService.class, useCase);
    }
}
