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
        return new RoomReportListResponse(
                reports.stream()
                        .map(RoomReportMapper::toApiReport)
                        .toList()
        );
    }

    static RoomReportResponse toIdResponse(Long id) {
        return new RoomReportResponse(id);
    }

    private static app.neuland.backend.core.api.v0.model.RoomReport toApiReport(RoomReport report) {
        app.neuland.backend.core.api.v0.model.RoomReport api =
                new app.neuland.backend.core.api.v0.model.RoomReport(
                        report.id(),
                        report.room(),
                        toApiCategory(report.reason())
                );
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
