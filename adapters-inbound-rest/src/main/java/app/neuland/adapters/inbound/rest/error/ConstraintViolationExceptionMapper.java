package app.neuland.adapters.inbound.rest.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<FieldViolation> violations = exception.getConstraintViolations()
                .stream()
                .map(this::toFieldViolation)
                .toList();

        String detail = violations
                .stream()
                .map(FieldViolation::toDetail)
                .collect(Collectors.joining(", "));

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(
                        new ErrorResponse(
                                Response.Status.BAD_REQUEST.getStatusCode(),
                                detail,
                                violations
                        )
                )
                .build();
    }

    private FieldViolation toFieldViolation(ConstraintViolation<?> violation) {
        return new FieldViolation(
                violation.getPropertyPath().toString(),
                violation.getMessage()
        );
    }
}
