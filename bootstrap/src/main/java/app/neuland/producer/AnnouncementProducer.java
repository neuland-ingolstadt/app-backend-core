package app.neuland.producer;

import app.neuland.implementation.announcements.AnnouncementService;
import app.neuland.ports.inbound.AnnouncementUseCase;
import app.neuland.ports.inbound.AuditLogUseCase;
import app.neuland.ports.outbound.AnnouncementRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class AnnouncementProducer {

    @Produces
    @ApplicationScoped
    public AnnouncementUseCase announcementUseCase(
            AnnouncementRepository announcementRepository,
            AuditLogUseCase auditLogUseCase
    ) {
        return new AnnouncementService(announcementRepository, auditLogUseCase);
    }
}
