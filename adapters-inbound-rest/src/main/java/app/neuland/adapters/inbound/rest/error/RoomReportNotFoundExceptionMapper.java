package app.neuland.adapters.inbound.rest.error;

import app.neuland.implementation.roomreports.RoomReportNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RoomReportNotFoundExceptionMapper
        extends AbstractProblemExceptionMapper<RoomReportNotFoundException> {

    @Override
    protected Problem problem(RoomReportNotFoundException exception) {
        return problem(Response.Status.NOT_FOUND, exception.getMessage());
    }
}
