package app.neuland.thinews;

import app.neuland.model.thinews.ThiNewsItem;
import app.neuland.ports.inbound.ThiNewsUseCase;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.when;

@QuarkusTest
class ThiNewsResourceTest {

    @InjectMock
    ThiNewsUseCase thiNewsUseCase;

    @Test
    void shouldReturnList() {
        when(thiNewsUseCase.list()).thenReturn(List.of(
                new ThiNewsItem(
                        "Körpergefühl und Kopfsache",
                        "Yoga, Pilates und eine Auszeit vom Hochschulalltag…",
                        Instant.parse("2026-10-14T12:10:17Z"),
                        "https://www.thi.de/image.webp",
                        "https://www.thi.de/hochschule/aktuelles/news/koerpergefuehl-und-kopfsache/"
                ),
                new ThiNewsItem(
                        "Without image",
                        "Description only",
                        Instant.parse("2026-10-01T08:00:00Z"),
                        null,
                        "https://www.thi.de/news/without-image/"
                )
        ));

        given()
                .when().get("/thi/news")
                .then()
                .statusCode(200)
                .body("news.size()", is(2))
                .body("news[0].title", is("Körpergefühl und Kopfsache"))
                .body("news[0].description", is("Yoga, Pilates und eine Auszeit vom Hochschulalltag…"))
                .body("news[0].date", is("2026-10-14T12:10:17Z"))
                .body("news[0].imageUrl", is("https://www.thi.de/image.webp"))
                .body(
                        "news[0].url",
                        is("https://www.thi.de/hochschule/aktuelles/news/koerpergefuehl-und-kopfsache/")
                )
                .body("news[1].title", is("Without image"))
                .body("news[1].imageUrl", nullValue());
    }

    @Test
    void shouldReturnEmptyList() {
        when(thiNewsUseCase.list()).thenReturn(List.of());

        given()
                .when().get("/thi/news")
                .then()
                .statusCode(200)
                .body("news.size()", is(0));
    }
}
