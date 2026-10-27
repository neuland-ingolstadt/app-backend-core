package app.neuland.producer;

import app.neuland.implementation.thinews.ThiNewsService;
import app.neuland.ports.inbound.ThiNewsUseCase;
import app.neuland.ports.outbound.ThiNewsClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ThiNewsProducer {

    @Produces
    @ApplicationScoped
    public ThiNewsUseCase thiNewsUseCase(ThiNewsClient thiNewsClient) {
        return new ThiNewsService(thiNewsClient);
    }
}
