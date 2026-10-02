package app.neuland.adapters.inbound.rest.error;

import jakarta.ws.rs.WebApplicationException;
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
class UnhandledExceptionMapperTest {

    @Mock
    UriInfo uriInfo;

    private UnhandledExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UnhandledExceptionMapper();
        mapper.uriInfo = uriInfo;
        when(uriInfo.getRequestUri()).thenReturn(URI.create("http://localhost/room-reports"));
    }

    @Test
    void shouldMapWebApplicationExceptionStatus() {
        Response response = mapper.toResponse(new WebApplicationException(Response.Status.NOT_FOUND));

        Problem problem = (Problem) response.getEntity();
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", problem.title());
    }

    @Test
    void shouldFallbackWhenStatusCodeIsUnknown() {
        Response response = mapper.toResponse(
                new WebApplicationException(Response.status(499).build())
        );

        Problem problem = (Problem) response.getEntity();
        assertEquals(500, response.getStatus());
        assertEquals("Internal Server Error", problem.title());
    }

    @Test
    void shouldMapUnhandledThrowable() {
        Response response = mapper.toResponse(new IllegalStateException("boom"));

        Problem problem = (Problem) response.getEntity();
        assertEquals(500, response.getStatus());
        assertEquals("Unexpected error", problem.detail());
    }
}
