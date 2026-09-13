package domain.model.roomreport;

import domain.model.user.User;
import java.time.Instant;

public class RoomReport {
    private final Long id;
    private final String room;
    private final RoomReportCategory reason;
    private final String description;
    private final Instant createdAt;

    private Instant resolvedAt;
    private User resolvedBy;

    public RoomReport(
            Long id,
            String room,
            RoomReportCategory reason,
            String description,
            Instant createdAt
    ) {
        if (room == null || room.isBlank()) {
            throw new IllegalArgumentException("Room must not be blank");
        }

        if (reason == null) {
            throw new IllegalArgumentException("Reason must not be null");
        }

        this.id = id;
        this.room = room;
        this.reason = reason;
        this.description = description;
        this.createdAt = createdAt;
    }

    public void resolve(User admin) {
        if (resolvedAt != null) {
            throw new IllegalStateException("Room report is already resolved");
        }

        this.resolvedAt = Instant.now();
        this.resolvedBy = admin;
    }
}