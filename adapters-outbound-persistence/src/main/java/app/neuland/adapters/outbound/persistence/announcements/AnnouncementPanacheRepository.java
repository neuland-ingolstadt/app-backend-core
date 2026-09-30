package app.neuland.adapters.outbound.persistence.announcements;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AnnouncementPanacheRepository
        implements PanacheRepository<AnnouncementEntity> {
}
