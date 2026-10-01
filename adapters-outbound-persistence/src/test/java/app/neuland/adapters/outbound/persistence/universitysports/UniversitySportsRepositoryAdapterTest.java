package app.neuland.adapters.outbound.persistence.universitysports;

import app.neuland.model.shared.Language;
import app.neuland.model.universitysports.Campus;
import app.neuland.model.universitysports.Sports;
import app.neuland.model.universitysports.SportsCategory;
import app.neuland.model.universitysports.SportsContent;
import app.neuland.model.universitysports.Weekday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniversitySportsRepositoryAdapterTest {

    @Mock
    UniversitySportsPanacheRepository repository;

    private UniversitySportsRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UniversitySportsRepositoryAdapter(repository);
    }

    @Test
    void shouldFindById() {
        when(repository.findById(1L)).thenReturn(entity(1L));

        Optional<Sports> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Titel", result.get().contents().get(Language.DE).title());
        assertEquals(Campus.INGOLSTADT, result.get().campus());
    }

    @Test
    void shouldFindAll() {
        when(repository.listAll()).thenReturn(List.of(entity(2L)));

        assertEquals(1, adapter.findAll().size());
    }

    @Test
    void shouldPersistNewSport() {
        doAnswer(invocation -> {
            UniversitySportsEntity entity = invocation.getArgument(0);
            entity.id = 20L;
            return null;
        }).when(repository).persist(any(UniversitySportsEntity.class));

        Sports saved = adapter.save(domain(null));

        assertEquals(20L, saved.id());
        verify(repository).persist(any(UniversitySportsEntity.class));
    }

    @Test
    void shouldUpdateExistingSport() {
        UniversitySportsEntity existing = entity(5L);
        when(repository.findById(5L)).thenReturn(existing);

        Sports saved = adapter.save(new Sports(
                5L,
                Map.of(
                        Language.DE, new SportsContent("Neu", "Neu DE"),
                        Language.EN, new SportsContent("New", "New EN")
                ),
                Campus.NEUBURG,
                "Halle 2",
                Weekday.FRIDAY,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                false,
                null,
                null,
                SportsCategory.TENNIS
        ));

        assertEquals(5L, saved.id());
        assertEquals(Campus.NEUBURG, saved.campus());
        assertEquals(SportsCategory.TENNIS, saved.sportsCategory());
        assertEquals("Neu", existing.contents.get(Language.DE).title);
    }

    @Test
    void shouldThrowWhenUpdatingMissingSport() {
        when(repository.findById(99L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> adapter.save(domain(99L)));
    }

    @Test
    void shouldRequireGermanContent() {
        Sports missingDe = new Sports(
                null,
                Map.of(Language.EN, new SportsContent("Title", "Description")),
                Campus.INGOLSTADT,
                "Gym",
                Weekday.MONDAY,
                LocalTime.of(18, 0),
                null,
                false,
                null,
                null,
                SportsCategory.BADMINTON
        );

        assertThrows(IllegalArgumentException.class, () -> adapter.save(missingDe));
    }

    @Test
    void shouldRequireEnglishContent() {
        Sports missingEn = new Sports(
                null,
                Map.of(Language.DE, new SportsContent("Titel", "Beschreibung")),
                Campus.INGOLSTADT,
                "Gym",
                Weekday.MONDAY,
                LocalTime.of(18, 0),
                null,
                false,
                null,
                null,
                SportsCategory.BOULDERING
        );

        assertThrows(IllegalArgumentException.class, () -> adapter.save(missingEn));
    }

    @Test
    void shouldRequireContentsMap() {
        Sports missingContents = new Sports(
                null,
                null,
                Campus.INGOLSTADT,
                "Gym",
                Weekday.MONDAY,
                LocalTime.of(18, 0),
                null,
                false,
                null,
                null,
                SportsCategory.MARTIAL_ARTS
        );

        assertThrows(IllegalArgumentException.class, () -> adapter.save(missingContents));
    }

    @Test
    void shouldDeleteById() {
        adapter.deleteById(8L);

        verify(repository).deleteById(8L);
    }

    private static Sports domain(Long id) {
        return new Sports(
                id,
                Map.of(
                        Language.DE, new SportsContent("Titel", "Beschreibung"),
                        Language.EN, new SportsContent("Title", "Description")
                ),
                Campus.INGOLSTADT,
                "Sporthalle",
                Weekday.MONDAY,
                LocalTime.of(17, 0),
                LocalTime.of(18, 30),
                true,
                "https://invite.example",
                "sports@example.com",
                SportsCategory.CALISTHENICS
        );
    }

    private static UniversitySportsEntity entity(Long id) {
        UniversitySportsEntity entity = new UniversitySportsEntity();
        entity.id = id;
        UniversitySportsContentEmbeddable de = new UniversitySportsContentEmbeddable();
        de.title = "Titel";
        de.description = "Beschreibung";
        UniversitySportsContentEmbeddable en = new UniversitySportsContentEmbeddable();
        en.title = "Title";
        en.description = "Description";
        entity.contents = new HashMap<>();
        entity.contents.put(Language.DE, de);
        entity.contents.put(Language.EN, en);
        entity.campus = Campus.INGOLSTADT;
        entity.location = "Sporthalle";
        entity.weekday = Weekday.MONDAY;
        entity.startTime = LocalTime.of(17, 0);
        entity.endTime = LocalTime.of(18, 30);
        entity.requiresRegistration = true;
        entity.invitationLink = "https://invite.example";
        entity.email = "sports@example.com";
        entity.sportsCategory = SportsCategory.CALISTHENICS;
        return entity;
    }
}
