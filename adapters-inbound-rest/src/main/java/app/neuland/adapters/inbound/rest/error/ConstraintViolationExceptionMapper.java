package app.neuland.adapters.inbound.rest.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper
        extends AbstractProblemExceptionMapper<ConstraintViolationException> {

    @Override
    protected Problem problem(ConstraintViolationException exception) {
        return problem(Response.Status.BAD_REQUEST, detail(exception));
    }

    private String detail(ConstraintViolationException exception) {
        return exception.getConstraintViolations()
                .stream()
                .map(this::toDetail)
                .collect(Collectors.joining(", "));
    }

    private String toDetail(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + ": " + violation.getMessage();
    }
}
