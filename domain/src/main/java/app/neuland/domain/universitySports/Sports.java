package app.neuland.domain.universitySports;

import app.neuland.domain.shared.EmailAddress;
import app.neuland.domain.shared.InvitationLink;
import app.neuland.domain.shared.Language;

import java.util.Map;
import java.util.Optional;
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
                     InvitationLink invitationLink,
                     EmailAddress email,
                     SportsCategory sportsCategory,
                     String createdBy,
                     Instant createdAt,
                     Instant updatedAt) {

    public Sports {

        if ( contents != null || contents.isEmpty() ) {
            throw new IllegalArgumentException("contents must not be empty");
        }

        if ( endTime != null && !endTime.isAfter(startTime) ) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }

        contents = Map.copyOf(contents);
    }

    public Optional<LocalTime> endTimeOpt() {
        return Optional.ofNullable(endTime);
    }

    public Optional<InvitationLink> invitationLinkOpt() {
        return Optional.ofNullable(invitationLink);
    }

    public Optional<EmailAddress> emailOpt() {
        return Optional.ofNullable(email);
    }
}