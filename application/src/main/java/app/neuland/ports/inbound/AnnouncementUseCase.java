package app.neuland.ports.inbound;

import app.neuland.model.announcement.Announcement;

import java.util.List;

public interface AnnouncementUseCase {

    List<Announcement> list(boolean includeInactive);

    Announcement get(long id);

    Announcement create(Announcement announcement);

    Announcement update(long id, Announcement announcement);

    void delete(long id);
}
