package app.neuland.model.universitysports;

import app.neuland.model.shared.Language;

import java.time.LocalTime;
import java.util.Map;

public final class SportsFixtures {

    private SportsFixtures() {
    }

    public static Sports sample(Long id) {
        return sample(id, Weekday.MONDAY, SportsCategory.CALISTHENICS);
    }

    public static Sports sample(Long id, Weekday weekday, SportsCategory category) {
        return new Sports(
                id,
                Map.of(
                        Language.DE, new SportsContent("Titel", "Beschreibung"),
                        Language.EN, new SportsContent("Title", "Description")
                ),
                Campus.INGOLSTADT,
                "Sporthalle",
                weekday,
                LocalTime.of(17, 0),
                LocalTime.of(18, 30),
                true,
                "https://invite.example",
                "sports@example.com",
                category
        );
    }
}
