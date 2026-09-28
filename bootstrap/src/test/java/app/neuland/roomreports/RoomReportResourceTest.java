package app.neuland.roomreports;

import app.neuland.implementation.roomreports.RoomReportNotFoundException;
import app.neuland.model.roomreport.RoomReport;
import app.neuland.model.roomreport.RoomReportCategory;
import app.neuland.ports.inbound.RoomReportUseCase;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class RoomReportResourceTest {

    @InjectMock
    RoomReportUseCase roomReportUseCase;

    @Test
    void shouldReturnList() {
        when(roomReportUseCase.list()).thenReturn(List.of(
                new RoomReport(1L, "A.101", RoomReportCategory.MISSING, "Beamer fehlt", false),
                new RoomReport(2L, "B.202", RoomReportCategory.OTHER, "Tür klemmt", true)
        ));

        given()
                .when().get("/room-reports")
                .then()
                .statusCode(200)
                .body("roomReports.size()", is(2))
                .body("roomReports.[0].id", is(1))
                .body("roomReports.[0].room", is("A.101"))
                .body("roomReports.[0].reason", is("MISSING"))
                .body("roomReports.[0].description", is("Beamer fehlt"))
                .body("roomReports.[0].resolved", is(false))
                .body("roomReports.[1].id", is(2))
                .body("roomReports.[1].resolved", is(true));
    }

    @Test
    void shouldReturnEmptyList() {
        when(roomReportUseCase.list()).thenReturn(List.of());

        given()
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
                          "room": "A.101",
                          "reason": "MISSING",
                          "description": "Beamer fehlt"
                        }
                        """)
                .when().post("/room-reports")
                .then()
                .statusCode(201)
                .header("Location", endsWith("/room-reports/7"))
                .body("id", is(7));

        verify(roomReportUseCase).create(
                new RoomReport(null, "A.101", RoomReportCategory.MISSING, "Beamer fehlt", false)
        );
    }

    @Test
    void shouldRejectCreateWithBlankRoom() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "room": "  ",
                          "reason": "MISSING",
                          "description": "Beamer fehlt"
                        }
                        """)
                .when().post("/room-reports")
                .then()
                .statusCode(400)
                .body("status", is(400))
                .body("violations[0].message", is("must not be blank"));
    }

    @Test
    void shouldRejectCreateWithUnknownReason() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "room": "A.101",
                          "reason": "NOPE",
                          "description": "Beamer fehlt"
                        }
                        """)
                .when().post("/room-reports")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldResolve() {
        when(roomReportUseCase.resolve(anyLong(), any(Boolean.class))).thenReturn(3L);

        given()
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
                .contentType("application/json")
                .body("{}")
                .when().patch("/room-reports/3")
                .then()
                .statusCode(400)
                .body("violations[0].message", is("must not be null"));
    }

    @Test
    void shouldReturnNotFound() {
        when(roomReportUseCase.resolve(anyLong(), any(Boolean.class)))
                .thenThrow(new RoomReportNotFoundException(99L));

        given()
                .contentType("application/json")
                .body("""
                        {
                          "resolved": true
                        }
                        """)
                .when().patch("/room-reports/99")
                .then()
                .statusCode(404)
                .body("status", is(404))
                .body("detail", is("Room report not found: 99"));
    }
}
