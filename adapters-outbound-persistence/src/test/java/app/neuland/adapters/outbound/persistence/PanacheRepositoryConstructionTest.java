package app.neuland.adapters.outbound.persistence;

import app.neuland.adapters.outbound.persistence.announcements.AnnouncementPanacheRepository;
import app.neuland.adapters.outbound.persistence.auditlogs.AuditLogPanacheRepository;
import app.neuland.adapters.outbound.persistence.roomreports.RoomReportPanacheRepository;
import app.neuland.adapters.outbound.persistence.universitysports.UniversitySportsPanacheRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PanacheRepositoryConstructionTest {

    @Test
    void shouldConstructRepositories() {
        assertNotNull(new AnnouncementPanacheRepository());
        assertNotNull(new RoomReportPanacheRepository());
        assertNotNull(new UniversitySportsPanacheRepository());
        assertNotNull(new AuditLogPanacheRepository());
    }
}
