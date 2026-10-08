package app.neuland.adapters.inbound.rest.roomreports;

import app.neuland.adapters.inbound.security.SecurityRoles;
import app.neuland.backend.core.api.v0.RoomReportsApi;
import app.neuland.backend.core.api.v0.model.CreateRoomReportRequest;
import app.neuland.backend.core.api.v0.model.RoomReportPatchRequest;
import app.neuland.ports.inbound.RoomReportUseCase;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

public class RoomReportResource implements RoomReportsApi {

    private final RoomReportUseCase roomReportUseCase;

    @Context
    UriInfo uriInfo;

    @Inject
    public RoomReportResource(RoomReportUseCase roomReportUseCase) {
        this.roomReportUseCase = roomReportUseCase;
    }

    @Override
    @RolesAllowed({
            SecurityRoles.reportsRole,
            SecurityRoles.adminRole
    })
    public Response listRoomReports() {
        return Response
                .ok(RoomReportMapper.toListResponse(roomReportUseCase.list()))
                .build();
    }

    @Override
    @PermitAll
    public Response createRoomReport(CreateRoomReportRequest createRoomReportRequest) {
        Long id = roomReportUseCase.create(RoomReportMapper.toDomain(createRoomReportRequest));

        return Response
                .created(
                        uriInfo.getAbsolutePathBuilder()
                                .path(String.valueOf(id))
                                .build()
                )
                .entity(RoomReportMapper.toIdResponse(id))
                .build();
    }

    @Override
    @RolesAllowed({
            SecurityRoles.reportsRole,
            SecurityRoles.adminRole
    })
    public Response resolveRoomReport(
            Long roomReportId,
            RoomReportPatchRequest roomReportPatchRequest
    ) {
        Long id = roomReportUseCase.resolve(
                roomReportId,
                roomReportPatchRequest.getResolved()
        );

        return Response
                .ok(RoomReportMapper.toIdResponse(id))
                .build();
    }
}
