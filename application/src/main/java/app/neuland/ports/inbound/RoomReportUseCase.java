package app.neuland.ports.inbound;

import app.neuland.model.roomreport.RoomReport;
import java.util.List;

public interface RoomReportUseCase {
    List<RoomReport> list();

    Long create(RoomReport report);

    Long resolve(long id, boolean resolved);
}
