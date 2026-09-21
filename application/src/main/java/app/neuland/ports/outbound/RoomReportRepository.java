package app.neuland.ports.outbound;

import app.neuland.model.roomreport.RoomReport;
import java.util.List;
import java.util.Optional;

public interface RoomReportRepository {
    Optional<RoomReport> findById(long id);

    List<RoomReport> findAll();

    RoomReport save(RoomReport report);
}
