package app.neuland.domain.roomreport;

import java.time.Instant;

public record RoomReport(
        Long id,
        String room,
        RoomReportCategory reason,
        String description,
        Instant createdAt,
        Instant resolvedAt,
        String resolvedBy
) {}
