package app.neuland.ports.outbound;

import app.neuland.model.announcement.Announcement;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AnnouncementRepository {

    Optional<Announcement> findById(long id);

    List<Announcement> findAll();

    List<Announcement> findActiveAt(Instant at);

    Announcement save(Announcement announcement);

    void deleteById(long id);
}
