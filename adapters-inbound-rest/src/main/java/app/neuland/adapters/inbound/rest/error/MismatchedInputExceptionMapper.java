package app.neuland.adapters.inbound.rest.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER - 1)
public class MismatchedInputExceptionMapper
        extends AbstractProblemExceptionMapper<MismatchedInputException> {

    @Override
    protected Problem problem(MismatchedInputException exception) {
        return problem(Response.Status.BAD_REQUEST, detail(exception));
    }

    private String detail(MismatchedInputException exception) {
        String message = exception.getOriginalMessage() != null
                ? exception.getOriginalMessage()
                : exception.getMessage();

        String field = exception.getPath()
                .stream()
                .map(reference -> reference.getFieldName())
                .filter(name -> name != null && !name.isBlank())
                .findFirst()
                .orElse(null);

        if (field == null && exception instanceof InvalidFormatException invalidFormat) {
            field = invalidFormat.getPath()
                    .stream()
                    .map(reference -> reference.getFieldName())
                    .filter(name -> name != null && !name.isBlank())
                    .findFirst()
                    .orElse("value");
            if (message == null || message.isBlank()) {
                message = "Invalid value: " + invalidFormat.getValue();
            }
        }

        if (field == null) {
            return message;
        }

        return field + ": " + message;
    }
}
