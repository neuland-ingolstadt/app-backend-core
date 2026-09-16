package app.neuland.domain.roomreport;

public record RoomReport(
        Long id,
        String room,
        RoomReportCategory reason,
        String description,
        boolean resolved
) {}
