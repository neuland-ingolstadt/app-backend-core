package app.neuland.adapters.inbound.rest.roomreports;

import app.neuland.backend.core.api.v0.model.CreateRoomReportRequest;
import app.neuland.backend.core.api.v0.model.RoomReportCategoryEnum;
import app.neuland.backend.core.api.v0.model.RoomReportListResponse;
import app.neuland.backend.core.api.v0.model.RoomReportResponse;
import app.neuland.model.roomreport.RoomReport;
import app.neuland.model.roomreport.RoomReportCategory;

import java.time.ZoneOffset;
import java.util.List;

record RoomReportMapper() {

    static RoomReport toDomain(CreateRoomReportRequest request) {
        return new RoomReport(
                null,
                request.getRoom(),
                toDomainCategory(request.getReason()),
                request.getDescription(),
                null
        );
    }

    static RoomReportListResponse toListResponse(List<RoomReport> reports) {
        RoomReportListResponse response = new RoomReportListResponse();
        response.setRoomReports(reports.stream()
                .map(RoomReportMapper::toApiReport)
                .toList());
        return response;
    }

    static RoomReportResponse toIdResponse(Long id) {
        RoomReportResponse response = new RoomReportResponse();
        response.setId(id);
        return response;
    }

    private static app.neuland.backend.core.api.v0.model.RoomReport toApiReport(RoomReport report) {
        app.neuland.backend.core.api.v0.model.RoomReport api =
                new app.neuland.backend.core.api.v0.model.RoomReport();
        api.setId(report.id());
        api.setRoom(report.room());
        api.setReason(toApiCategory(report.reason()));
        api.setDescription(report.description());
        if (report.resolvedAt() != null) {
            api.setResolvedAt(report.resolvedAt().atOffset(ZoneOffset.UTC));
        }
        return api;
    }

    private static RoomReportCategory toDomainCategory(RoomReportCategoryEnum reason) {
        return RoomReportCategory.valueOf(reason.name());
    }

    private static RoomReportCategoryEnum toApiCategory(RoomReportCategory reason) {
        return RoomReportCategoryEnum.fromValue(reason.name());
    }
}
