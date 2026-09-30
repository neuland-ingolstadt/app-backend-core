package app.neuland.adapters.outbound.persistence.roomreports;

import app.neuland.model.roomreport.RoomReportCategory;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "room_reports")
public class RoomReportEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public RoomReportCategory reason;

    @Column
    public String description;

    @Column(name = "resolved_at")
    public Instant resolvedAt;
}
