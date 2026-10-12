package app.neuland.adapters.inbound.rest.announcements;

import app.neuland.backend.core.api.v0.model.AnnouncementContents;
import app.neuland.backend.core.api.v0.model.AnnouncementCreateRequest;
import app.neuland.backend.core.api.v0.model.AnnouncementListResponse;
import app.neuland.backend.core.api.v0.model.AnnouncementPatchRequest;
import app.neuland.backend.core.api.v0.model.PlatformEnum;
import app.neuland.backend.core.api.v0.model.UserKindEnum;
import app.neuland.model.announcement.Announcement;
import app.neuland.model.announcement.AnnouncementContent;
import app.neuland.model.announcement.Platform;
import app.neuland.model.announcement.UserKind;
import app.neuland.model.shared.Language;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

record AnnouncementMapper() {

    static Announcement toDomain(AnnouncementCreateRequest request) {
        return toDomain(
                null,
                request.getPlatforms(),
                request.getUserKinds(),
                request.getContents(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getPriority(),
                request.getUrl(),
                request.getImageUrl()
        );
    }

    static Announcement toDomain(Long id, AnnouncementPatchRequest request) {
        return toDomain(
                id,
                request.getPlatforms(),
                request.getUserKinds(),
                request.getContents(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getPriority(),
                request.getUrl(),
                request.getImageUrl()
        );
    }

    static AnnouncementListResponse toListResponse(List<Announcement> announcements) {
        AnnouncementListResponse response = new AnnouncementListResponse();
        response.setAnnouncements(announcements.stream()
                .map(AnnouncementMapper::toResponse)
                .toList());
        return response;
    }

    static app.neuland.backend.core.api.v0.model.Announcement toResponse(Announcement announcement) {
        app.neuland.backend.core.api.v0.model.Announcement api =
                new app.neuland.backend.core.api.v0.model.Announcement();
        api.setPlatforms(toApiPlatformSet(announcement.platforms()));
        api.setUserKinds(toApiUserKindSet(announcement.userKinds()));
        api.setContents(toApiContents(announcement.contents()));
        api.setStartDateTime(toApiDateTime(announcement.startDateTime()));
        api.setEndDateTime(toApiDateTime(announcement.endDateTime()));
        api.setPriority(announcement.priority());
        api.setId(announcement.id());
        api.setUrl(toApiUri(announcement.url()));
        api.setImageUrl(toApiUri(announcement.imageUrl()));
        return api;
    }

    private static Announcement toDomain(
            Long id,
            Set<PlatformEnum> platforms,
            Set<UserKindEnum> userKinds,
            AnnouncementContents contents,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            Integer priority,
            URI url,
            URI imageUrl
    ) {
        return new Announcement(
                id,
                toDomainPlatformSet(platforms),
                toDomainUserKindSet(userKinds),
                toDomainContents(contents),
                toDomainDateTime(startDateTime),
                toDomainDateTime(endDateTime),
                priority,
                toDomainUrl(url),
                toDomainUrl(imageUrl)
        );
    }

    private static Set<Platform> toDomainPlatformSet(Set<PlatformEnum> platforms) {
        if (platforms == null) {
            return Set.of();
        }

        return platforms.stream()
                .map(PlatformEnum::name)
                .map(Platform::valueOf)
                .collect(Collectors.toSet());
    }

    private static Set<UserKind> toDomainUserKindSet(Set<UserKindEnum> userKinds) {
        if (userKinds == null) {
            return Set.of();
        }

        return userKinds.stream()
                .map(UserKindEnum::name)
                .map(UserKind::valueOf)
                .collect(Collectors.toSet());
    }

    private static Map<Language, AnnouncementContent> toDomainContents(
            AnnouncementContents contents
    ) {
        if (contents == null) {
            return Map.of();
        }

        Map<Language, AnnouncementContent> mapped = new EnumMap<>(Language.class);
        if (contents.getDe() != null) {
            mapped.put(Language.DE, toDomainContent(contents.getDe()));
        }
        if (contents.getEn() != null) {
            mapped.put(Language.EN, toDomainContent(contents.getEn()));
        }
        return Map.copyOf(mapped);
    }

    private static AnnouncementContent toDomainContent(
            app.neuland.backend.core.api.v0.model.AnnouncementContent content
    ) {
        return new AnnouncementContent(
                content.getTitle(),
                content.getDescription()
        );
    }

    private static Set<PlatformEnum> toApiPlatformSet(Set<Platform> platforms) {
        return platforms.stream()
                .map(Platform::name)
                .map(PlatformEnum::fromValue)
                .collect(Collectors.toSet());
    }

    private static Set<UserKindEnum> toApiUserKindSet(Set<UserKind> userKinds) {
        return userKinds.stream()
                .map(UserKind::name)
                .map(UserKindEnum::fromValue)
                .collect(Collectors.toSet());
    }

    private static AnnouncementContents toApiContents(
            Map<Language, AnnouncementContent> contents
    ) {
        AnnouncementContent de = Objects.requireNonNull(
                contents.get(Language.DE),
                "Announcement is missing German content"
        );
        AnnouncementContent en = Objects.requireNonNull(
                contents.get(Language.EN),
                "Announcement is missing English content"
        );
        AnnouncementContents api = new AnnouncementContents();
        api.setDe(toApiContent(de));
        api.setEn(toApiContent(en));
        return api;
    }

    private static app.neuland.backend.core.api.v0.model.AnnouncementContent toApiContent(
            AnnouncementContent content
    ) {
        app.neuland.backend.core.api.v0.model.AnnouncementContent api =
                new app.neuland.backend.core.api.v0.model.AnnouncementContent();
        api.setTitle(content.title());
        api.setDescription(content.description());
        return api;
    }

    private static Instant toDomainDateTime(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toInstant() : null;
    }

    private static OffsetDateTime toApiDateTime(Instant dateTime) {
        return dateTime != null ? dateTime.atOffset(ZoneOffset.UTC) : null;
    }

    private static String toDomainUrl(URI url) {
        return url != null ? url.toString() : null;
    }

    private static URI toApiUri(String url) {
        return url != null ? URI.create(url) : null;
    }
}
