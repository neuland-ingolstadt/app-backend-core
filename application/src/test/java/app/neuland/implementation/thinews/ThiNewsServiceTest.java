package app.neuland.implementation.thinews;

import app.neuland.model.thinews.ThiNewsItem;
import app.neuland.ports.outbound.ThiNewsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThiNewsServiceTest {

    @Mock
    ThiNewsClient thiNewsClient;

    private ThiNewsService service;

    @BeforeEach
    void setUp() {
        service = new ThiNewsService(thiNewsClient);
    }

    @Test
    void shouldDelegateToClient() {
        List<ThiNewsItem> news = List.of(
                new ThiNewsItem(
                        "Title",
                        "Description",
                        Instant.parse("2026-10-14T12:10:17Z"),
                        "https://www.thi.de/image.webp",
                        "https://www.thi.de/news/title/"
                )
        );
        when(thiNewsClient.fetchAll()).thenReturn(news);

        assertEquals(news, service.list());
        verify(thiNewsClient).fetchAll();
    }
}
