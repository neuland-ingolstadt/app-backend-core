package app.neuland.adapters.outbound.persistence.auditlogs;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
public class AuditLogPanacheRepository
        implements PanacheRepository<AuditLogEntity> {

    public java.util.List<AuditLogEntity> findPage(
            long limit, Instant afterOccurredAt, Long afterId) {

        Sort sort = Sort.by("occurredAt", Sort.Direction.Descending)
                .and("id", Sort.Direction.Descending);

        if (afterOccurredAt == null) {
            return findAll(sort).page(0, (int) limit + 1).list();
        }

        return find("occurredAt < ?1 or (occurredAt = ?1 and id < ?2)",
                sort, afterOccurredAt, afterId)
                .page(0, (int) limit + 1).list();
    }
}
