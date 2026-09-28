package app.neuland.adapters.inbound.rest.roomreports;

import jakarta.validation.constraints.NotNull;

public record ResolveRoomReportRequest(

        @NotNull
        Boolean resolved
) {
}
