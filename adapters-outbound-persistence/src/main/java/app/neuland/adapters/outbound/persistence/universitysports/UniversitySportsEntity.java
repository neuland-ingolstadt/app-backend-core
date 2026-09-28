package app.neuland.adapters.outbound.persistence.universitysports;

import app.neuland.model.universitysports.Campus;
import app.neuland.model.universitysports.SportsCategory;
import app.neuland.model.universitysports.Weekday;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "university_sports")
public class UniversitySportsEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "title_de", nullable = false)
    public String titleDe;

    @Column(name = "description_de")
    public String descriptionDe;

    @Column(name = "title_en", nullable = false)
    public String titleEn;

    @Column(name = "description_en")
    public String descriptionEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Campus campus;

    @Column(nullable = false)
    public String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Weekday weekday;

    @Column(name = "start_time", nullable = false)
    public LocalTime startTime;

    @Column(name = "end_time")
    public LocalTime endTime;

    @Column(name = "requires_registration", nullable = false)
    public boolean requiresRegistration;

    @Column(name = "invitation_link")
    public String invitationLink;

    @Column(name = "e_mail")
    public String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public SportsCategory sportsCategory;
}
