package app.neuland.adapters.outbound.persistence.roomreports;

import app.neuland.model.roomreport.RoomReportCategory;

import java.time.Instant;

final class RoomReportEntityFixtures {

    private RoomReportEntityFixtures() {
    }

    static RoomReportEntity entity(
            Long id,
            String room,
            RoomReportCategory reason,
            String description,
            Instant resolvedAt
    ) {
        RoomReportEntity entity = new RoomReportEntity();
        entity.id = id;
        entity.room = room;
        entity.reason = reason;
        entity.description = description;
        entity.resolvedAt = resolvedAt;
        return entity;
    }
}
