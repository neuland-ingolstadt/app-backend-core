package app.neuland.producer;

import app.neuland.implementation.roomreports.RoomReportService;
import app.neuland.ports.inbound.RoomReportUseCase;
import app.neuland.ports.outbound.RoomReportRepository;
import app.neuland.ports.inbound.AuditLogUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class RoomReportProducer {

    @Produces
    @ApplicationScoped
    public RoomReportUseCase roomReportUseCase(
            RoomReportRepository roomReportRepository,
            AuditLogUseCase auditLogUseCase
    ) {
        return new RoomReportService(roomReportRepository, auditLogUseCase);
    }
}
