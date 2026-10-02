package app.neuland.producer;

import app.neuland.implementation.roomreports.RoomReportService;
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

    @Test
    void shouldProduceRoomReportService() {
        RoomReportUseCase useCase = new RoomReportProducer().roomReportUseCase(roomReportRepository);

        assertInstanceOf(RoomReportService.class, useCase);
    }
}
