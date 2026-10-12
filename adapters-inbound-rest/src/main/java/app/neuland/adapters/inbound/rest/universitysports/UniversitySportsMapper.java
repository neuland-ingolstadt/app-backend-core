package app.neuland.adapters.inbound.rest.universitysports;

import app.neuland.backend.core.api.v0.model.CampusEnum;
import app.neuland.backend.core.api.v0.model.SportsCategoryEnum;
import app.neuland.backend.core.api.v0.model.SportsContents;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        SportsListResponse response = new SportsListResponse();
        response.setSports(sports.stream()
                .map(UniversitySportsMapper::toResponse)
                .toList());
        return response;
    }

    static app.neuland.backend.core.api.v0.model.Sports toResponse(Sports sports) {
        app.neuland.backend.core.api.v0.model.Sports api =
                new app.neuland.backend.core.api.v0.model.Sports();
        api.setId(Objects.requireNonNull(sports.id(), "Sports id is required"));
        api.setContents(toApiContents(sports.contents()));
        api.setCampus(Objects.requireNonNull(
                toApiCampus(sports.campus()),
                "Sports campus is required"
        ));
        api.setLocation(Objects.requireNonNull(
                sports.location(),
                "Sports location is required"
        ));
        api.setWeekday(Objects.requireNonNull(
                toApiWeekday(sports.weekday()),
                "Sports weekday is required"
        ));
        api.setStartTime(Objects.requireNonNull(
                toApiTime(sports.startTime()),
                "Sports startTime is required"
        ));
        api.setEndTime(toApiTime(sports.endTime()));
        api.setRequiresRegistration(sports.requiresRegistration());
        api.setInvitationLink(toApiUri(sports.invitationLink()));
        api.setEmail(sports.email());
        api.setSportsCategory(Objects.requireNonNull(
                toApiCategory(sports.sportsCategory()),
                "Sports sportsCategory is required"
        ));
        return api;
    }

    private static Sports toDomain(
            Long id,
            SportsContents contents,
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
                toDomainContents(contents),
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

    private static Map<Language, SportsContent> toDomainContents(SportsContents contents) {
        if (contents == null) {
            return Map.of();
        }

        Map<Language, SportsContent> mapped = new EnumMap<>(Language.class);
        if (contents.getDe() != null) {
            mapped.put(Language.DE, toDomainContent(contents.getDe()));
        }
        if (contents.getEn() != null) {
            mapped.put(Language.EN, toDomainContent(contents.getEn()));
        }
        return Map.copyOf(mapped);
    }

    private static SportsContent toDomainContent(
            app.neuland.backend.core.api.v0.model.SportsContent content
    ) {
        return new SportsContent(
                content.getTitle(),
                content.getDescription()
        );
    }

    private static SportsContents toApiContents(Map<Language, SportsContent> contents) {
        SportsContent de = Objects.requireNonNull(
                contents.get(Language.DE),
                "Sports is missing German content"
        );
        SportsContent en = Objects.requireNonNull(
                contents.get(Language.EN),
                "Sports is missing English content"
        );
        SportsContents api = new SportsContents();
        api.setDe(toApiContent(de));
        api.setEn(toApiContent(en));
        return api;
    }

    private static app.neuland.backend.core.api.v0.model.SportsContent toApiContent(
            SportsContent content
    ) {
        app.neuland.backend.core.api.v0.model.SportsContent api =
                new app.neuland.backend.core.api.v0.model.SportsContent();
        api.setTitle(content.title());
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
