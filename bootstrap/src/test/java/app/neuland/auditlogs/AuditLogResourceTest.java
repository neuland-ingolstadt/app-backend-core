package app.neuland.auditlogs;

import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogOperation;
import app.neuland.model.auditlog.AuditLogPage;
import app.neuland.ports.inbound.AuditLogUseCase;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class AuditLogResourceTest {

    @InjectMock
    AuditLogUseCase auditLogUseCase;

    @Test
    void shouldReturnAuditLogPage() {
        when(auditLogUseCase.list(null, null)).thenReturn(new AuditLogPage(
                List.of(
                        new AuditLogEntry(
                                1L,
                                "Announcement",
                                42L,
                                AuditLogOperation.CREATE,
                                "title",
                                "user-1",
                                Instant.parse("2026-01-01T12:00:00Z")
                        ),
                        new AuditLogEntry(
                                2L,
                                "RoomReport",
                                7L,
                                AuditLogOperation.DELETE,
                                "obsolete report",
                                "user-2",
                                Instant.parse("2026-01-02T13:30:00Z")
                        )
                ),
                "next-page"
        ));

        given()
                .when().get("/audit-logs")
                .then()
                .statusCode(200)
                .body("auditLogs.size()", is(2))
                .body("auditLogs.[0].id", is(1))
                .body("auditLogs.[0].entity", is("Announcement"))
                .body("auditLogs.[0].entityId", is(42))
                .body("auditLogs.[0].operation", is("CREATE"))
                .body("auditLogs.[0].name", is("title"))
                .body("auditLogs.[0].userId", is("user-1"))
                .body("auditLogs.[0].createdAt", is("2026-01-01T12:00:00Z"))
                .body("auditLogs.[1].id", is(2))
                .body("auditLogs.[1].operation", is("DELETE"))
                .body("auditLogs.[1].createdAt", is("2026-01-02T13:30:00Z"))
                .body("nextCursor", is("next-page"));
    }

    @Test
    void shouldReturnEmptyAuditLogPage() {
        when(auditLogUseCase.list(null, null)).thenReturn(new AuditLogPage(
                List.of(),
                null
        ));

        given()
                .when().get("/audit-logs")
                .then()
                .statusCode(200)
                .body("auditLogs.size()", is(0))
                .body("nextCursor", nullValue());
    }

    @Test
    void shouldPassPaginationParametersToUseCase() {
        when(auditLogUseCase.list(10L, "next-page")).thenReturn(new AuditLogPage(
                List.of(),
                null
        ));

        given()
                .queryParam("limit", 10)
                .queryParam("cursor", "next-page")
                .when().get("/audit-logs")
                .then()
                .statusCode(200);

        verify(auditLogUseCase).list(10L, "next-page");
    }

    @Test
    void shouldReturnProblemForInvalidCursor() {
        when(auditLogUseCase.list(null, "invalid-cursor"))
                .thenThrow(new IllegalArgumentException("Invalid cursor"));

        given()
                .queryParam("cursor", "invalid-cursor")
                .when().get("/audit-logs")
                .then()
                .statusCode(500)
                .contentType("application/problem+json")
                .body("title", is("Internal Server Error"))
                .body("status", is(500))
                .body("detail", is("Unexpected error"))
                .body("detail", not(containsString("Invalid cursor")));
    }
}
