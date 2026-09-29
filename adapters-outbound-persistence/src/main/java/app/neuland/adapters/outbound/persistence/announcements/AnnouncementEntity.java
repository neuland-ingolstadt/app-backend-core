package app.neuland.adapters.outbound.persistence.announcements;

import app.neuland.model.announcement.Platform;
import app.neuland.model.announcement.UserKind;
import app.neuland.model.shared.Language;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "announcements")
public class AnnouncementEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ElementCollection
    @CollectionTable(
            name = "announcement_platforms",
            joinColumns = @JoinColumn(name = "announcement_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    public Set<Platform> platforms = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "announcement_user_kinds",
            joinColumns = @JoinColumn(name = "announcement_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "user_kind", nullable = false)
    public Set<UserKind> userKinds = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "announcement_contents",
            joinColumns = @JoinColumn(name = "announcement_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "language", nullable = false)
    public Map<Language, AnnouncementContentEmbeddable> contents = new HashMap<>();

    @Column(name = "start_date_time", nullable = false)
    public Instant startDateTime;

    @Column(name = "end_date_time", nullable = false)
    public Instant endDateTime;

    @Column(nullable = false)
    public Integer priority;

    @Column
    public String url;

    @Column(name = "image_url")
    public String imageUrl;
}