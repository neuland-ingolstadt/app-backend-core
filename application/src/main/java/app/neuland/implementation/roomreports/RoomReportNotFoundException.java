package app.neuland.implementation.roomreports;

public class RoomReportNotFoundException extends RuntimeException {

    public RoomReportNotFoundException(long id) {
        super("Room report not found: " + id);
    }
}
