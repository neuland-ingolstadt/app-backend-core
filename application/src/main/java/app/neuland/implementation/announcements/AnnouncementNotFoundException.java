package app.neuland.implementation.announcements;

public class AnnouncementNotFoundException extends RuntimeException {

    public AnnouncementNotFoundException(long id) {
        super("Announcement not found: " + id);
    }
}
