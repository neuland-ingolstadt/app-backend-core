package app.neuland.model.roomreport;

import java.time.Instant;

public record RoomReport(
        Long id,
        String room,
        RoomReportCategory reason,
        String description,
        Instant resolvedAt
) {}
