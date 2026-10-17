package app.neuland.adapters.inbound.rest.error;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper
        extends AbstractProblemExceptionMapper<IllegalArgumentException> {

    @Override
    protected Problem problem(IllegalArgumentException exception) {
        return problem(Response.Status.BAD_REQUEST, detail(exception));
    }

    private String detail(IllegalArgumentException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return "Bad request";
        }
        return message;
    }
}
