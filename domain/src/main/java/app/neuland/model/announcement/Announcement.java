package app.neuland.model.announcement;

import java.util.Set;
import java.util.Map;
import java.time.Instant;

public record Announcement (
    Long id,
    Set<Platform> platforms,
    Set<UserKind> userKinds,
    Map<Language, AnnouncementContent> contents,
    Instant startDateTime,
    Instant endDateTime,
    Integer priority,
    String url,
    String imageUrl
) {}
