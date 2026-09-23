package app.neuland.model.universitysports;

import app.neuland.model.shared.Language;
import java.util.Map;
import java.time.LocalTime;

public record Sports(Long id,
                     Map<Language, SportsContent> contents,
                     Campus campus,
                     String location,
                     Weekday weekday,
                     LocalTime startTime,
                     LocalTime endTime,
                     boolean requiresRegistration,
                     String invitationLink,
                     String email,
                     SportsCategory sportsCategory) {

    public Sports {
        contents = contents == null ? null : Map.copyOf(contents);
    }

}
