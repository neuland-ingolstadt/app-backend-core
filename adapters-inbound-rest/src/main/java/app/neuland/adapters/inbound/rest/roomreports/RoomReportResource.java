package app.neuland.adapters.inbound.rest.roomreports;

import app.neuland.ports.inbound.RoomReportUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/room-reports")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoomReportResource {

    private final RoomReportUseCase roomReportUseCase;

    @Inject
    public RoomReportResource(RoomReportUseCase roomReportUseCase) {
        this.roomReportUseCase = roomReportUseCase;
    }

    @GET
    public RoomReportResponse list() {
        return new RoomReportResponse(
                roomReportUseCase.list()
                .stream()
                .map(RoomReportResponse::from)
                .toList()
        );
    }

    @POST
    public Response create(
            @Valid CreateRoomReportRequest request,
            @Context UriInfo uriInfo
    ) {
        Long id = roomReportUseCase.create(request.toDomain());

        return Response
                .created(
                        uriInfo.getAbsolutePathBuilder()
                                .path(String.valueOf(id))
                                .build()
                )
                .entity(new RoomReportIdResponse(id))
                .build();
    }

    @PATCH
    @Path("/{id}")
    public RoomReportIdResponse resolve(
            @PathParam("id") long id,
            @Valid ResolveRoomReportRequest request
    ) {
        return new RoomReportIdResponse(
                roomReportUseCase.resolve(id, request.resolved())
        );
    }
}
