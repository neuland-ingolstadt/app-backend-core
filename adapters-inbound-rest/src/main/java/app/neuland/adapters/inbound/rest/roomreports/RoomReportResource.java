package app.neuland.adapters.inbound.rest.roomreports;

import app.neuland.adapters.inbound.security.SecurityRoles;
import app.neuland.backend.core.api.v0.RoomReportsApi;
import app.neuland.backend.core.api.v0.model.CreateRoomReportRequest;
import app.neuland.backend.core.api.v0.model.RoomReportListResponse;
import app.neuland.backend.core.api.v0.model.RoomReportPatchRequest;
import app.neuland.backend.core.api.v0.model.RoomReportResponse;
import app.neuland.ports.inbound.RoomReportUseCase;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.RestResponse;
import io.smallrye.faulttolerance.api.RateLimit;
import java.time.temporal.ChronoUnit;

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
    public RestResponse<RoomReportListResponse> listRoomReports() {
        return RestResponse.ok(
                RoomReportMapper.toListResponse(roomReportUseCase.list())
        );
    }

    @Override
    @PermitAll
    @RateLimit(value = 100, window = 1, windowUnit = ChronoUnit.MINUTES)
    public RestResponse<RoomReportResponse> createRoomReport(
            CreateRoomReportRequest createRoomReportRequest
    ) {
        Long id = roomReportUseCase.create(RoomReportMapper.toDomain(createRoomReportRequest));

        return RestResponse.ResponseBuilder
                .create(
                        Response.Status.CREATED,
                        RoomReportMapper.toIdResponse(id)
                )
                .location(uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(id))
                        .build())
                .build();
    }

    @Override
    @RolesAllowed({
            SecurityRoles.reportsRole,
            SecurityRoles.adminRole
    })
    @RateLimit(value = 100, window = 1, windowUnit = ChronoUnit.MINUTES)
    public RestResponse<RoomReportResponse> resolveRoomReport(
            Long roomReportId,
            RoomReportPatchRequest roomReportPatchRequest
    ) {
        Long id = roomReportUseCase.resolve(
                roomReportId,
                roomReportPatchRequest.getResolved()
        );

        return RestResponse.ok(RoomReportMapper.toIdResponse(id));
    }
}
