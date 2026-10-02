package app.neuland.model.announcement;

import app.neuland.model.shared.Language;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Map;

public final class AnnouncementFixtures {

    private AnnouncementFixtures() {
    }

    public static Announcement sample(Long id) {
        return new Announcement(
                id,
                EnumSet.of(Platform.ANDROID, Platform.IOS),
                EnumSet.of(UserKind.STUDENT, UserKind.GUEST),
                Map.of(Language.DE, new AnnouncementContent("Titel", "Beschreibung")),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-12-31T23:59:59Z"),
                1,
                null,
                null
        );
    }
}
