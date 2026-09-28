package app.neuland.adapters.outbound.persistence.universitysports;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UniversitySportsPanacheRepository
        implements PanacheRepository<UniversitySportsEntity> {
}
