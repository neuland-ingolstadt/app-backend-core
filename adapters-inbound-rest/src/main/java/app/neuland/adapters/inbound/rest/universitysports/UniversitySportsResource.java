package app.neuland.adapters.inbound.rest.universitysports;

import app.neuland.backend.core.api.v0.UniversitySportsApi;
import app.neuland.backend.core.api.v0.model.SportsCreateRequest;
import app.neuland.backend.core.api.v0.model.SportsPatchRequest;
import app.neuland.model.universitysports.Sports;
import app.neuland.ports.inbound.UniversitySportsUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

public class UniversitySportsResource implements UniversitySportsApi {

    private final UniversitySportsUseCase universitySportsUseCase;

    @Context
    UriInfo uriInfo;

    @Inject
    public UniversitySportsResource(UniversitySportsUseCase universitySportsUseCase) {
        this.universitySportsUseCase = universitySportsUseCase;
    }

    @Override
    public Response listSports() {
        return Response
                .ok(UniversitySportsMapper.toListResponse(universitySportsUseCase.list()))
                .build();
    }

    @Override
    public Response getSport(Long sportsId) {
        return Response
                .ok(UniversitySportsMapper.toResponse(universitySportsUseCase.get(sportsId)))
                .build();
    }

    @Override
    public Response createSport(SportsCreateRequest sportsCreateRequest) {
        Sports created = universitySportsUseCase.create(
                UniversitySportsMapper.toDomain(sportsCreateRequest)
        );
        return Response
                .created(uriInfo.getAbsolutePathBuilder()
                                .path(String.valueOf(created.id()))
                                .build()
                )
                .entity(UniversitySportsMapper.toResponse(created))
                .build();
    }

    @Override
    public Response updateSport(Long sportsId, SportsPatchRequest sportsPatchRequest) {
        return Response
                .ok(UniversitySportsMapper.toResponse(universitySportsUseCase.update(
                                sportsId,
                                UniversitySportsMapper.toDomain(
                                        sportsId,
                                        sportsPatchRequest
                                )
                        )
                ))
                .build();
    }

    @Override
    public Response deleteSport(Long sportsId) {
        universitySportsUseCase.delete(sportsId);
        return Response
                .noContent()
                .build();
    }
}