package app.neuland.adapters.inbound.rest.error;

import io.smallrye.faulttolerance.api.RateLimitException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitExceptionMapperTest {

    @Mock
    UriInfo uriInfo;

    @Test
    void shouldMapToTooManyRequestsProblem() {
        when(uriInfo.getRequestUri()).thenReturn(URI.create("http://localhost/room-reports"));
        RateLimitExceptionMapper mapper = new RateLimitExceptionMapper();
        mapper.uriInfo = uriInfo;

        try(Response response = mapper.toResponse(new RateLimitException("limit"))) {
            Problem problem = (Problem) response.getEntity();
            assertEquals(429, response.getStatus());
            assertEquals("Too Many Requests", problem.title());
            assertEquals(429, problem.status());
            assertEquals("http://localhost/room-reports", problem.instance());
            assertEquals("application/problem+json", response.getMediaType().toString());
        }
    }
}
