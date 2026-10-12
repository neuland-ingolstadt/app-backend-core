package app.neuland.adapters.inbound.rest.auditlogs;

import app.neuland.adapters.inbound.security.SecurityRoles;
import app.neuland.backend.core.api.v0.AuditLogsApi;
import app.neuland.backend.core.api.v0.model.AuditLogListResponse;
import app.neuland.ports.inbound.AuditLogUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestResponse;

public class AuditLogResource implements AuditLogsApi {

    private final AuditLogUseCase auditLogUseCase;

    @Inject
    public AuditLogResource(AuditLogUseCase auditLogUseCase) {
        this.auditLogUseCase = auditLogUseCase;
    }

    @Override
    @RolesAllowed({
            SecurityRoles.adminRole
    })
    public RestResponse<AuditLogListResponse> listAuditLogs(Long limit, String cursor) {
        return RestResponse.ok(
                AuditLogMapper.mapToAuditLogListResponse(auditLogUseCase.list(limit, cursor))
        );
    }
}
