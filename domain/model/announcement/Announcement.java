package domain.model.announcement;

import java.util.Set;
import java.util.Map;
import java.time.Instant;

public record Announcement {
    Long id,
    Set<Platform> platforms,
    Set<UserKind> userKinds,
    Map<LanguageEnum, AnnouncentContent> contents,
    Instant startDateTime,
    Instant endDateTime,
    Integer priority,
    String url,
    String imageUrl,
    User createdBy,
    Instant createdAt,
    Instant updatedAt
) {}
