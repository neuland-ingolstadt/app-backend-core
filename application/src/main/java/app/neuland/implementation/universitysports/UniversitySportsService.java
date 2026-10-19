package app.neuland.implementation.universitysports;

import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogOperation;
import app.neuland.model.shared.Language;
import app.neuland.model.universitysports.Sports;
import app.neuland.ports.inbound.UniversitySportsUseCase;
import app.neuland.ports.outbound.UniversitySportsRepository;
import app.neuland.model.universitysports.SportsContent;
import app.neuland.ports.inbound.AuditLogUseCase;

import java.time.Instant;
import java.util.List;

public class UniversitySportsService implements UniversitySportsUseCase {

    private final UniversitySportsRepository universitySportsRepository;
    private final AuditLogUseCase auditLogUseCase;

    public UniversitySportsService(UniversitySportsRepository universitySportsRepository, AuditLogUseCase auditLogUseCase) {
        this.universitySportsRepository = universitySportsRepository;
        this.auditLogUseCase = auditLogUseCase;
    }

    @Override
    public List<Sports> list() {
        return universitySportsRepository.findAll();
    }

    @Override
    public Sports get(long id) {
        return universitySportsRepository
                .findById(id)
                .orElseThrow(() -> new SportsNotFoundException(id));
    }

    @Override
    public Sports create(Sports sport, String userId) {
        Sports toCreate = new Sports(
                null,
                sport.contents(),
                sport.campus(),
                sport.location(),
                sport.weekday(),
                sport.startTime(),
                sport.endTime(),
                sport.requiresRegistration(),
                sport.invitationLink(),
                sport.email(),
                sport.sportsCategory()
        );

        Sports saved = universitySportsRepository.save(toCreate);

        appendAuditLog(saved, AuditLogOperation.CREATE, userId);

        return saved;
    }

    @Override
    public Sports update(long id, Sports sport, String userId) {
        universitySportsRepository
                .findById(id)
                .orElseThrow(() -> new SportsNotFoundException(id));

        Sports updated = new Sports(
                id,
                sport.contents(),
                sport.campus(),
                sport.location(),
                sport.weekday(),
                sport.startTime(),
                sport.endTime(),
                sport.requiresRegistration(),
                sport.invitationLink(),
                sport.email(),
                sport.sportsCategory()
        );

        Sports saved = universitySportsRepository.save(updated);

        appendAuditLog(saved, AuditLogOperation.UPDATE, userId);

        return saved;
    }

    @Override
    public void delete(long id, String userId) {
        Sports existing = universitySportsRepository
                .findById(id)
                .orElseThrow(() -> new SportsNotFoundException(id));

        universitySportsRepository.deleteById(id);

        appendAuditLog(existing, AuditLogOperation.DELETE, userId);
    }

    private String getTitle(Sports sport) {
        if (sport.contents() == null) {
            return null;
        }

        SportsContent germanContent = sport.contents().get(Language.DE);
        if (germanContent != null) {
            return germanContent.title();
        }

        SportsContent englishContent = sport.contents().get(Language.EN);
        return englishContent == null ? null : englishContent.title();
    }

    private void appendAuditLog(
            Sports sport,
            AuditLogOperation operation,
            String userId) {
        auditLogUseCase.append(
                new AuditLogEntry(
                        null,
                        "Sports",
                        sport.id(),
                        operation,
                        getTitle(sport),
                        userId,
                        Instant.now()
                )
        );
    }
}
