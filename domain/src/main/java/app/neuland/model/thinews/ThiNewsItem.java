package app.neuland.model.thinews;

import java.time.OffsetDateTime;

public record ThiNewsItem(
        String title,
        String description,
        OffsetDateTime date,
        String imageUrl,
        String url
) {}
