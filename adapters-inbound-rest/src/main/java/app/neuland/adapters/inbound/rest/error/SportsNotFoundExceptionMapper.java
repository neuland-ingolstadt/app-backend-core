package app.neuland.adapters.inbound.rest.error;

import app.neuland.implementation.universitysports.SportsNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SportsNotFoundExceptionMapper
        extends AbstractProblemExceptionMapper<SportsNotFoundException> {

    @Override
    protected Problem problem(SportsNotFoundException exception) {
        return problem(Response.Status.NOT_FOUND, exception.getMessage());
    }
}
