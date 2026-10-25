package app.neuland.adapters.outbound.http.thinews;

import app.neuland.model.thinews.ThiNewsItem;
import app.neuland.ports.outbound.ThiNewsClient;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ThiNewsClientAdapter implements ThiNewsClient {

    private static final int PREFERRED_IMAGE_WIDTH = 1100;

    private final String pressUrl;
    private final String baseUrl;
    private final int timeoutMillis;

    public ThiNewsClientAdapter(
            @ConfigProperty(name = "thi.news.url") String pressUrl,
            @ConfigProperty(name = "thi.news.timeout-ms") int timeoutMillis
    ) {
        this.pressUrl = pressUrl;
        this.baseUrl = originOf(pressUrl);
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    @CacheResult(cacheName = "thi-news")
    public List<ThiNewsItem> fetchAll() {
        try {
            Document document = Jsoup.connect(pressUrl)
                    .userAgent("NeulandAppBackend/1.0 (+https://neuland.app)")
                    .timeout(timeoutMillis)
                    .get();
            return parse(document);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to fetch THI press releases from " + pressUrl, e);
        }
    }

    List<ThiNewsItem> parse(Document document) {
        Elements articles = document.select("article.c-news-teaser");
        List<ThiNewsItem> items = new ArrayList<>(articles.size());
        for (Element article : articles) {
            parseArticle(article).ifPresent(items::add);
        }
        return List.copyOf(items);
    }

    private Optional<ThiNewsItem> parseArticle(Element article) {
        Element link = article.selectFirst("a.c-news-teaser__link");
        if (link == null) {
            return Optional.empty();
        }

        String title = firstNonBlank(
                textOrNull(article.selectFirst("span[itemprop=headline]")),
                link.attr("title"),
                link.text()
        );
        if (title == null) {
            return Optional.empty();
        }

        String description = Optional.ofNullable(article.selectFirst("p.c-news-teaser__text"))
                .map(Element::text)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .orElse("");

        Instant date = parseDate(article).orElse(null);
        if (date == null) {
            return Optional.empty();
        }

        String absoluteUrl = toAbsoluteUrl(link.attr("href"));
        String imageUrl = extractImageUrl(article);

        return Optional.of(new ThiNewsItem(title.trim(), description, date, imageUrl, absoluteUrl));
    }

    private Optional<Instant> parseDate(Element article) {
        String datetime = Optional.ofNullable(article.selectFirst("time[itemprop=datePublished]"))
                .map(time -> time.attr("datetime"))
                .filter(value -> !value.isBlank())
                .orElse(null);
        if (datetime == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(OffsetDateTime.parse(datetime).toInstant());
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    private String extractImageUrl(Element article) {
        Elements sources = article.select("picture.c-news-teaser__media source[data-srcset]");
        Element preferred = null;
        for (Element source : sources) {
            String media = source.attr("media");
            if (media.contains("768")) {
                preferred = source;
                break;
            }
            if (preferred == null) {
                preferred = source;
            }
        }
        if (preferred == null) {
            return null;
        }

        return pickSrcsetUrl(preferred.attr("data-srcset"))
                .map(this::toAbsoluteUrl)
                .orElse(null);
    }

    Optional<String> pickSrcsetUrl(String srcset) {
        if (srcset == null || srcset.isBlank()) {
            return Optional.empty();
        }

        List<SrcsetCandidate> candidates = new ArrayList<>();
        for (String part : srcset.split(",")) {
            parseSrcsetEntry(part.trim()).ifPresent(candidates::add);
        }
        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        return candidates.stream()
                .min(Comparator.comparingInt(c -> Math.abs(c.width() - PREFERRED_IMAGE_WIDTH)))
                .map(SrcsetCandidate::url);
    }

    static Optional<SrcsetCandidate> parseSrcsetEntry(String entry) {
        if (entry.isEmpty()) {
            return Optional.empty();
        }

        int separator = entry.lastIndexOf(' ');
        if (separator <= 0 || separator == entry.length() - 1) {
            return Optional.empty();
        }

        String url = entry.substring(0, separator).trim();
        String descriptor = entry.substring(separator + 1).trim();
        if (url.isEmpty() || descriptor.length() < 2 || descriptor.charAt(descriptor.length() - 1) != 'w') {
            return Optional.empty();
        }

        try {
            int width = Integer.parseInt(descriptor, 0, descriptor.length() - 1, 10);
            return Optional.of(new SrcsetCandidate(url, width));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    String toAbsoluteUrl(String href) {
        if (href == null || href.isBlank()) {
            return null;
        }
        if (href.startsWith("http://") || href.startsWith("https://")) {
            return href;
        }
        if (href.startsWith("/")) {
            return baseUrl + href;
        }
        return baseUrl + "/" + href;
    }

    static String originOf(String url) {
        URI uri = URI.create(url);
        if (uri.getScheme() == null || uri.getAuthority() == null) {
            throw new IllegalArgumentException("thi.news.url must be an absolute http(s) URL: " + url);
        }
        return uri.getScheme() + "://" + uri.getAuthority();
    }

    static String textOrNull(Element element) {
        return element == null ? null : element.text();
    }

    static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    record SrcsetCandidate(String url, int width) {}
}
