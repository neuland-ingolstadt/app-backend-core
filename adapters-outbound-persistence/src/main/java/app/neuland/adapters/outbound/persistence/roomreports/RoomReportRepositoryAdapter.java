package app.neuland.adapters.outbound.persistence.roomreports;

import app.neuland.model.roomreport.RoomReport;
import app.neuland.ports.outbound.RoomReportRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RoomReportRepositoryAdapter
        implements RoomReportRepository {

    private final RoomReportPanacheRepository repository;

    public RoomReportRepositoryAdapter(
            RoomReportPanacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RoomReport> findById(long id) {
        return Optional.ofNullable(repository.findById(id))
                .map(this::toDomain);
    }

    @Override
    public List<RoomReport> findAll() {
        return repository.listAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public RoomReport save(RoomReport report) {
        RoomReportEntity entity;

        if (report.id() == null) {
            entity = new RoomReportEntity();

            entity.room = report.room();
            entity.reason = report.reason();
            entity.description = report.description();
            entity.resolved = report.resolved();

            repository.persist(entity);
        } else {
            entity = repository.findById(report.id());

            if (entity == null) {
                throw new IllegalArgumentException(
                        "Room report not found: " + report.id()
                );
            }

            entity.room = report.room();
            entity.reason = report.reason();
            entity.description = report.description();
            entity.resolved = report.resolved();
        }

        return toDomain(entity);
    }

    private RoomReport toDomain(RoomReportEntity entity) {
        return new RoomReport(
                entity.id,
                entity.room,
                entity.reason,
                entity.description,
                entity.resolved
        );
    }
}
