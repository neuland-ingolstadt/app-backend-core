package app.neuland.adapters.inbound.rest.thinews;

import app.neuland.backend.core.api.v0.ThiNewsApi;
import app.neuland.backend.core.api.v0.model.ThiNewsListResponse;
import app.neuland.ports.inbound.ThiNewsUseCase;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestResponse;

public class ThiNewsResource implements ThiNewsApi {

    private final ThiNewsUseCase thiNewsUseCase;

    @Inject
    public ThiNewsResource(ThiNewsUseCase thiNewsUseCase) {
        this.thiNewsUseCase = thiNewsUseCase;
    }

    @Override
    @PermitAll
    public RestResponse<ThiNewsListResponse> listThiNews() {
        return RestResponse.ok(ThiNewsMapper.toListResponse(thiNewsUseCase.list()));
    }
}
