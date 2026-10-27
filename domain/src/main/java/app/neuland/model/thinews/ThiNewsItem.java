package app.neuland.model.thinews;

import java.time.Instant;

public record ThiNewsItem(
        String title,
        String description,
        Instant date,
        String imageUrl,
        String url
) {}
