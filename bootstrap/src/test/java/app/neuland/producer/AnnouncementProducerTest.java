package app.neuland.producer;

import app.neuland.implementation.announcements.AnnouncementService;
import app.neuland.ports.inbound.AnnouncementUseCase;
import app.neuland.ports.outbound.AnnouncementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class AnnouncementProducerTest {

    @Mock
    AnnouncementRepository announcementRepository;

    @Test
    void shouldProduceAnnouncementService() {
        AnnouncementUseCase useCase = new AnnouncementProducer().announcementUseCase(announcementRepository);

        assertInstanceOf(AnnouncementService.class, useCase);
    }
}
