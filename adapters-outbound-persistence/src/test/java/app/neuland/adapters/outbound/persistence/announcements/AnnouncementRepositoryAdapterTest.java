package app.neuland.adapters.outbound.persistence.announcements;

import app.neuland.model.announcement.Announcement;
import app.neuland.model.announcement.AnnouncementContent;
import app.neuland.model.announcement.Platform;
import app.neuland.model.announcement.UserKind;
import app.neuland.model.shared.Language;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementRepositoryAdapterTest {

    @Mock
    AnnouncementPanacheRepository repository;

    private AnnouncementRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AnnouncementRepositoryAdapter(repository);
    }

    @Test
    void shouldFindById() {
        when(repository.findById(1L)).thenReturn(entity(1L));

        Optional<Announcement> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals("Titel", result.get().contents().get(Language.DE).title());
    }

    @Test
    void shouldFindAll() {
        when(repository.listAll()).thenReturn(List.of(entity(2L)));

        assertEquals(1, adapter.findAll().size());
    }

    @Test
    void shouldFindActiveAt() {
        @SuppressWarnings("unchecked")
        PanacheQuery<AnnouncementEntity> query = mock(PanacheQuery.class);
        when(repository.find(anyString(), any(Instant.class))).thenReturn(query);
        when(query.list()).thenReturn(List.of(entity(3L)));

        List<Announcement> active = adapter.findActiveAt(Instant.parse("2026-06-01T00:00:00Z"));

        assertEquals(1, active.size());
        assertEquals(3L, active.getFirst().id());
    }

    @Test
    void shouldPersistNewAnnouncement() {
        doAnswer(invocation -> {
            AnnouncementEntity entity = invocation.getArgument(0);
            entity.id = 10L;
            return null;
        }).when(repository).persist(any(AnnouncementEntity.class));

        Announcement saved = adapter.save(domain(null));

        assertEquals(10L, saved.id());
        assertEquals(Set.of(Platform.ANDROID, Platform.IOS), saved.platforms());
        verify(repository).persist(any(AnnouncementEntity.class));
    }

    @Test
    void shouldUpdateExistingAnnouncement() {
        AnnouncementEntity existing = entity(5L);
        when(repository.findById(5L)).thenReturn(existing);

        Announcement saved = adapter.save(new Announcement(
                5L,
                Set.of(Platform.WEB),
                Set.of(UserKind.EMPLOYEE),
                Map.of(Language.EN, new AnnouncementContent("EN", "Body")),
                Instant.parse("2026-02-01T00:00:00Z"),
                Instant.parse("2026-03-01T00:00:00Z"),
                9,
                "https://example.com",
                "https://example.com/img.png"
        ));

        assertEquals(5L, saved.id());
        assertEquals(Set.of(Platform.WEB), saved.platforms());
        assertEquals(9, saved.priority());
        assertEquals("EN", existing.contents.get(Language.EN).title);
    }

    @Test
    void shouldThrowWhenUpdatingMissingAnnouncement() {
        when(repository.findById(99L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> adapter.save(domain(99L)));
    }

    @Test
    void shouldDelete() {
        AnnouncementEntity existing = entity(6L);
        when(repository.findById(6L)).thenReturn(existing);

        adapter.deleteById(6L);

        verify(repository).delete(existing);
    }

    @Test
    void shouldThrowWhenDeletingMissingAnnouncement() {
        when(repository.findById(99L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> adapter.deleteById(99L));
    }

    private static Announcement domain(Long id) {
        return new Announcement(
                id,
                EnumSet.of(Platform.ANDROID, Platform.IOS),
                EnumSet.of(UserKind.STUDENT, UserKind.GUEST),
                Map.of(Language.DE, new AnnouncementContent("Titel", "Beschreibung")),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-12-31T23:59:59Z"),
                1,
                null,
                null
        );
    }

    private static AnnouncementEntity entity(Long id) {
        AnnouncementEntity entity = new AnnouncementEntity();
        entity.id = id;
        entity.platforms = new HashSet<>(EnumSet.of(Platform.ANDROID, Platform.IOS));
        entity.userKinds = new HashSet<>(EnumSet.of(UserKind.STUDENT, UserKind.GUEST));
        AnnouncementContentEmbeddable content = new AnnouncementContentEmbeddable();
        content.title = "Titel";
        content.description = "Beschreibung";
        entity.contents = new HashMap<>();
        entity.contents.put(Language.DE, content);
        entity.startDateTime = Instant.parse("2026-01-01T00:00:00Z");
        entity.endDateTime = Instant.parse("2026-12-31T23:59:59Z");
        entity.priority = 1;
        return entity;
    }
}
