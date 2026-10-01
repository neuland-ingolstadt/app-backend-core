package app.neuland.implementation.announcements;

import app.neuland.model.announcement.Announcement;
import app.neuland.model.announcement.AnnouncementContent;
import app.neuland.model.announcement.Platform;
import app.neuland.model.announcement.UserKind;
import app.neuland.model.shared.Language;
import app.neuland.ports.outbound.AnnouncementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @Mock
    AnnouncementRepository announcementRepository;

    private AnnouncementService service;

    @BeforeEach
    void setUp() {
        service = new AnnouncementService(announcementRepository);
    }

    @Test
    void shouldListAllWhenInactiveIncluded() {
        Announcement announcement = sample(1L);
        when(announcementRepository.findAll()).thenReturn(List.of(announcement));

        assertEquals(List.of(announcement), service.list(true));
        verify(announcementRepository).findAll();
    }

    @Test
    void shouldListActiveOnlyByDefault() {
        Announcement announcement = sample(2L);
        when(announcementRepository.findActiveAt(any(Instant.class))).thenReturn(List.of(announcement));

        assertEquals(List.of(announcement), service.list(false));
        verify(announcementRepository).findActiveAt(any(Instant.class));
    }

    @Test
    void shouldGetById() {
        Announcement announcement = sample(3L);
        when(announcementRepository.findById(3L)).thenReturn(Optional.of(announcement));

        assertEquals(announcement, service.get(3L));
    }

    @Test
    void shouldThrowWhenGettingMissing() {
        when(announcementRepository.findById(99L)).thenReturn(Optional.empty());

        AnnouncementNotFoundException exception = assertThrows(
                AnnouncementNotFoundException.class,
                () -> service.get(99L)
        );
        assertEquals("Announcement not found: 99", exception.getMessage());
    }

    @Test
    void shouldCreate() {
        Announcement input = sample(null);
        Announcement saved = sample(4L);
        when(announcementRepository.save(input)).thenReturn(saved);

        assertEquals(saved, service.create(input));
    }

    @Test
    void shouldUpdate() {
        Announcement existing = sample(5L);
        Announcement update = new Announcement(
                null,
                Set.of(Platform.WEB, Platform.WEB_DEV),
                Set.of(UserKind.EMPLOYEE),
                Map.of(Language.EN, new AnnouncementContent("Title EN", "Body EN")),
                Instant.parse("2026-02-01T00:00:00Z"),
                Instant.parse("2026-03-01T00:00:00Z"),
                2,
                "https://example.com",
                "https://example.com/image.png"
        );
        when(announcementRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(announcementRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Announcement result = service.update(5L, update);

        assertEquals(5L, result.id());
        assertEquals(Set.of(Platform.WEB, Platform.WEB_DEV), result.platforms());
        assertEquals(Set.of(UserKind.EMPLOYEE), result.userKinds());
        assertEquals(2, result.priority());
        verify(announcementRepository).save(argThat(announcement -> announcement.id().equals(5L)));
    }

    @Test
    void shouldThrowWhenUpdatingMissing() {
        when(announcementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AnnouncementNotFoundException.class, () -> service.update(99L, sample(null)));
    }

    @Test
    void shouldDelete() {
        when(announcementRepository.findById(6L)).thenReturn(Optional.of(sample(6L)));

        service.delete(6L);

        verify(announcementRepository).deleteById(eq(6L));
    }

    @Test
    void shouldThrowWhenDeletingMissing() {
        when(announcementRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AnnouncementNotFoundException.class, () -> service.delete(99L));
    }

    private static Announcement sample(Long id) {
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
}
