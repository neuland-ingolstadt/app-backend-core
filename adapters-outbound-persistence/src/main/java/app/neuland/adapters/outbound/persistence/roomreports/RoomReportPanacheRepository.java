package app.neuland.adapters.outbound.persistence.roomreports;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoomReportPanacheRepository
        implements PanacheRepository<RoomReportEntity> {
}
