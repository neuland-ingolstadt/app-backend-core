package app.neuland.adapters.outbound.persistence.auditlogs;

import app.neuland.model.auditlog.AuditLogOperation;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "entity_type", nullable = false)
    public String entity;

    @Column(name = "entity_id")
    public Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public AuditLogOperation operation;

    @Column
    public String name;

    @Column(name = "user_id")
    public String userId;

    @Column(name = "occurred_at", nullable = false)
    public Instant occurredAt;

}
