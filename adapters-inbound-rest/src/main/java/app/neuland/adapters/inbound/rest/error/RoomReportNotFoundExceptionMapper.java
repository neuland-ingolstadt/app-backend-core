package app.neuland.adapters.inbound.rest.error;

import app.neuland.implementation.roomreports.RoomReportNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RoomReportNotFoundExceptionMapper
        implements ExceptionMapper<RoomReportNotFoundException> {

    @Override
    public Response toResponse(RoomReportNotFoundException exception) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(
                        new ErrorResponse(
                                Response.Status.NOT_FOUND.getStatusCode(),
                                exception.getMessage()
                        )
                )
                .build();
    }
}
