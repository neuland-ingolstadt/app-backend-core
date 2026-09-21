package app.neuland.ports.inbound;

import app.neuland.model.roomreport.RoomReport;
import java.util.List;

public interface RoomReportUseCase {
    List<RoomReport> list();

    RoomReport create(RoomReport report);

    RoomReport resolve(long id, boolean resolved);
}
