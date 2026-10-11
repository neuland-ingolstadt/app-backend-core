package app.neuland.roomreports;

import app.neuland.implementation.roomreports.RoomReportNotFoundException;
import app.neuland.model.roomreport.RoomReport;
import app.neuland.model.roomreport.RoomReportCategory;
import app.neuland.ports.inbound.RoomReportUseCase;
import app.neuland.security.JwtTestFixture;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class RoomReportResourceTest {

    private static final Instant RESOLVED_AT = Instant.parse("2026-09-30T10:15:30Z");

    @InjectMock
    RoomReportUseCase roomReportUseCase;

    @Test
    void shouldReturnList() {
        when(roomReportUseCase.list()).thenReturn(List.of(
                new RoomReport(1L, "A101", RoomReportCategory.MISSING, "The beamer is not lost, it is in the cloud.", null),
                new RoomReport(2L, "B202", RoomReportCategory.OTHER, "It works on my machine, but the door is not a machine.", RESOLVED_AT)
        ));

        given()
                .auth()
                .oauth2(JwtTestFixture.reportsToken())
                .when().get("/room-reports")
                .then()
                .statusCode(200)
                .body("roomReports.size()", is(2))
                .body("roomReports.[0].id", is(1))
                .body("roomReports.[0].room", is("A101"))
                .body("roomReports.[0].reason", is("MISSING"))
                .body("roomReports.[0].description", is("The beamer is not lost, it is in the cloud."))
                .body("roomReports.[0].resolvedAt", nullValue())
                .body("roomReports.[1].id", is(2))
                .body("roomReports.[1].room", is("B202"))
                .body("roomReports.[1].resolvedAt", is("2026-09-30T10:15:30Z"));
    }

    @Test
    void shouldReturnEmptyList() {
        when(roomReportUseCase.list()).thenReturn(List.of());

        given()
                .auth()
                .oauth2(JwtTestFixture.reportsToken())
                .when().get("/room-reports")
                .then()
                .statusCode(200)
                .body("roomReports.size()", is(0));
    }

    @Test
    void shouldCreate() {
        when(roomReportUseCase.create(any())).thenReturn(7L);

        given()
                .contentType("application/json")
                .body("""
                        {
                          "room": "A101",
                          "reason": "MISSING",
                          "description": "The beamer is not lost, it is in the cloud."
                        }
                        """)
                .when().post("/room-reports")
                .then()
                .statusCode(201)
                .header("Location", endsWith("/room-reports/7"))
                .body("id", is(7));

        verify(roomReportUseCase).create(
                new RoomReport(null, "A101", RoomReportCategory.MISSING, "The beamer is not lost, it is in the cloud.", null)
        );
    }

    @Test
    void shouldCreateWithoutDescription() {
        when(roomReportUseCase.create(any())).thenReturn(8L);

        given()
                .contentType("application/json")
                .body("""
                        {
                          "room": "A101",
                          "reason": "MISSING"
                        }
                        """)
                .when().post("/room-reports")
                .then()
                .statusCode(201)
                .header("Location", endsWith("/room-reports/8"))
                .body("id", is(8));

        verify(roomReportUseCase).create(
                new RoomReport(null, "A101", RoomReportCategory.MISSING, null, null)
        );
    }

    @Test
    void shouldRejectCreateWithMissingRoom() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "reason": "MISSING",
                          "description": "The beamer is not lost, it is in the cloud."
                        }
                        """)
                .when().post("/room-reports")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("title", is("Bad Request"))
                .body("status", is(400))
                .body("detail", containsString("room"))
                .body("instance", endsWith("/room-reports"));
    }

    @Test
    void shouldRejectCreateWithUnknownReason() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "room": "A101",
                          "reason": "NOPE",
                          "description": "The beamer is not lost, it is in the cloud."
                        }
                        """)
                .when().post("/room-reports")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("title", is("Bad Request"))
                .body("status", is(400))
                .body("instance", endsWith("/room-reports"));
    }

    @Test
    void shouldResolve() {
        when(roomReportUseCase.resolve(anyLong(), any(Boolean.class))).thenReturn(3L);

        given()
                .auth()
                .oauth2(JwtTestFixture.reportsToken())
                .contentType("application/json")
                .body("""
                        {
                          "resolved": true
                        }
                        """)
                .when().patch("/room-reports/3")
                .then()
                .statusCode(200)
                .body("id", is(3));

        verify(roomReportUseCase).resolve(3L, true);
    }

    @Test
    void shouldRejectResolveWithoutResolvedFlag() {
        given()
                .auth()
                .oauth2(JwtTestFixture.reportsToken())
                .contentType("application/json")
                .body("{}")
                .when().patch("/room-reports/3")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("title", is("Bad Request"))
                .body("detail", containsString("resolved"));
    }

    @Test
    void shouldReturnNotFound() {
        when(roomReportUseCase.resolve(anyLong(), any(Boolean.class)))
                .thenThrow(new RoomReportNotFoundException(99L));

        given()
                .auth()
                .oauth2(JwtTestFixture.reportsToken())
                .contentType("application/json")
                .body("""
                        {
                          "resolved": true
                        }
                        """)
                .when().patch("/room-reports/99")
                .then()
                .statusCode(404)
                .contentType("application/problem+json")
                .body("$", not(hasKey("type")))
                .body("title", is("Not Found"))
                .body("status", is(404))
                .body("detail", is("Room report not found: 99"))
                .body("instance", endsWith("/room-reports/99"));
    }

    @Test
    void shouldReturnProblemForUnhandledException() {
        when(roomReportUseCase.list())
                .thenThrow(new IllegalStateException("database is on fire"));

        given()
                .auth()
                .oauth2(JwtTestFixture.reportsToken())
                .when().get("/room-reports")
                .then()
                .statusCode(500)
                .contentType("application/problem+json")
                .body("title", is("Internal Server Error"))
                .body("status", is(500))
                .body("detail", is("Unexpected error"))
                .body("detail", not(containsString("database is on fire")))
                .body("instance", endsWith("/room-reports"));
    }

    @Test
    void shouldReturnProblemForUnknownRoute() {
        given()
                .when().get("/nope")
                .then()
                .statusCode(404)
                .contentType("application/problem+json")
                .body("title", is("Not Found"))
                .body("status", is(404))
                .body("instance", endsWith("/nope"));
    }

    @Test
    void shouldRejectListWithoutAuthentication() {
        given()
                .when()
                .get("/room-reports")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldRejectListWithInvalidToken() {
        given()
                .auth()
                .oauth2("invalid-token")
                .when()
                .get("/room-reports")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldRejectListWithWrongGroup() {
        given()
                .auth()
                .oauth2(JwtTestFixture.wrongGroupToken())
                .when()
                .get("/room-reports")
                .then()
                .statusCode(403);
    }

    @Test
    void shouldAllowListForReportsRole() {
        when(roomReportUseCase.list()).thenReturn(List.of());

        given()
                .auth()
                .oauth2(JwtTestFixture.reportsToken())
                .when()
                .get("/room-reports")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldAllowListForAdminRole() {
        when(roomReportUseCase.list()).thenReturn(List.of());

        given()
                .auth()
                .oauth2(JwtTestFixture.adminToken())
                .when()
                .get("/room-reports")
                .then()
                .statusCode(200);
    }
}
