package app.neuland.implementation.announcements;

import app.neuland.model.announcement.Announcement;
import app.neuland.ports.inbound.AnnouncementUseCase;
import app.neuland.ports.outbound.AnnouncementRepository;

import java.time.Instant;
import java.util.List;

public class AnnouncementService implements AnnouncementUseCase {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Override
    public List<Announcement> list(boolean includeInactive) {
        if (includeInactive) {
            return announcementRepository.findAll();
        }

        return announcementRepository.findActiveAt(Instant.now());
    }

    @Override
    public Announcement get(long id) {
        return announcementRepository
                .findById(id)
                .orElseThrow(() -> new AnnouncementNotFoundException(id));
    }

    @Override
    public Announcement create(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Override
    public Announcement update(long id, Announcement announcement) {
        announcementRepository
                .findById(id)
                .orElseThrow(() -> new AnnouncementNotFoundException(id));

        Announcement updated = new Announcement(
                id,
                announcement.platforms(),
                announcement.userKinds(),
                announcement.contents(),
                announcement.startDateTime(),
                announcement.endDateTime(),
                announcement.priority(),
                announcement.url(),
                announcement.imageUrl()
        );

        return announcementRepository.save(updated);
    }

    @Override
    public void delete(long id) {
        announcementRepository
                .findById(id)
                .orElseThrow(() -> new AnnouncementNotFoundException(id));

        announcementRepository.deleteById(id);
    }
}
