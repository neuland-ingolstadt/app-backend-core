package app.neuland.adapters.inbound.rest.auditlogs;

import app.neuland.backend.core.api.v0.AuditLogsApi;
import app.neuland.ports.inbound.AuditLogUseCase;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

public class AuditLogResource implements AuditLogsApi {

    private final AuditLogUseCase auditLogUseCase;

    @Inject
    public AuditLogResource(AuditLogUseCase auditLogUseCase) {
        this.auditLogUseCase = auditLogUseCase;
    }

    @Override
    public Response listAuditLogs(Long limit, String cursor) {
        return Response
            .ok(AuditLogMapper.mapToAuditLogListResponse(auditLogUseCase.list(limit, cursor)))
            .build();
    }
}
