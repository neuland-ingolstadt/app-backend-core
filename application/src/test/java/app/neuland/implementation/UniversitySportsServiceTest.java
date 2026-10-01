package app.neuland.implementation;

import app.neuland.model.shared.Language;
import app.neuland.model.universitysports.Campus;
import app.neuland.model.universitysports.Sports;
import app.neuland.model.universitysports.SportsCategory;
import app.neuland.model.universitysports.SportsContent;
import app.neuland.model.universitysports.Weekday;
import app.neuland.ports.outbound.UniversitySportsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniversitySportsServiceTest {

    @Mock
    UniversitySportsRepository universitySportsRepository;

    private UniversitySportsService service;

    @BeforeEach
    void setUp() {
        service = new UniversitySportsService(universitySportsRepository);
    }

    @Test
    void shouldList() {
        Sports sport = sample(1L, Weekday.MONDAY, SportsCategory.BASKETBALL);
        when(universitySportsRepository.findAll()).thenReturn(List.of(sport));

        assertEquals(List.of(sport), service.list());
    }

    @Test
    void shouldGet() {
        Sports sport = sample(2L, Weekday.TUESDAY, SportsCategory.FOOTBALL);
        when(universitySportsRepository.findById(2L)).thenReturn(Optional.of(sport));

        assertEquals(sport, service.get(2L));
    }

    @Test
    void shouldThrowWhenGettingMissing() {
        when(universitySportsRepository.findById(99L)).thenReturn(Optional.empty());

        SportsNotFoundException exception = assertThrows(
                SportsNotFoundException.class,
                () -> service.get(99L)
        );
        assertEquals("Sport not found: 99", exception.getMessage());
    }

    @Test
    void shouldCreateWithNullId() {
        Sports input = sample(42L, Weekday.WEDNESDAY, SportsCategory.VOLLEYBALL);
        when(universitySportsRepository.save(any())).thenAnswer(invocation -> {
            Sports sport = invocation.getArgument(0);
            return sample(7L, sport.weekday(), sport.sportsCategory());
        });

        Sports created = service.create(input);

        assertEquals(7L, created.id());
        verify(universitySportsRepository).save(argThat(sport -> sport.id() == null));
    }

    @Test
    void shouldUpdate() {
        Sports existing = sample(8L, Weekday.THURSDAY, SportsCategory.HANDBALL);
        Sports update = sample(null, Weekday.FRIDAY, SportsCategory.YOGA);
        when(universitySportsRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(universitySportsRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Sports result = service.update(8L, update);

        assertEquals(8L, result.id());
        assertEquals(Weekday.FRIDAY, result.weekday());
        assertEquals(SportsCategory.YOGA, result.sportsCategory());
    }

    @Test
    void shouldThrowWhenUpdatingMissing() {
        when(universitySportsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                SportsNotFoundException.class,
                () -> service.update(99L, sample(null, Weekday.SATURDAY, SportsCategory.OTHER))
        );
    }

    @Test
    void shouldDelete() {
        when(universitySportsRepository.findById(9L))
                .thenReturn(Optional.of(sample(9L, Weekday.SUNDAY, SportsCategory.SWIMMING)));

        service.delete(9L);

        verify(universitySportsRepository).deleteById(eq(9L));
    }

    @Test
    void shouldThrowWhenDeletingMissing() {
        when(universitySportsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SportsNotFoundException.class, () -> service.delete(99L));
    }

    @Test
    void shouldCopyNullContents() {
        Sports sport = new Sports(
                1L,
                null,
                Campus.NEUBURG,
                "Gym",
                Weekday.MONDAY,
                LocalTime.of(18, 0),
                LocalTime.of(19, 0),
                false,
                null,
                null,
                SportsCategory.DANCING
        );

        assertNull(sport.contents());
    }

    private static Sports sample(Long id, Weekday weekday, SportsCategory category) {
        return new Sports(
                id,
                Map.of(
                        Language.DE, new SportsContent("Titel", "Beschreibung"),
                        Language.EN, new SportsContent("Title", "Description")
                ),
                Campus.INGOLSTADT,
                "Sporthalle",
                weekday,
                LocalTime.of(17, 0),
                LocalTime.of(18, 30),
                true,
                "https://invite.example",
                "sports@example.com",
                category
        );
    }
}
