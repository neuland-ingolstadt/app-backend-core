package app.neuland.adapters.outbound.http.thinews;

import app.neuland.model.thinews.ThiNewsItem;
import com.sun.net.httpserver.HttpServer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(Instant.parse("2026-10-14T12:10:17Z"), item.date());
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
    void shouldSkipArticlesWithoutLink() {
        Document document = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-01T10:00:00+02:00">01.10.2026</time>
                  <span itemprop="headline">No link</span>
                </article>
                """,
                "https://www.thi.de"
        );

        assertEquals(List.of(), adapter.parse(document));
    }

    @Test
    void shouldSkipArticlesWithBlankDatetimeAttribute() {
        Document document = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="   ">01.10.2026</time>
                  <a class="c-news-teaser__link" title="Blank date" href="/news/blank-date/">
                    <span itemprop="headline">Blank date</span>
                  </a>
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
    void shouldTreatWhitespaceOnlyDescriptionAsEmpty() {
        Document document = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-01T10:00:00+02:00">01.10.2026</time>
                  <a class="c-news-teaser__link" title="Whitespace description" href="/news/ws/">
                    <span itemprop="headline">Whitespace description</span>
                  </a>
                  <p class="c-news-teaser__text">   </p>
                </article>
                """,
                "https://www.thi.de"
        );

        assertEquals("", adapter.parse(document).getFirst().description());
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

    @Test
    void shouldResolveAbsoluteAndPathRelativeHrefs() {
        Document document = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-01T10:00:00+02:00">01.10.2026</time>
                  <a class="c-news-teaser__link" title="Absolute"
                     href="http://cdn.example/news/abs/">
                    <span itemprop="headline">Absolute</span>
                  </a>
                </article>
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-02T10:00:00+02:00">02.10.2026</time>
                  <a class="c-news-teaser__link" title="Https absolute"
                     href="https://cdn.example/news/https/">
                    <span itemprop="headline">Https absolute</span>
                  </a>
                </article>
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-03T10:00:00+02:00">03.10.2026</time>
                  <a class="c-news-teaser__link" title="Relative path" href="news/rel/">
                    <span itemprop="headline">Relative path</span>
                  </a>
                </article>
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-04T10:00:00+02:00">04.10.2026</time>
                  <a class="c-news-teaser__link" title="Blank href" href="  ">
                    <span itemprop="headline">Blank href</span>
                  </a>
                </article>
                """,
                "https://www.thi.de"
        );

        List<ThiNewsItem> news = adapter.parse(document);
        assertEquals(4, news.size());
        assertEquals("http://cdn.example/news/abs/", news.get(0).url());
        assertEquals("https://cdn.example/news/https/", news.get(1).url());
        assertEquals("https://www.thi.de/news/rel/", news.get(2).url());
        assertNull(news.get(3).url());
    }

    @Test
    void shouldFallBackToFirstSourceWhenNoDesktopMedia() {
        Document document = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-01T10:00:00+02:00">01.10.2026</time>
                  <a class="c-news-teaser__link" title="Mobile only" href="/news/mobile/">
                    <span itemprop="headline">Mobile only</span>
                  </a>
                  <picture class="c-news-teaser__media">
                    <source data-srcset="/img-400.webp 400w, /img-1100.webp 1100w" />
                    <source data-srcset="/img-square-400.webp 400w" />
                  </picture>
                </article>
                """,
                "https://www.thi.de"
        );

        assertEquals("https://www.thi.de/img-1100.webp", adapter.parse(document).getFirst().imageUrl());
    }

    @Test
    void shouldIgnoreBlankOrUnmatchedSrcset() {
        Document document = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-01T10:00:00+02:00">01.10.2026</time>
                  <a class="c-news-teaser__link" title="Blank srcset" href="/news/blank-srcset/">
                    <span itemprop="headline">Blank srcset</span>
                  </a>
                  <picture class="c-news-teaser__media">
                    <source data-srcset="   " media="(min-width: 768px)" />
                  </picture>
                </article>
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-02T10:00:00+02:00">02.10.2026</time>
                  <a class="c-news-teaser__link" title="Bad srcset" href="/news/bad-srcset/">
                    <span itemprop="headline">Bad srcset</span>
                  </a>
                  <picture class="c-news-teaser__media">
                    <source data-srcset="/img.webp, , /img.webp abcw, /img.webp w, /img.webp 12x" media="(min-width: 768px)" />
                  </picture>
                </article>
                """,
                "https://www.thi.de"
        );

        List<ThiNewsItem> news = adapter.parse(document);
        assertEquals(2, news.size());
        assertNull(news.get(0).imageUrl());
        assertNull(news.get(1).imageUrl());
    }

    @Test
    void shouldFallBackToLinkTitleAndLinkTextWhenHeadlineMissing() {
        Document withTitleAttr = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-01T10:00:00+02:00">01.10.2026</time>
                  <a class="c-news-teaser__link" title="From title attr" href="/news/title-attr/"></a>
                </article>
                """,
                "https://www.thi.de"
        );
        Document withLinkText = Jsoup.parse(
                """
                <article class="c-news-teaser">
                  <time itemprop="datePublished" datetime="2026-10-01T10:00:00+02:00">01.10.2026</time>
                  <a class="c-news-teaser__link" href="/news/link-text/">From link text</a>
                </article>
                """,
                "https://www.thi.de"
        );

        assertEquals("From title attr", adapter.parse(withTitleAttr).getFirst().title());
        assertEquals("From link text", adapter.parse(withLinkText).getFirst().title());
    }

    @Test
    void shouldRejectNonAbsolutePressUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ThiNewsClientAdapter("/relative/presse/", 1000)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new ThiNewsClientAdapter("mailto:news@thi.de", 1000)
        );
    }

    @Test
    void shouldCoverSrcsetAndUrlHelpersDirectly() {
        assertTrue(adapter.pickSrcsetUrl(null).isEmpty());
        assertTrue(adapter.pickSrcsetUrl("   ").isEmpty());
        assertEquals("/img-1100.webp", adapter.pickSrcsetUrl("/img-400.webp 400w, /img-1100.webp 1100w").orElseThrow());

        assertTrue(ThiNewsClientAdapter.parseSrcsetEntry("").isEmpty());
        assertTrue(ThiNewsClientAdapter.parseSrcsetEntry("noseparator").isEmpty());
        assertTrue(ThiNewsClientAdapter.parseSrcsetEntry("url ").isEmpty());
        assertTrue(ThiNewsClientAdapter.parseSrcsetEntry("  1100w").isEmpty());
        assertTrue(ThiNewsClientAdapter.parseSrcsetEntry("/img.webp w").isEmpty());
        assertTrue(ThiNewsClientAdapter.parseSrcsetEntry("/img.webp 12x").isEmpty());
        assertTrue(ThiNewsClientAdapter.parseSrcsetEntry("/img.webp abcw").isEmpty());
        assertEquals(
                new ThiNewsClientAdapter.SrcsetCandidate("/img.webp", 1100),
                ThiNewsClientAdapter.parseSrcsetEntry("/img.webp 1100w").orElseThrow()
        );

        assertNull(adapter.toAbsoluteUrl(null));
        assertNull(adapter.toAbsoluteUrl("  "));
        assertEquals("https://cdn.example/a", adapter.toAbsoluteUrl("https://cdn.example/a"));
        assertEquals("http://cdn.example/a", adapter.toAbsoluteUrl("http://cdn.example/a"));
        assertEquals("https://www.thi.de/a", adapter.toAbsoluteUrl("/a"));
        assertEquals("https://www.thi.de/a", adapter.toAbsoluteUrl("a"));

        assertNull(ThiNewsClientAdapter.textOrNull(null));
        assertNull(ThiNewsClientAdapter.firstNonBlank(null, "  ", null));
        assertEquals("ok", ThiNewsClientAdapter.firstNonBlank(null, "  ", "ok"));
        assertEquals("https://www.thi.de", ThiNewsClientAdapter.originOf("https://www.thi.de/presse/"));
    }

    @Test
    void shouldFetchAndParseFromHttpEndpoint() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/presse/", exchange -> {
            byte[] body = SAMPLE_HTML.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();

        try {
            String pressUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/presse/";
            ThiNewsClientAdapter httpAdapter = new ThiNewsClientAdapter(pressUrl, 2000);

            List<ThiNewsItem> news = httpAdapter.fetchAll();

            assertEquals(1, news.size());
            assertEquals("Körpergefühl und Kopfsache", news.getFirst().title());
            assertTrue(news.getFirst().url().startsWith("http://127.0.0.1:"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldWrapFetchFailures() {
        ThiNewsClientAdapter unreachable = new ThiNewsClientAdapter("http://127.0.0.1:1/", 200);

        UncheckedIOException error = assertThrows(UncheckedIOException.class, unreachable::fetchAll);
        assertTrue(error.getMessage().contains("Failed to fetch THI press releases"));
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
