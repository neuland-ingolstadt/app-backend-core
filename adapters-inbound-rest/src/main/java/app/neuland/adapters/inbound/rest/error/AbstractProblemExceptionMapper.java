package app.neuland.adapters.inbound.rest.error;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;

public abstract class AbstractProblemExceptionMapper<E extends Throwable>
        implements ExceptionMapper<E> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(E exception) {
        return problem(exception).toResponse();
    }

    protected abstract Problem problem(E exception);

    protected Problem problem(Response.Status status) {
        return problem(status, null);
    }

    protected Problem problem(Response.Status status, String detail) {
        return Problem.of(
                status,
                null,
                detail,
                uriInfo.getRequestUri().toString()
        );
    }
}
