package app.neuland.adapters.inbound.rest.roomreports;

import app.neuland.model.roomreport.RoomReport;
import app.neuland.model.roomreport.RoomReportCategory;

import java.util.List;

public record RoomReportResponse(
        List<RoomReport> roomReports
) {

    static RoomReport from(RoomReport report) {
        return new RoomReport(
                report.id(),
                report.room(),
                report.reason(),
                report.description(),
                report.resolved()
        );
    }
}
