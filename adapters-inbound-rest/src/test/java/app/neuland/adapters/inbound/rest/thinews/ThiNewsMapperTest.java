package app.neuland.adapters.inbound.rest.thinews;

import app.neuland.backend.core.api.v0.model.ThiNewsListResponse;
import app.neuland.model.thinews.ThiNewsItem;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ThiNewsMapperTest {

    @Test
    void shouldNormalizeDateToUtcAndMapFields() {
        new ThiNewsMapper();

        ThiNewsListResponse response = ThiNewsMapper.toListResponse(List.of(
                new ThiNewsItem(
                        "Title",
                        "Description",
                        Instant.parse("2026-10-14T12:10:17Z"),
                        "https://www.thi.de/image.webp",
                        "https://www.thi.de/news/title/"
                )
        ));

        assertEquals(1, response.getNews().size());
        var item = response.getNews().getFirst();
        assertEquals("Title", item.getTitle());
        assertEquals("Description", item.getDescription());
        assertEquals(OffsetDateTime.parse("2026-10-14T12:10:17Z"), item.getDate());
        assertEquals(ZoneOffset.UTC, item.getDate().getOffset());
        assertEquals("https://www.thi.de/image.webp", item.getImageUrl().toString());
        assertEquals("https://www.thi.de/news/title/", item.getUrl().toString());
    }

    @Test
    void shouldMapNullAndBlankUrisAndNullDate() {
        var item = ThiNewsMapper.toResponse(new ThiNewsItem(
                "Title",
                "Description",
                null,
                "   ",
                null
        ));

        assertNull(item.getDate());
        assertNull(item.getImageUrl());
        assertNull(item.getUrl());
    }

    @Test
    void shouldMapInvalidUriToNull() {
        var item = ThiNewsMapper.toResponse(new ThiNewsItem(
                "Title",
                "Description",
                Instant.parse("2026-10-01T10:00:00Z"),
                "http://[invalid",
                "not a uri"
        ));

        assertNotNull(item.getDate());
        assertNull(item.getImageUrl());
        assertNull(item.getUrl());
    }
}
