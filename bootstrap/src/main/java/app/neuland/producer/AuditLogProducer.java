package app.neuland.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import app.neuland.ports.inbound.AuditLogUseCase;
import app.neuland.ports.outbound.AuditLogRepository;

import app.neuland.implementation.auditlogs.AuditLogService;

@ApplicationScoped
public class AuditLogProducer {
    
    @Produces
    @ApplicationScoped
    public AuditLogUseCase auditLogUseCase(AuditLogRepository auditLogRepository) {
        return new AuditLogService(auditLogRepository);
    }
}
