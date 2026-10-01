package app.neuland.adapters.outbound.persistence.universitysports;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UniversitySportsContentEmbeddable {

    @Column(nullable = false)
    public String title;

    @Column
    public String description;
}
