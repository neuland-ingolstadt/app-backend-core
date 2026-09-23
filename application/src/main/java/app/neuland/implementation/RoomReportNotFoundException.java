package app.neuland.implementation;

public class RoomReportNotFoundException extends RuntimeException {

    public RoomReportNotFoundException(long id) {
        super("Room report not found: " + id);
    }
}
