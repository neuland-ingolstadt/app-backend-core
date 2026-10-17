package app.neuland.adapters.inbound.rest.announcements;

import app.neuland.adapters.inbound.security.SecurityRoles;
import app.neuland.backend.core.api.v0.AnnouncementsApi;
import app.neuland.backend.core.api.v0.model.Announcement;
import app.neuland.backend.core.api.v0.model.AnnouncementCreateRequest;
import app.neuland.backend.core.api.v0.model.AnnouncementListResponse;
import app.neuland.backend.core.api.v0.model.AnnouncementPatchRequest;
import app.neuland.ports.inbound.AnnouncementUseCase;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.RestResponse;

public class AnnouncementResource implements AnnouncementsApi {

    private final AnnouncementUseCase announcementUseCase;
    private final SecurityIdentity securityIdentity;

    @Context
    UriInfo uriInfo;

    @Inject
    public AnnouncementResource(
            AnnouncementUseCase announcementUseCase,
            SecurityIdentity securityIdentity
    ) {
        this.announcementUseCase = announcementUseCase;
        this.securityIdentity = securityIdentity;
    }

    @Override
    @PermitAll
    public RestResponse<AnnouncementListResponse> listAnnouncements(Boolean includeInactive) {
        boolean inactive = Boolean.TRUE.equals(includeInactive);
        if (inactive) {
            requireInactiveAccess();
        }
        return RestResponse.ok(
                AnnouncementMapper.toListResponse(announcementUseCase.list(inactive))
        );
    }

    private void requireInactiveAccess() {
        if (securityIdentity.isAnonymous()) {
            throw new NotAuthorizedException("Bearer");
        }
        if (!(securityIdentity.hasRole(SecurityRoles.announcementRole)
                || securityIdentity.hasRole(SecurityRoles.adminRole))) {
            throw new ForbiddenException();
        }
    }

    @Override
    @PermitAll
    public RestResponse<Announcement> getAnnouncement(Long announcementId) {
        return RestResponse.ok(
                AnnouncementMapper.toResponse(announcementUseCase.get(announcementId))
        );
    }

    @Override
    @RolesAllowed({
            SecurityRoles.announcementRole,
            SecurityRoles.adminRole
    })
    public RestResponse<Announcement> createAnnouncement(
            AnnouncementCreateRequest announcementCreateRequest
    ) {
        app.neuland.model.announcement.Announcement created = announcementUseCase.create(
                AnnouncementMapper.toDomain(announcementCreateRequest)
        );

        return RestResponse.ResponseBuilder
                .create(
                        Response.Status.CREATED,
                        AnnouncementMapper.toResponse(created)
                )
                .location(uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(created.id()))
                        .build())
                .build();
    }

    @Override
    @RolesAllowed({
            SecurityRoles.announcementRole,
            SecurityRoles.adminRole
    })
    public RestResponse<Announcement> updateAnnouncement(
            Long announcementId,
            AnnouncementPatchRequest announcementPatchRequest
    ) {
        return RestResponse.ok(
                AnnouncementMapper.toResponse(
                        announcementUseCase.update(
                                announcementId,
                                AnnouncementMapper.toDomain(
                                        announcementId,
                                        announcementPatchRequest
                                )
                        )
                )
        );
    }

    @Override
    @RolesAllowed({
            SecurityRoles.announcementRole,
            SecurityRoles.adminRole
    })
    public RestResponse<Void> deleteAnnouncement(Long announcementId) {
        announcementUseCase.delete(announcementId);
        return RestResponse.noContent();
    }
}
