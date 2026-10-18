package app.neuland.implementation.announcements;

import app.neuland.model.announcement.Announcement;
import app.neuland.model.announcement.AnnouncementContent;
import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogOperation;
import app.neuland.ports.inbound.AnnouncementUseCase;
import app.neuland.ports.inbound.AuditLogUseCase;
import app.neuland.ports.outbound.AnnouncementRepository;

import app.neuland.model.shared.Language;

import java.time.Instant;
import java.util.List;

public class AnnouncementService implements AnnouncementUseCase {

    private final AnnouncementRepository announcementRepository;

    private final AuditLogUseCase auditLogUseCase;

    public AnnouncementService(AnnouncementRepository announcementRepository, AuditLogUseCase auditLogUseCase) {
        this.announcementRepository = announcementRepository;
        this.auditLogUseCase = auditLogUseCase;
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
    public Announcement create(Announcement announcement, String userId) {

        Announcement saved = announcementRepository.save(announcement);

        appendAuditLog(saved, AuditLogOperation.CREATE, userId);

        return saved;
    }

    @Override
    public Announcement update(long id, Announcement announcement, String userId) {
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

        Announcement saved = announcementRepository.save(updated);

        appendAuditLog(saved, AuditLogOperation.UPDATE, userId);

        return saved;
    }

    @Override
    public void delete(long id, String userId) {
        Announcement existing = announcementRepository
                .findById(id)
                .orElseThrow(() -> new AnnouncementNotFoundException(id));

        announcementRepository.deleteById(id);

        appendAuditLog(existing, AuditLogOperation.DELETE, userId);
    }

    private String getTitle(Announcement announcement) {
        if (announcement.contents() == null) {
            return null;
        }

        AnnouncementContent germanContent = announcement.contents().get(Language.DE);
        if (germanContent != null) {
            return germanContent.title();
        }

        AnnouncementContent englishContent = announcement.contents().get(Language.EN);
        return englishContent == null ? null : englishContent.title();
    }

    private void appendAuditLog(
            Announcement announcement,
            AuditLogOperation operation,
            String userId)
{
        auditLogUseCase.append(
                new AuditLogEntry(
                        null,
                        "Announcement",
                        announcement.id(),
                        operation,
                        getTitle(announcement),
                        userId,
                        Instant.now()
                )
        );
    }
}
