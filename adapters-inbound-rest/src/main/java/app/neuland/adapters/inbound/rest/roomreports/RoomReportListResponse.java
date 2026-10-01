package app.neuland.adapters.inbound.rest.roomreports;

import app.neuland.model.roomreport.RoomReport;

import java.util.List;

public record RoomReportListResponse(
        List<RoomReport> roomReports
) {
}