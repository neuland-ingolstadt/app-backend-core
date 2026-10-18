package app.neuland.adapters.inbound.rest.universitysports;

import app.neuland.adapters.inbound.security.SecurityRoles;
import app.neuland.backend.core.api.v0.UniversitySportsApi;
import app.neuland.backend.core.api.v0.model.Sports;
import app.neuland.backend.core.api.v0.model.SportsCreateRequest;
import app.neuland.backend.core.api.v0.model.SportsListResponse;
import app.neuland.backend.core.api.v0.model.SportsPatchRequest;
import app.neuland.ports.inbound.UniversitySportsUseCase;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.RestResponse;
import io.quarkus.security.identity.SecurityIdentity;

public class UniversitySportsResource implements UniversitySportsApi {

    private final UniversitySportsUseCase universitySportsUseCase;
    private final SecurityIdentity securityIdentity;

    @Context
    UriInfo uriInfo;

    @Inject
    public UniversitySportsResource(UniversitySportsUseCase universitySportsUseCase, SecurityIdentity securityIdentity) {
        this.universitySportsUseCase = universitySportsUseCase;
        this.securityIdentity = securityIdentity;
    }

    @Override
    @PermitAll
    public RestResponse<SportsListResponse> listSports() {
        return RestResponse.ok(
                UniversitySportsMapper.toListResponse(universitySportsUseCase.list())
        );
    }

    @Override
    @PermitAll
    public RestResponse<Sports> getSport(Long sportsId) {
        return RestResponse.ok(
                UniversitySportsMapper.toResponse(universitySportsUseCase.get(sportsId))
        );
    }

    @Override
    @RolesAllowed({
            SecurityRoles.sportRole,
            SecurityRoles.adminRole
    })
    @Transactional
    public RestResponse<Sports> createSport(SportsCreateRequest sportsCreateRequest) {
        app.neuland.model.universitysports.Sports created = universitySportsUseCase.create(
                UniversitySportsMapper.toDomain(sportsCreateRequest),
                securityIdentity.getPrincipal().getName()
        );
        return RestResponse.ResponseBuilder
                .create(
                        Response.Status.CREATED,
                        UniversitySportsMapper.toResponse(created)
                )
                .location(uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(created.id()))
                        .build())
                .build();
    }

    @Override
    @RolesAllowed({
            SecurityRoles.sportRole,
            SecurityRoles.adminRole
    })
    @Transactional
    public RestResponse<Sports> updateSport(Long sportsId, SportsPatchRequest sportsPatchRequest) {
        return RestResponse.ok(
                UniversitySportsMapper.toResponse(universitySportsUseCase.update(
                        sportsId,
                        UniversitySportsMapper.toDomain(sportsId, sportsPatchRequest),
                        securityIdentity.getPrincipal().getName()
                ))
        );
    }

    @Override
    @RolesAllowed({
            SecurityRoles.sportRole,
            SecurityRoles.adminRole
    })
    @Transactional
    public RestResponse<Void> deleteSport(Long sportsId) {
        universitySportsUseCase.delete(sportsId, securityIdentity.getPrincipal().getName());
        return RestResponse.noContent();
    }
}
