package app.neuland.adapters.outbound.persistence.announcements;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AnnouncementContentEmbeddable {

    @Column(nullable = false)
    public String title;

    @Column(nullable = false)
    public String description;
}
