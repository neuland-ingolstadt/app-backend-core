package app.neuland.adapters.inbound.rest.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class UnhandledExceptionMapper
        extends AbstractProblemExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(UnhandledExceptionMapper.class);

    @Override
    protected Problem problem(Throwable exception) {
        if (exception instanceof WebApplicationException webApplicationException) {
            return problem(statusOf(webApplicationException));
        }

        LOG.error("Unhandled exception", exception);

        return problem(Response.Status.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    private Response.Status statusOf(WebApplicationException exception) {
        Response.Status status = Response.Status.fromStatusCode(
                exception.getResponse()
                        .getStatus()
        );
        return status != null ? status : Response.Status.INTERNAL_SERVER_ERROR;
    }
}
