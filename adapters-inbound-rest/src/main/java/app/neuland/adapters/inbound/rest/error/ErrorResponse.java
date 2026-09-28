package app.neuland.adapters.inbound.rest.error;

import java.util.List;

public record ErrorResponse(
        int status,
        String detail,
        List<FieldViolation> violations
) {

    public ErrorResponse(int status, String detail) {
        this(status, detail, List.of());
    }
}
