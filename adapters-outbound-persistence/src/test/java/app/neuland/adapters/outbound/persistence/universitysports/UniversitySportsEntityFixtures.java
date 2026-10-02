package app.neuland.adapters.outbound.persistence.universitysports;

import app.neuland.model.shared.Language;
import app.neuland.model.universitysports.Campus;
import app.neuland.model.universitysports.SportsCategory;
import app.neuland.model.universitysports.Weekday;

import java.time.LocalTime;
import java.util.HashMap;

final class UniversitySportsEntityFixtures {

    private UniversitySportsEntityFixtures() {
    }

    static UniversitySportsEntity entity(Long id) {
        UniversitySportsEntity entity = new UniversitySportsEntity();
        entity.id = id;
        UniversitySportsContentEmbeddable de = new UniversitySportsContentEmbeddable();
        de.title = "Titel";
        de.description = "Beschreibung";
        UniversitySportsContentEmbeddable en = new UniversitySportsContentEmbeddable();
        en.title = "Title";
        en.description = "Description";
        entity.contents = new HashMap<>();
        entity.contents.put(Language.DE, de);
        entity.contents.put(Language.EN, en);
        entity.campus = Campus.INGOLSTADT;
        entity.location = "Sporthalle";
        entity.weekday = Weekday.MONDAY;
        entity.startTime = LocalTime.of(17, 0);
        entity.endTime = LocalTime.of(18, 30);
        entity.requiresRegistration = true;
        entity.invitationLink = "https://invite.example";
        entity.email = "sports@example.com";
        entity.sportsCategory = SportsCategory.CALISTHENICS;
        return entity;
    }
}
