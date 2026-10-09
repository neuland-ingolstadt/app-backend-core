package app.neuland.announcements;

import app.neuland.implementation.announcements.AnnouncementNotFoundException;
import app.neuland.model.announcement.Announcement;
import app.neuland.model.announcement.AnnouncementContent;
import app.neuland.model.announcement.Platform;
import app.neuland.model.announcement.UserKind;
import app.neuland.model.shared.Language;
import app.neuland.ports.inbound.AnnouncementUseCase;
import app.neuland.security.JwtTestFixture;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class AnnouncementResourceTest {

    private static final Instant START = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant END = Instant.parse("2026-12-31T23:59:59Z");
    private static final String URL = "https://example.com";
    private static final String IMAGE_URL = "https://example.com/image.png";

    @InjectMock
    AnnouncementUseCase announcementUseCase;

    @Test
    void shouldReturnList() {
        when(announcementUseCase.list(false)).thenReturn(List.of(
                announcement(1L),
                announcement(2L, null, null)
        ));

        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .when().get("/announcements")
                .then()
                .statusCode(200)
                .body("announcements.size()", is(2))
                .body("announcements.[0].id", is(1))
                .body("announcements.[0].platforms", containsInAnyOrder("WEB"))
                .body("announcements.[0].userKinds", containsInAnyOrder("STUDENT", "GUEST"))
                .body("announcements.[0].contents.DE.title", is("Titel"))
                .body("announcements.[0].contents.DE.description", is("Beschreibung"))
                .body("announcements.[0].contents.EN.title", is("Title"))
                .body("announcements.[0].startDateTime", is("2026-01-01T00:00:00Z"))
                .body("announcements.[0].endDateTime", is("2026-12-31T23:59:59Z"))
                .body("announcements.[0].priority", is(1))
                .body("announcements.[0].url", is(URL))
                .body("announcements.[0].imageUrl", is(IMAGE_URL))
                .body("announcements.[1].id", is(2))
                .body("announcements.[1].url", nullValue())
                .body("announcements.[1].imageUrl", nullValue());
    }

    @Test
    void shouldReturnEmptyList() {
        when(announcementUseCase.list(false)).thenReturn(List.of());

        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .when().get("/announcements")
                .then()
                .statusCode(200)
                .body("announcements.size()", is(0));
    }

    @Test
    void shouldIncludeInactiveWhenRequested() {
        when(announcementUseCase.list(true)).thenReturn(List.of(announcement(1L)));

        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .queryParam("includeInactive", true)
                .when().get("/announcements")
                .then()
                .statusCode(200)
                .body("announcements.size()", is(1));

        verify(announcementUseCase).list(true);
    }

    @Test
    void shouldReturnSingleAnnouncement() {
        when(announcementUseCase.get(1L)).thenReturn(announcement(1L));

        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .when().get("/announcements/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("platforms", containsInAnyOrder("WEB"))
                .body("contents.DE.title", is("Titel"));
    }

    @Test
    void shouldCreate() {
        when(announcementUseCase.create(any())).thenReturn(announcement(7L));

        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .contentType("application/json")
                .body("""
                        {
                          "platforms": ["WEB"],
                          "userKinds": ["STUDENT"],
                          "contents": {
                            "DE": {
                              "title": "Titel",
                              "description": "Beschreibung"
                            }
                          },
                          "startDateTime": "2026-01-01T00:00:00Z",
                          "endDateTime": "2026-12-31T23:59:59Z",
                          "priority": 1,
                          "url": "https://example.com",
                          "imageUrl": "https://example.com/image.png"
                        }
                        """)
                .when().post("/announcements")
                .then()
                .statusCode(201)
                .header("Location", endsWith("/announcements/7"))
                .body("id", is(7))
                .body("contents.DE.title", is("Titel"))
                .body("url", is(URL))
                .body("imageUrl", is(IMAGE_URL));

        verify(announcementUseCase).create(new Announcement(
                null,
                Set.of(Platform.WEB),
                Set.of(UserKind.STUDENT),
                Map.of(Language.DE, new AnnouncementContent("Titel", "Beschreibung")),
                START,
                END,
                1,
                URL,
                IMAGE_URL
        ));
    }

    @Test
    void shouldCreateWithoutOptionalUrls() {
        when(announcementUseCase.create(any())).thenReturn(announcement(8L, null, null));

        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .contentType("application/json")
                .body("""
                        {
                          "platforms": ["WEB"],
                          "userKinds": ["STUDENT"],
                          "contents": {
                            "DE": {
                              "title": "Titel",
                              "description": "Beschreibung"
                            }
                          },
                          "startDateTime": "2026-01-01T00:00:00Z",
                          "endDateTime": "2026-12-31T23:59:59Z",
                          "priority": 1
                        }
                        """)
                .when().post("/announcements")
                .then()
                .statusCode(201)
                .header("Location", endsWith("/announcements/8"))
                .body("id", is(8))
                .body("url", nullValue())
                .body("imageUrl", nullValue());

        verify(announcementUseCase).create(new Announcement(
                null,
                Set.of(Platform.WEB),
                Set.of(UserKind.STUDENT),
                Map.of(Language.DE, new AnnouncementContent("Titel", "Beschreibung")),
                START,
                END,
                1,
                null,
                null
        ));
    }

    @Test
    void shouldRejectCreateWithMissingContents() {
        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .contentType("application/json")
                .body("""
                        {
                          "platforms": ["WEB"],
                          "userKinds": ["STUDENT"],
                          "startDateTime": "2026-01-01T00:00:00Z",
                          "endDateTime": "2026-12-31T23:59:59Z",
                          "priority": 1
                        }
                        """)
                .when().post("/announcements")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("title", is("Bad Request"))
                .body("status", is(400))
                .body("detail", containsString("contents"))
                .body("instance", endsWith("/announcements"));
    }

    @Test
    void shouldRejectCreateWithUnknownPlatform() {
        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .contentType("application/json")
                .body("""
                        {
                          "platforms": ["NOPE"],
                          "userKinds": ["STUDENT"],
                          "contents": {
                            "DE": {
                              "title": "Titel",
                              "description": "Beschreibung"
                            }
                          },
                          "startDateTime": "2026-01-01T00:00:00Z",
                          "endDateTime": "2026-12-31T23:59:59Z",
                          "priority": 1
                        }
                        """)
                .when().post("/announcements")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("title", is("Bad Request"))
                .body("status", is(400))
                .body("instance", endsWith("/announcements"));
    }

    @Test
    void shouldUpdate() {
        when(announcementUseCase.update(anyLong(), any())).thenReturn(announcement(3L, null, null));

        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .contentType("application/json")
                .body("""
                        {
                          "platforms": ["WEB"],
                          "userKinds": ["GUEST"],
                          "contents": {
                            "DE": {
                              "title": "Neuer Titel",
                              "description": "Neue Beschreibung"
                            }
                          },
                          "startDateTime": "2026-01-01T00:00:00Z",
                          "endDateTime": "2026-12-31T23:59:59Z",
                          "priority": 5
                        }
                        """)
                .when().patch("/announcements/3")
                .then()
                .statusCode(200)
                .body("id", is(3))
                .body("platforms", containsInAnyOrder("WEB"))
                .body("contents.DE.title", is("Titel"));

        verify(announcementUseCase).update(3L, new Announcement(
                3L,
                Set.of(Platform.WEB),
                Set.of(UserKind.GUEST),
                Map.of(Language.DE, new AnnouncementContent("Neuer Titel", "Neue Beschreibung")),
                START,
                END,
                5,
                null,
                null
        ));
    }

    @Test
    void shouldDelete() {
        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .when().delete("/announcements/5")
                .then()
                .statusCode(204);

        verify(announcementUseCase).delete(5L);
    }

    @Test
    void shouldReturnNotFoundOnGet() {
        when(announcementUseCase.get(anyLong()))
                .thenThrow(new AnnouncementNotFoundException(99L));

        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .when().get("/announcements/99")
                .then()
                .statusCode(404)
                .contentType("application/problem+json")
                .body("title", is("Not Found"))
                .body("status", is(404))
                .body("detail", is("Announcement not found: 99"))
                .body("instance", endsWith("/announcements/99"));
    }

    @Test
    void shouldReturnNotFoundOnDelete() {
        doThrow(new AnnouncementNotFoundException(99L))
                .when(announcementUseCase)
                .delete(99L);

        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .when().delete("/announcements/99")
                .then()
                .statusCode(404)
                .contentType("application/problem+json")
                .body("title", is("Not Found"))
                .body("detail", is("Announcement not found: 99"));
    }

    @Test
    void shouldReturnProblemForUnhandledException() {
        when(announcementUseCase.list(anyBoolean()))
                .thenThrow(new IllegalStateException("database is on fire"));

        given()
                .auth()
                .oauth2(JwtTestFixture.announcementToken())
                .when().get("/announcements")
                .then()
                .statusCode(500)
                .contentType("application/problem+json")
                .body("title", is("Internal Server Error"))
                .body("status", is(500))
                .body("detail", is("Unexpected error"))
                .body("instance", endsWith("/announcements"));
    }

     @Test
     void shouldRejectUnauthenticatedCreate() {
        given()
             .contentType("application/json")
             .body("""
                        {
                          "platforms": ["WEB"],
                          "userKinds": ["GUEST"],
                          "contents": {
                            "DE": {
                              "title": "Neuer Titel",
                              "description": "Neue Beschreibung"
                            }
                          },
                          "startDateTime": "2026-01-01T00:00:00Z",
                          "endDateTime": "2026-12-31T23:59:59Z",
                          "priority": 5
                        }
                        """)
             .when()
             .post("/announcements")
             .then()
             .statusCode(401);
     }
     
     @Test
     void shouldRejectUnauthenticatedUpdate() {
        given()
             .when()
             .patch("/announcements/3")
             .then()
             .statusCode(401);
     }
     
     @Test
     void shouldRejectUnauthenticatedDelete() {
        given()
             .when()
             .delete("/announcements/5")
             .then()
             .statusCode(401);
     }
     
     @Test
     void shouldRejectCreateForUserWithoutAnnouncementRole() {
        given()
             .auth()
             .oauth2(JwtTestFixture.reportsToken())
             .contentType("application/json")
             .body("""
                        {
                          "platforms": ["WEB"],
                          "userKinds": ["GUEST"],
                          "contents": {
                            "DE": {
                              "title": "Neuer Titel",
                              "description": "Neue Beschreibung"
                            }
                          },
                          "startDateTime": "2026-01-01T00:00:00Z",
                          "endDateTime": "2026-12-31T23:59:59Z",
                          "priority": 5
                        }
                        """)
             .when()
             .post("/announcements")
             .then()
             .statusCode(403);
     }
     
     @Test
     void shouldRejectUpdateForUserWithoutAnnouncementRole() {
        given()
             .auth()
             .oauth2(JwtTestFixture.reportsToken())
             .when()
             .patch("/announcements/3")
             .then()
             .statusCode(403);
     }
     
     @Test
     void shouldRejectDeleteForUserWithoutAnnouncementRole() {
        given()
             .auth()
             .oauth2(JwtTestFixture.reportsToken())
             .when()
             .delete("/announcements/5")
             .then()
             .statusCode(403);
     }

    private static Announcement announcement(Long id) {
        return announcement(id, URL, IMAGE_URL);
    }

    private static Announcement announcement(Long id, String url, String imageUrl) {
        return new Announcement(
                id,
                Set.of(Platform.WEB),
                Set.of(UserKind.STUDENT, UserKind.GUEST),
                Map.of(
                        Language.DE, new AnnouncementContent("Titel", "Beschreibung"),
                        Language.EN, new AnnouncementContent("Title", "Description")
                ),
                START,
                END,
                1,
                url,
                imageUrl
        );
    }
}
