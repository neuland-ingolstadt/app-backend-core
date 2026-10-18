package app.neuland.ratelimit;

import app.neuland.ports.inbound.RoomReportUseCase;
import app.neuland.security.JwtTestFixture;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile(RoomReportRateLimitTest.LowLimitProfile.class)
class RoomReportRateLimitTest {

    private static final String CLASS =
            "app.neuland.adapters.inbound.rest.roomreports.RoomReportResource";
    private static final String BODY = """
            {"room": "A101", "reason": "MISSING"}
            """;

    public static class LowLimitProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    CLASS + "/createRoomReport/RateLimit/value", "3",
                    CLASS + "/createRoomReport/RateLimit/window", "1",
                    CLASS + "/createRoomReport/RateLimit/windowUnit", "MINUTES",
                    CLASS + "/resolveRoomReport/RateLimit/value", "2",
                    CLASS + "/resolveRoomReport/RateLimit/window", "1",
                    CLASS + "/resolveRoomReport/RateLimit/windowUnit", "MINUTES"
            );
        }
    }

    @InjectMock
    RoomReportUseCase roomReportUseCase;

    @Test
    void shouldReturn429ForBurstOfPostsAndKeepReadPathUnaffected() {
        when(roomReportUseCase.create(any())).thenReturn(1L);
        when(roomReportUseCase.list()).thenReturn(java.util.List.of());

        for (int i = 0; i < 3; i++) {
            given().contentType("application/json").body(BODY)
                    .when().post("/room-reports")
                    .then().statusCode(201);
        }

        given().contentType("application/json").body(BODY)
                .when().post("/room-reports")
                .then()
                .statusCode(429)
                .contentType("application/problem+json")
                .body("title", is("Too Many Requests"))
                .body("status", is(429))
                .body("instance", endsWith("/room-reports"));

        for (int i = 0; i < 10; i++) {
            given().auth().oauth2(JwtTestFixture.reportsToken())
                    .when().get("/room-reports")
                    .then().statusCode(200);
        }
    }

    @Test
    void shouldReturn429ForBurstOfResolves() {
        when(roomReportUseCase.resolve(anyLong(), anyBoolean(), anyString())).thenReturn(1L);

        for (int i = 0; i < 2; i++) {
            given().auth().oauth2(JwtTestFixture.reportsToken())
                    .contentType("application/json").body("{\"resolved\": true}")
                    .when().patch("/room-reports/1")
                    .then().statusCode(200);
        }

        given().auth().oauth2(JwtTestFixture.reportsToken())
                .contentType("application/json").body("{\"resolved\": true}")
                .when().patch("/room-reports/1")
                .then().statusCode(429);
    }
}