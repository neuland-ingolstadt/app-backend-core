package app.neuland.model.roomreport;

public record RoomReport(
        Long id,
        String room,
        RoomReportCategory reason,
        String description,
        boolean resolved
) {}
