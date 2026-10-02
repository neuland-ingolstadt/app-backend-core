package app.neuland.adapters.inbound.rest.error;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProblemTest {

    @Test
    void shouldCreateFromStatusOnly() {
        Problem problem = Problem.of(Response.Status.NOT_FOUND);

        assertEquals("Not Found", problem.title());
        assertEquals(404, problem.status());
        assertNull(problem.type());
        assertNull(problem.detail());
        assertNull(problem.instance());
    }

    @Test
    void shouldCreateFromStatusAndDetail() {
        Problem problem = Problem.of(Response.Status.BAD_REQUEST, "broken");

        assertEquals("Bad Request", problem.title());
        assertEquals(400, problem.status());
        assertEquals("broken", problem.detail());
        assertNull(problem.type());
        assertNull(problem.instance());
    }

    @Test
    void shouldBuildProblemResponse() {
        Response response = Problem.of(Response.Status.INTERNAL_SERVER_ERROR, "boom").toResponse();

        assertEquals(500, response.getStatus());
        assertEquals(Problem.MEDIA_TYPE, response.getMediaType());
    }
}
