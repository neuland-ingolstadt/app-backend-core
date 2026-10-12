package app.neuland.adapters.inbound.rest.universitysports;

import app.neuland.backend.core.api.v0.model.CampusEnum;
import app.neuland.backend.core.api.v0.model.SportsCategoryEnum;
import app.neuland.backend.core.api.v0.model.SportsCreateRequest;
import app.neuland.backend.core.api.v0.model.SportsListResponse;
import app.neuland.backend.core.api.v0.model.SportsPatchRequest;
import app.neuland.backend.core.api.v0.model.WeekdayEnum;
import app.neuland.model.shared.Language;
import app.neuland.model.universitysports.Campus;
import app.neuland.model.universitysports.Sports;
import app.neuland.model.universitysports.SportsCategory;
import app.neuland.model.universitysports.SportsContent;
import app.neuland.model.universitysports.Weekday;

import java.net.URI;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

record UniversitySportsMapper() {

    static Sports toDomain(SportsCreateRequest request) {
        return toDomain(
                null,
                request.getContents(),
                request.getCampus(),
                request.getLocation(),
                request.getWeekday(),
                request.getStartTime(),
                request.getEndTime(),
                request.getRequiresRegistration(),
                request.getInvitationLink(),
                request.getEmail(),
                request.getSportsCategory()
        );
    }

    static Sports toDomain(Long id, SportsPatchRequest request) {
        return toDomain(
                id,
                request.getContents(),
                request.getCampus(),
                request.getLocation(),
                request.getWeekday(),
                request.getStartTime(),
                request.getEndTime(),
                request.getRequiresRegistration(),
                request.getInvitationLink(),
                request.getEmail(),
                request.getSportsCategory()
        );
    }

    static SportsListResponse toListResponse(List<Sports> sports) {
        return new SportsListResponse(sports.stream()
                        .map(UniversitySportsMapper::toResponse)
                        .toList()
        );
    }

    static app.neuland.backend.core.api.v0.model.Sports toResponse(Sports sports) {
        app.neuland.backend.core.api.v0.model.Sports api =
                new app.neuland.backend.core.api.v0.model.Sports(sports.id());
        api.setContents(toApiContentsMap(sports.contents()));
        api.setCampus(toApiCampus(sports.campus()));
        api.setLocation(sports.location());
        api.setWeekday(toApiWeekday(sports.weekday()));
        api.setStartTime(toApiTime(sports.startTime()));
        api.setEndTime(toApiTime(sports.endTime()));
        api.setRequiresRegistration(sports.requiresRegistration());
        api.setInvitationLink(toApiUri(sports.invitationLink()));
        api.setEmail(sports.email());
        api.setSportsCategory(toApiCategory(sports.sportsCategory()));
        return api;
    }

    private static Sports toDomain(
            Long id,
            Map<String, app.neuland.backend.core.api.v0.model.SportsContent> contents,
            CampusEnum campus,
            String location,
            WeekdayEnum weekday,
            String startTime,
            String endTime,
            Boolean requiresRegistration,
            URI invitationLink,
            String email,
            SportsCategoryEnum sportsCategory
    ) {
        return new Sports(
                id,
                toDomainContentsMap(contents),
                toDomainCampus(campus),
                location,
                toDomainWeekday(weekday),
                toDomainTime(startTime),
                toDomainTime(endTime),
                Boolean.TRUE.equals(requiresRegistration),
                toDomainUrl(invitationLink),
                email,
                toDomainCategory(sportsCategory)
        );
    }

    private static Map<Language, SportsContent> toDomainContentsMap(
            Map<String, app.neuland.backend.core.api.v0.model.SportsContent> contents
    ) {
        if (contents == null) {
            return Map.of();
        }

        return contents.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> Language.valueOf(entry.getKey()),
                        entry -> toDomainContent(entry.getValue())
                ));
    }

    private static SportsContent toDomainContent(
            app.neuland.backend.core.api.v0.model.SportsContent content
    ) {
        return new SportsContent(
                content.getTitle(),
                content.getDescription()
        );
    }

    private static Map<String, app.neuland.backend.core.api.v0.model.SportsContent> toApiContentsMap(
            Map<Language, SportsContent> contents
    ) {
        return contents.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry -> toApiContent(entry.getValue())
                ));
    }

    private static app.neuland.backend.core.api.v0.model.SportsContent toApiContent(
            SportsContent content
    ) {
        app.neuland.backend.core.api.v0.model.SportsContent api =
                new app.neuland.backend.core.api.v0.model.SportsContent(content.title());
        api.setDescription(content.description());
        return api;
    }

    private static Campus toDomainCampus(CampusEnum campus) {
        return campus != null ? Campus.valueOf(campus.name()) : null;
    }

    private static CampusEnum toApiCampus(Campus campus) {
        return campus != null ? CampusEnum.fromValue(campus.name()) : null;
    }

    private static Weekday toDomainWeekday(WeekdayEnum weekday) {
        return weekday != null ? Weekday.valueOf(weekday.name()) : null;
    }

    private static WeekdayEnum toApiWeekday(Weekday weekday) {
        return weekday != null ? WeekdayEnum.fromValue(weekday.name()) : null;
    }

    private static SportsCategory toDomainCategory(SportsCategoryEnum category) {
        return category != null ? SportsCategory.valueOf(category.name()) : null;
    }

    private static SportsCategoryEnum toApiCategory(SportsCategory category) {
        return category != null ? SportsCategoryEnum.fromValue(category.name()) : null;
    }

    private static LocalTime toDomainTime(String time) {
        if (time == null) {
            return null;
        }

        try {
            return OffsetTime.parse(time).toLocalTime();
        } catch (DateTimeParseException ignored) {
            return LocalTime.parse(time);
        }
    }

    private static String toApiTime(LocalTime time) {
        return time != null
                ? DateTimeFormatter.ISO_OFFSET_TIME.format(OffsetTime.of(time, ZoneOffset.UTC))
                : null;
    }

    private static String toDomainUrl(URI url) {
        return url != null ? url.toString() : null;
    }

    private static URI toApiUri(String url) {
        return url != null ? URI.create(url) : null;
    }
}

