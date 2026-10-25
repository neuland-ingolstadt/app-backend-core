package app.neuland.adapters.outbound.http.thinews;

import app.neuland.model.thinews.ThiNewsItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ThiNewsClientAdapterTest {

    private final ThiNewsClientAdapter adapter = new ThiNewsClientAdapter(
            "https://www.thi.de/hochschule/aktuelles/presse/",
            1000
    );

    @Test
    void shouldParseNewsTeasers() {
        Document document = Jsoup.parse(SAMPLE_HTML, "https://www.thi.de");

        List<ThiNewsItem> news = adapter.parse(document);

        assertEquals(1, news.size());
        ThiNewsItem item = news.getFirst();
        assertEquals("Körpergefühl und Kopfsache", item.title());
        assertEquals(
                "Yoga, Pilates und eine Auszeit vom Hochschulalltag: Beim Campus Balance Day…",
                item.description()
        );
        assertEquals(OffsetDateTime.parse("2026-10-14T14:10:17+02:00"), item.date());
        assertEquals(
                "https://www.thi.de/fileadmin/_processed_/image-enhancer/74675/image-1100x733.webp",
                item.imageUrl()
        );
        assertEquals(
                "https://www.thi.de/hochschule/aktuelles/news/koerpergefuehl-und-kopfsache/",
                item.url()
        );
    }

    @Test
    void shouldSkipArticlesWithoutTitleOrDate() {
        Document document = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <a class="c-news-teaser__link" href="/news/missing/"></a>
                  <p class="c-news-teaser__text">Only text</p>
                </article>
                """,
                "https://www.thi.de"
        );

        assertEquals(List.of(), adapter.parse(document));
    }

    @Test
    void shouldAllowMissingImage() {
        Document document = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-01T10:00:00+02:00">01.10.2026</time>
                  <a class="c-news-teaser__link" title="No image" href="/news/no-image/">
                    <span itemprop="headline">No image</span>
                  </a>
                  <p class="c-news-teaser__text">Description without image</p>
                </article>
                """,
                "https://www.thi.de"
        );

        List<ThiNewsItem> news = adapter.parse(document);
        assertEquals(1, news.size());
        assertNull(news.getFirst().imageUrl());
        assertEquals("Description without image", news.getFirst().description());
    }

    @Test
    void shouldSkipArticlesWithInvalidDateAndKeepValidOnes() {
        Document document = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="not-a-date">broken</time>
                  <a class="c-news-teaser__link" title="Broken" href="/news/broken/">
                    <span itemprop="headline">Broken</span>
                  </a>
                </article>
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-01T10:00:00+02:00">01.10.2026</time>
                  <a class="c-news-teaser__link" title="Valid" href="/news/valid/">
                    <span itemprop="headline">Valid</span>
                  </a>
                </article>
                """,
                "https://www.thi.de"
        );

        List<ThiNewsItem> news = adapter.parse(document);
        assertEquals(1, news.size());
        assertEquals("Valid", news.getFirst().title());
        assertEquals("https://www.thi.de/news/valid/", news.getFirst().url());
    }

    @Test
    void shouldResolveRelativeUrlsAgainstPressUrlOrigin() {
        ThiNewsClientAdapter customOrigin = new ThiNewsClientAdapter(
                "https://presse.example/aktuelles/",
                1000
        );
        Document document = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-01T10:00:00+02:00">01.10.2026</time>
                  <a class="c-news-teaser__link" title="Mirror" href="/news/mirror/">
                    <span itemprop="headline">Mirror</span>
                  </a>
                </article>
                """,
                "https://presse.example"
        );

        List<ThiNewsItem> news = customOrigin.parse(document);
        assertEquals(1, news.size());
        assertEquals("https://presse.example/news/mirror/", news.getFirst().url());
    }

    private static final String SAMPLE_HTML = """
            <article class="c-news-teaser" itemtype="https://schema.org/NewsArticle" itemscope>
              <p class="c-news-teaser__roofline">
                <time itemprop="datePublished" datetime="2026-10-14T14:10:17+02:00">14.10.2026</time>
              </p>
              <div class="c-news-teaser__body" itemprop="articleBody">
                <h3 class="c-news-teaser__title" itemprop="headline">
                  <a itemprop="url" class="c-news-teaser__link" title="Körpergefühl und Kopfsache"
                     href="/hochschule/aktuelles/news/koerpergefuehl-und-kopfsache/">
                    <span itemprop="headline">Körpergefühl und Kopfsache</span>
                  </a>
                </h3>
                <p class="c-news-teaser__text" itemprop="description">
                  Yoga, Pilates und eine Auszeit vom Hochschulalltag: Beim Campus Balance Day…
                </p>
              </div>
              <picture class="c-news-teaser__media" itemprop="image">
                <source data-srcset="/fileadmin/_processed_/image-enhancer/74675/image-400x266.webp 400w,
            /fileadmin/_processed_/image-enhancer/74675/image-1100x733.webp 1100w,
            /fileadmin/_processed_/image-enhancer/74675/image-2200x1466.webp 2200w"
                        media="(min-width: 768px)" />
                <source data-srcset="/fileadmin/_processed_/image-enhancer/74675/image-400x400.webp 400w,
            /fileadmin/_processed_/image-enhancer/74675/image-1100x1100.webp 1100w"
                        />
              </picture>
            </article>
            """;
}
