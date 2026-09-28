package app.neuland.adapters.inbound.rest.roomreports;

import app.neuland.model.roomreport.RoomReport;
import app.neuland.model.roomreport.RoomReportCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRoomReportRequest(

        @NotBlank
        @Size(max = 255)
        String room,

        @NotNull
        RoomReportCategory reason,

        @NotBlank
        @Size(max = 2000)
        String description
) {

    RoomReport toDomain() {
        return new RoomReport(
                null,
                room,
                reason,
                description,
                false
        );
    }
}
