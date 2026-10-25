package app.neuland.adapters.inbound.rest.thinews;

import app.neuland.backend.core.api.v0.model.ThiNewsListResponse;
import app.neuland.model.thinews.ThiNewsItem;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

record ThiNewsMapper() {

    static ThiNewsListResponse toListResponse(List<ThiNewsItem> news) {
        ThiNewsListResponse response = new ThiNewsListResponse();
        response.setNews(news.stream()
                .map(ThiNewsMapper::toResponse)
                .toList());
        return response;
    }

    static app.neuland.backend.core.api.v0.model.ThiNewsItem toResponse(ThiNewsItem item) {
        app.neuland.backend.core.api.v0.model.ThiNewsItem api =
                new app.neuland.backend.core.api.v0.model.ThiNewsItem();
        api.setTitle(item.title());
        api.setDescription(item.description());
        api.setDate(toApiDateTime(item.date()));
        api.setImageUrl(toApiUri(item.imageUrl()));
        api.setUrl(toApiUri(item.url()));
        return api;
    }

    private static OffsetDateTime toApiDateTime(Instant dateTime) {
        return dateTime != null ? dateTime.atOffset(ZoneOffset.UTC) : null;
    }

    private static URI toApiUri(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return URI.create(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
