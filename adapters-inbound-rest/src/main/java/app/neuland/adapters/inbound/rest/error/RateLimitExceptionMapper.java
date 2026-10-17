package app.neuland.adapters.inbound.rest.error;

import io.smallrye.faulttolerance.api.RateLimitException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RateLimitExceptionMapper
        extends AbstractProblemExceptionMapper<RateLimitException> {

    @Override
    protected Problem problem(RateLimitException exception) {
        return problem(Response.Status.TOO_MANY_REQUESTS, "Rate limit exceeded, please retry later");
    }
}
