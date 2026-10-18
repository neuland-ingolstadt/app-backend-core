package app.neuland.implementation.roomreports;

import app.neuland.model.auditlog.AuditLogEntry;
import app.neuland.model.auditlog.AuditLogOperation;
import app.neuland.model.roomreport.RoomReport;
import app.neuland.ports.inbound.AuditLogUseCase;
import app.neuland.ports.inbound.RoomReportUseCase;
import app.neuland.ports.outbound.RoomReportRepository;

import java.time.Instant;
import java.util.List;

public class RoomReportService implements RoomReportUseCase {

    private final RoomReportRepository roomReportRepository;
    private final AuditLogUseCase auditLogUseCase;

    public RoomReportService(RoomReportRepository roomReportRepository, AuditLogUseCase auditLogUseCase) {
        this.roomReportRepository = roomReportRepository;
        this.auditLogUseCase = auditLogUseCase;
    }

    @Override
    public List<RoomReport> list() {
        return roomReportRepository.findAll();
    }

    @Override
    public Long create(RoomReport report) {
        RoomReport toCreate = new RoomReport(
                null,
                report.room(),
                report.reason(),
                report.description(),
                null
        );
        return roomReportRepository.save(toCreate).id();
    }

    @Override
    public Long resolve(long id, boolean resolved, String userId) {
        RoomReport existing = roomReportRepository
                .findById(id)
                .orElseThrow(() -> new RoomReportNotFoundException(id));

        Instant resolvedAt = resolved
                ? Instant.now()
                : null;

        RoomReport updated = new RoomReport(
                existing.id(),
                existing.room(),
                existing.reason(),
                existing.description(),
                resolvedAt
        );

        RoomReport saved = roomReportRepository.save(updated);

        appendAuditLog(saved, AuditLogOperation.UPDATE, userId);

        return saved.id();
    }

    private void appendAuditLog(
            RoomReport roomReport,
            AuditLogOperation operation,
            String userId) {
        auditLogUseCase.append(
                new AuditLogEntry(
                        null,
                        "RoomReport",
                        roomReport.id(),
                        operation,
                        roomReport.room(),
                        userId,
                        Instant.now()
                )
        );
    }
}
