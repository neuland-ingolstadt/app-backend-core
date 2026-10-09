package app.neuland.adapters.inbound.rest.announcements;

import app.neuland.adapters.inbound.security.SecurityRoles;
import app.neuland.backend.core.api.v0.AnnouncementsApi;
import app.neuland.backend.core.api.v0.model.AnnouncementCreateRequest;
import app.neuland.backend.core.api.v0.model.AnnouncementPatchRequest;
import app.neuland.model.announcement.Announcement;
import app.neuland.ports.inbound.AnnouncementUseCase;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

public class AnnouncementResource implements AnnouncementsApi {

    private final AnnouncementUseCase announcementUseCase;

    @Context
    UriInfo uriInfo;

    @Inject
    public AnnouncementResource(AnnouncementUseCase announcementUseCase) {
        this.announcementUseCase = announcementUseCase;
    }

    @Override
    @PermitAll
    public Response listAnnouncements(Boolean includeInactive) {
        return Response
                .ok(AnnouncementMapper.toListResponse(
                        announcementUseCase.list(Boolean.TRUE.equals(includeInactive))
                ))
                .build();
    }

    @Override
    @PermitAll
    public Response getAnnouncement(Long announcementId) {
        return Response
                .ok(AnnouncementMapper.toResponse(
                        announcementUseCase.get(announcementId)
                ))
                .build();
    }

    @Override
    @RolesAllowed({
            SecurityRoles.announcementRole,
            SecurityRoles.adminRole
    })
    public Response createAnnouncement(AnnouncementCreateRequest announcementCreateRequest) {
        Announcement created = announcementUseCase.create(
                AnnouncementMapper.toDomain(announcementCreateRequest)
        );

        return Response
                .created(
                        uriInfo.getAbsolutePathBuilder()
                                .path(String.valueOf(created.id()))
                                .build()
                )
                .entity(AnnouncementMapper.toResponse(created))
                .build();
    }

    @Override
    @RolesAllowed({
            SecurityRoles.announcementRole,
            SecurityRoles.adminRole
    })
    public Response updateAnnouncement(
            Long announcementId,
            AnnouncementPatchRequest announcementPatchRequest
    ) {
        return Response
                .ok(AnnouncementMapper.toResponse(
                        announcementUseCase.update(
                                announcementId,
                                AnnouncementMapper.toDomain(
                                        announcementId,
                                        announcementPatchRequest
                                )
                        )
                ))
                .build();
    }

    @Override
    @RolesAllowed({
            SecurityRoles.announcementRole,
            SecurityRoles.adminRole
    })
    public Response deleteAnnouncement(Long announcementId) {
        announcementUseCase.delete(announcementId);

        return Response
                .noContent()
                .build();
    }
}
