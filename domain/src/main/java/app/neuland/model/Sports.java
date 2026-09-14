package app.neuland.model;

import java.util.Map;
import java.util.Optional;
import java.time.Instant;
import java.time.LocalTime;

public final class Sports {

    private final Long id;
    private final Map<String, String> title;
    private final Map<String, String> description;
    private final Campus campus;
    private final String location;
    private final Weekday weekday;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final boolean requiresRegistration;
    private final InvitationLink invitationLink;
    private final EmailAddress eMail;
    private final SportsCategory sportsCategory;
    private final String createdBy;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Sports(Long id,
                  Map<String, String> title,
                  Map<String, String> description,
                  Campus campus,
                  String location,
                  Weekday weekday,
                  LocalTime startTime,
                  LocalTime endTime,
                  boolean requiresRegistration,
                  InvitationLink invitationLink,
                  EmailAddress eMail,
                  SportsCategory sportsCategory,
                  String createdBy,
                  Instant createdAt,
                  Instant updatedAt) {

        if ( endTime != null && !endTime.isAfter(startTime) ) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }

        this.id = id;
        this.title = Map.copyOf(title);
        this.description = description == null ? null : Map.copyOf(description);
        this.campus = campus;
        this.location = location;
        this.weekday = weekday;
        this.startTime = startTime;
        this.endTime = endTime;
        this.requiresRegistration = requiresRegistration;
        this.invitationLink = invitationLink;
        this.eMail = eMail;
        this.sportsCategory = sportsCategory;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long id() { return id; }
    public Map<String, String> title() { return title; }
    public Optional<Map<String, String>> description() { return Optional.ofNullable(description); }
    public Campus campus() { return campus; }
    public String location() { return location; }
    public Weekday weekday() { return weekday; }
    public LocalTime startTime() { return startTime; }
    public Optional<LocalTime> endTime() { return Optional.ofNullable(endTime); }
    public boolean requiresRegistration() { return requiresRegistration; }
    public Optional<InvitationLink> invitationLink() { return Optional.ofNullable(invitationLink); }
    public Optional<EmailAddress> eMail() { return Optional.ofNullable(eMail); }
    public SportsCategory sportsCategory() { return sportsCategory; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

}