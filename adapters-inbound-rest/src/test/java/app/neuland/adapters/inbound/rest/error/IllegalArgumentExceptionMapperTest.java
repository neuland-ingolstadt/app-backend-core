package app.neuland.adapters.inbound.rest.error;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IllegalArgumentExceptionMapperTest {

    @Mock
    UriInfo uriInfo;

    private IllegalArgumentExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IllegalArgumentExceptionMapper();
        mapper.uriInfo = uriInfo;
        when(uriInfo.getRequestUri()).thenReturn(URI.create("http://localhost/audit-logs"));
    }

    @Test
    void shouldMapToBadRequestWithMessage() {
        Response response = mapper.toResponse(new IllegalArgumentException("Invalid cursor"));

        Problem problem = (Problem) response.getEntity();
        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", problem.title());
        assertEquals("Invalid cursor", problem.detail());
        assertEquals("http://localhost/audit-logs", problem.instance());
    }

    @Test
    void shouldFallbackWhenMessageIsNull() {
        Response response = mapper.toResponse(new IllegalArgumentException((String) null));

        Problem problem = (Problem) response.getEntity();
        assertEquals(400, response.getStatus());
        assertEquals("Bad request", problem.detail());
    }

    @Test
    void shouldFallbackWhenMessageIsBlank() {
        Response response = mapper.toResponse(new IllegalArgumentException("   "));

        Problem problem = (Problem) response.getEntity();
        assertEquals(400, response.getStatus());
        assertEquals("Bad request", problem.detail());
    }
}
