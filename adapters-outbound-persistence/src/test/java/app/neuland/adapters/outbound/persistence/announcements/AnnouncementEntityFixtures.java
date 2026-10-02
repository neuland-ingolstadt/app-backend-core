package app.neuland.adapters.outbound.persistence.announcements;

import app.neuland.model.announcement.Platform;
import app.neuland.model.announcement.UserKind;
import app.neuland.model.shared.Language;

import java.time.Instant;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;

final class AnnouncementEntityFixtures {

    private AnnouncementEntityFixtures() {
    }

    static AnnouncementEntity entity(Long id) {
        AnnouncementEntity entity = new AnnouncementEntity();
        entity.id = id;
        entity.platforms = new HashSet<>(EnumSet.of(Platform.ANDROID, Platform.IOS));
        entity.userKinds = new HashSet<>(EnumSet.of(UserKind.STUDENT, UserKind.GUEST));
        AnnouncementContentEmbeddable content = new AnnouncementContentEmbeddable();
        content.title = "Titel";
        content.description = "Beschreibung";
        entity.contents = new HashMap<>();
        entity.contents.put(Language.DE, content);
        entity.startDateTime = Instant.parse("2026-01-01T00:00:00Z");
        entity.endDateTime = Instant.parse("2026-12-31T23:59:59Z");
        entity.priority = 1;
        return entity;
    }
}
