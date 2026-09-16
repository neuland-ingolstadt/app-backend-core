package app.neuland.domain.universitysports;

import app.neuland.domain.shared.Language;

import java.util.Map;
import java.time.Instant;
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
                     SportsCategory sportsCategory,
                     String createdBy,
                     Instant createdAt,
                     Instant updatedAt) {

    public Sports {
        contents = contents == null ? null : Map.copyOf(contents);
    }

}