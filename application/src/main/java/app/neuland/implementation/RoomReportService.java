package app.neuland.implementation;

import app.neuland.model.roomreport.RoomReport;
import app.neuland.ports.inbound.RoomReportUseCase;
import app.neuland.ports.outbound.RoomReportRepository;

import java.util.List;

public class RoomReportService implements RoomReportUseCase {

    private final RoomReportRepository roomReportRepository;

    public RoomReportService(RoomReportRepository roomReportRepository) {
        this.roomReportRepository = roomReportRepository;
    }

    @Override
    public List<RoomReport> list() {
        return roomReportRepository.findAll();
    }

    @Override
    public Long create(RoomReport report) {
        RoomReport toCreate = new RoomReport(
                null,
                report.room(),
                report.reason(),
                report.description(),
                false
        );
        return roomReportRepository.save(toCreate).id();
    }

    @Override
    public Long resolve(long id, boolean resolved) {
        RoomReport existing = roomReportRepository
                .findById(id)
                .orElseThrow(() -> new RoomReportNotFoundException(id));

        RoomReport updated = new RoomReport(
                existing.id(),
                existing.room(),
                existing.reason(),
                existing.description(),
                resolved
        );
        return roomReportRepository.save(updated).id();
    }
}
