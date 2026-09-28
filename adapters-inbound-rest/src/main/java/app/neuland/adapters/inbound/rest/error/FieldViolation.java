package app.neuland.adapters.inbound.rest.error;

public record FieldViolation(
        String field,
        String message
) {
}
