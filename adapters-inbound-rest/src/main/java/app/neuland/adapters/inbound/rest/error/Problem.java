package app.neuland.adapters.inbound.rest.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Problem(
        String type,
        String title,
        int status,
        String detail,
        String instance
) {

    public static final MediaType MEDIA_TYPE = MediaType.valueOf("application/problem+json");

    public static Problem of(Response.Status status) {
        return of(status, null, null, null);
    }

    public static Problem of(Response.Status status, String detail) {
        return of(status, null, detail, null);
    }

    public static Problem of(Response.Status status, String type, String detail, String instance) {
        return new Problem(
                type,
                status.getReasonPhrase(),
                status.getStatusCode(),
                detail,
                instance
        );
    }

    public Response toResponse() {
        return Response
                .status(status)
                .type(MEDIA_TYPE)
                .entity(this)
                .build();
    }
}
