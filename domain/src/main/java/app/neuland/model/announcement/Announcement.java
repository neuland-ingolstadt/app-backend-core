package app.neuland.model.announcement;

import java.util.Set;
import java.util.Map;
import java.time.Instant;
import app.neuland.model.user.*;

public record Announcement (
    Long id,
    Set<Platform> platforms,
    Set<UserKind> userKinds,
    Map<Language, AnnouncementContent> contents,
    Instant startDateTime,
    Instant endDateTime,
    Integer priority,
    String url,
    String imageUrl,
    User createdBy,
    Instant createdAt,
    Instant updatedAt
) {}
