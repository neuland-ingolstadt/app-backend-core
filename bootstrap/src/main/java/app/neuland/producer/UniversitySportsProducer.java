package app.neuland.producer;

import app.neuland.implementation.universitysports.UniversitySportsService;
import app.neuland.ports.inbound.UniversitySportsUseCase;
import app.neuland.ports.outbound.UniversitySportsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class UniversitySportsProducer {

    @Produces
    @ApplicationScoped
    public UniversitySportsUseCase universitySportsUseCase(
            UniversitySportsRepository universitySportsRepository
    ) {
        return new UniversitySportsService(universitySportsRepository);
    }
}