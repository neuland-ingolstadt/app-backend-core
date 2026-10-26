package app.neuland.producer;

import app.neuland.implementation.thinews.ThiNewsService;
import app.neuland.ports.inbound.ThiNewsUseCase;
import app.neuland.ports.outbound.ThiNewsClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class ThiNewsProducerTest {

    @Mock
    ThiNewsClient thiNewsClient;

    @Test
    void shouldProduceThiNewsService() {
        ThiNewsUseCase useCase = new ThiNewsProducer().thiNewsUseCase(thiNewsClient);

        assertInstanceOf(ThiNewsService.class, useCase);
    }
}
