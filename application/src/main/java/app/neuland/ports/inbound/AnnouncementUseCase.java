package app.neuland.ports.inbound;

import app.neuland.model.announcement.Announcement;

import java.util.List;

public interface AnnouncementUseCase {

    List<Announcement> list(boolean includeInactive);

    Announcement get(long id);

    Announcement create(Announcement announcement, String userId);

    Announcement update(long id, Announcement announcement, String userId);

    void delete(long id, String userId);
}
