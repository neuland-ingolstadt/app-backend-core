package app.neuland.universitysports;

import app.neuland.implementation.universitysports.SportsNotFoundException;
import app.neuland.model.shared.Language;
import app.neuland.model.universitysports.Campus;
import app.neuland.model.universitysports.Sports;
import app.neuland.model.universitysports.SportsCategory;
import app.neuland.model.universitysports.SportsContent;
import app.neuland.model.universitysports.Weekday;
import app.neuland.ports.inbound.UniversitySportsUseCase;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class UniversitySportsResourceTest {

    private static final LocalTime START = LocalTime.of(18, 0);
    private static final LocalTime END = LocalTime.of(19, 30);
    private static final String INVITATION_LINK = "https://example.com/invite";
    private static final String EMAIL = "test@example.com";

    @InjectMock
    UniversitySportsUseCase universitySportsUseCase;

    @Test
    void shouldReturnList() {
        when(universitySportsUseCase.list()).thenReturn(List.of(
                sports(1L),
                sports(2L, null, null, null)
        ));

        given()
                .when().get("/university-sports")
                .then()
                .statusCode(200)
                .body("sports.size()", is(2))
                .body("sports.[0].id", is(1))
                .body("sports.[0].contents.DE.title", is("Basketball"))
                .body("sports.[0].contents.DE.description", is("Beschreibung"))
                .body("sports.[0].contents.EN.title", is("Basketball"))
                .body("sports.[0].campus", is("INGOLSTADT"))
                .body("sports.[0].location", is("Sporthalle"))
                .body("sports.[0].weekday", is("MONDAY"))
                .body("sports.[0].startTime", is("18:00:00Z"))
                .body("sports.[0].endTime", is("19:30:00Z"))
                .body("sports.[0].requiresRegistration", is(true))
                .body("sports.[0].invitationLink", is(INVITATION_LINK))
                .body("sports.[0].email", is(EMAIL))
                .body("sports.[0].sportsCategory", is("BASKETBALL"))
                .body("sports.[1].id", is(2))
                .body("sports.[1].endTime", nullValue())
                .body("sports.[1].invitationLink", nullValue())
                .body("sports.[1].email", nullValue());
    }

    @Test
    void shouldReturnEmptyList() {
        when(universitySportsUseCase.list()).thenReturn(List.of());

        given()
                .when().get("/university-sports")
                .then()
                .statusCode(200)
                .body("sports.size()", is(0));
    }

    @Test
    void shouldReturnSingleSports() {
        when(universitySportsUseCase.get(1L)).thenReturn(sports(1L));

        given()
                .when().get("/university-sports/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("contents.DE.title", is("Basketball"))
                .body("campus", is("INGOLSTADT"))
                .body("weekday", is("MONDAY"))
                .body("startTime", is("18:00:00Z"))
                .body("sportsCategory", is("BASKETBALL"));
    }

    @Test
    void shouldCreate() {
        when(universitySportsUseCase.create(any())).thenReturn(sports(7L));

        given()
                .contentType("application/json")
                .body("""
                        {
                          "contents": {
                            "DE": {
                              "title": "Basketball",
                              "description": "Beschreibung"
                            }
                          },
                          "campus": "INGOLSTADT",
                          "location": "Sporthalle",
                          "weekday": "MONDAY",
                          "startTime": "18:00:00Z",
                          "endTime": "19:30:00Z",
                          "requiresRegistration": true,
                          "invitationLink": "https://example.com/invite",
                          "email": "test@example.com",
                          "sportsCategory": "BASKETBALL"
                        }
                        """)
                .when().post("/university-sports")
                .then()
                .statusCode(201)
                .header("Location", endsWith("/university-sports/7"))
                .body("id", is(7))
                .body("contents.DE.title", is("Basketball"))
                .body("startTime", is("18:00:00Z"))
                .body("invitationLink", is(INVITATION_LINK));

        verify(universitySportsUseCase).create(new Sports(
                null,
                Map.of(Language.DE, new SportsContent("Basketball", "Beschreibung")),
                Campus.INGOLSTADT,
                "Sporthalle",
                Weekday.MONDAY,
                START,
                END,
                true,
                INVITATION_LINK,
                EMAIL,
                SportsCategory.BASKETBALL
        ));
    }

    @Test
    void shouldCreateWithoutOptionalFields() {
        when(universitySportsUseCase.create(any())).thenReturn(sports(8L, null, null, null));

        given()
                .contentType("application/json")
                .body("""
                        {
                          "contents": {
                            "DE": {
                              "title": "Basketball",
                              "description": "Beschreibung"
                            }
                          },
                          "campus": "INGOLSTADT",
                          "location": "Sporthalle",
                          "weekday": "MONDAY",
                          "startTime": "18:00:00Z",
                          "requiresRegistration": true,
                          "sportsCategory": "BASKETBALL"
                        }
                        """)
                .when().post("/university-sports")
                .then()
                .statusCode(201)
                .header("Location", endsWith("/university-sports/8"))
                .body("id", is(8))
                .body("endTime", nullValue())
                .body("invitationLink", nullValue())
                .body("email", nullValue());

        verify(universitySportsUseCase).create(new Sports(
                null,
                Map.of(Language.DE, new SportsContent("Basketball", "Beschreibung")),
                Campus.INGOLSTADT,
                "Sporthalle",
                Weekday.MONDAY,
                START,
                null,
                true,
                null,
                null,
                SportsCategory.BASKETBALL
        ));
    }

    @Test
    void shouldRejectCreateWithMissingContents() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "campus": "INGOLSTADT",
                          "location": "Sporthalle",
                          "weekday": "MONDAY",
                          "startTime": "18:00:00Z",
                          "requiresRegistration": true,
                          "sportsCategory": "BASKETBALL"
                        }
                        """)
                .when().post("/university-sports")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("title", is("Bad Request"))
                .body("status", is(400))
                .body("detail", containsString("contents"))
                .body("instance", endsWith("/university-sports"));
    }

    @Test
    void shouldRejectCreateWithUnknownCampus() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "contents": {
                            "DE": {
                              "title": "Basketball",
                              "description": "Beschreibung"
                            }
                          },
                          "campus": "NOPE",
                          "location": "Sporthalle",
                          "weekday": "MONDAY",
                          "startTime": "18:00:00Z",
                          "requiresRegistration": true,
                          "sportsCategory": "BASKETBALL"
                        }
                        """)
                .when().post("/university-sports")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("title", is("Bad Request"))
                .body("status", is(400))
                .body("instance", endsWith("/university-sports"));
    }

    @Test
    void shouldUpdate() {
        when(universitySportsUseCase.update(anyLong(), any())).thenReturn(sports(3L, null, null, null));

        given()
                .contentType("application/json")
                .body("""
                        {
                          "contents": {
                            "DE": {
                              "title": "Volleyball",
                              "description": "Neue Beschreibung"
                            }
                          },
                          "campus": "NEUBURG",
                          "location": "Sportplatz",
                          "weekday": "TUESDAY",
                          "startTime": "17:00:00Z",
                          "requiresRegistration": false,
                          "sportsCategory": "VOLLEYBALL"
                        }
                        """)
                .when().patch("/university-sports/3")
                .then()
                .statusCode(200)
                .body("id", is(3))
                .body("contents.DE.title", is("Basketball"));

        verify(universitySportsUseCase).update(3L, new Sports(
                3L,
                Map.of(Language.DE, new SportsContent("Volleyball", "Neue Beschreibung")),
                Campus.NEUBURG,
                "Sportplatz",
                Weekday.TUESDAY,
                LocalTime.of(17, 0),
                null,
                false,
                null,
                null,
                SportsCategory.VOLLEYBALL
        ));
    }

    @Test
    void shouldDelete() {
        given()
                .when().delete("/university-sports/5")
                .then()
                .statusCode(204);

        verify(universitySportsUseCase).delete(5L);
    }

    @Test
    void shouldReturnNotFoundOnGet() {
        when(universitySportsUseCase.get(anyLong()))
                .thenThrow(new SportsNotFoundException(99L));

        given()
                .when().get("/university-sports/99")
                .then()
                .statusCode(404)
                .contentType("application/problem+json")
                .body("title", is("Not Found"))
                .body("status", is(404))
                .body("detail", is("Sport not found: 99"))
                .body("instance", endsWith("/university-sports/99"));
    }

    @Test
    void shouldReturnNotFoundOnDelete() {
        doThrow(new SportsNotFoundException(99L))
                .when(universitySportsUseCase)
                .delete(99L);

        given()
                .when().delete("/university-sports/99")
                .then()
                .statusCode(404)
                .contentType("application/problem+json")
                .body("title", is("Not Found"))
                .body("detail", is("Sport not found: 99"));
    }

    @Test
    void shouldReturnProblemForUnhandledException() {
        when(universitySportsUseCase.list())
                .thenThrow(new IllegalStateException("database is on fire"));

        given()
                .when().get("/university-sports")
                .then()
                .statusCode(500)
                .contentType("application/problem+json")
                .body("title", is("Internal Server Error"))
                .body("status", is(500))
                .body("detail", is("Unexpected error"))
                .body("instance", endsWith("/university-sports"));
    }

    private static Sports sports(Long id) {
        return sports(id, END, INVITATION_LINK, EMAIL);
    }

    private static Sports sports(Long id, LocalTime endTime, String invitationLink, String email) {
        return new Sports(
                id,
                Map.of(
                        Language.DE, new SportsContent("Basketball", "Beschreibung"),
                        Language.EN, new SportsContent("Basketball", "Description")
                ),
                Campus.INGOLSTADT,
                "Sporthalle",
                Weekday.MONDAY,
                START,
                endTime,
                true,
                invitationLink,
                email,
                SportsCategory.BASKETBALL
        );
    }
}

