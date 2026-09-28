package app.neuland.adapters.outbound.persistence.universitysports;

import app.neuland.model.shared.Language;
import app.neuland.model.universitysports.Sports;
import app.neuland.model.universitysports.SportsContent;
import app.neuland.ports.outbound.UniversitySportsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UniversitySportsRepositoryAdapter
        implements UniversitySportsRepository {

    private final UniversitySportsPanacheRepository repository;

    public UniversitySportsRepositoryAdapter(
            UniversitySportsPanacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Sports> findById(long id) {
        return Optional.ofNullable(repository.findById(id))
                .map(this::toDomain);
    }

    @Override
    public List<Sports> findAll() {
        return repository.listAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Sports save(Sports sport) {
        UniversitySportsEntity entity;

        if (sport.id() == null) {
            entity = new UniversitySportsEntity();
            applyToEntity(sport, entity);
            repository.persist(entity);
        } else {
            entity = repository.findById(sport.id());

            if (entity == null) {
                throw new IllegalArgumentException(
                        "University sport not found: " + sport.id()
                );
            }

            applyToEntity(sport, entity);
        }

        return toDomain(entity);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    private void applyToEntity(Sports sport, UniversitySportsEntity entity) {
        SportsContent de = requireContent(sport, Language.DE);
        SportsContent en = requireContent(sport, Language.EN);

        entity.titleDe = de.title();
        entity.descriptionDe = de.description();
        entity.titleEn = en.title();
        entity.descriptionEn = en.description();
        entity.campus = sport.campus();
        entity.location = sport.location();
        entity.weekday = sport.weekday();
        entity.startTime = sport.startTime();
        entity.endTime = sport.endTime();
        entity.requiresRegistration = sport.requiresRegistration();
        entity.invitationLink = sport.invitationLink();
        entity.email = sport.email();
        entity.sportsCategory = sport.sportsCategory();
    }

    private SportsContent requireContent(Sports sport, Language language) {
        SportsContent content = sport.contents() == null ? null : sport.contents().get(language);

        if (content == null) {
            throw new IllegalArgumentException(
                    "Missing content for language " + language
            );
        }

        return content;
    }

    private Sports toDomain(UniversitySportsEntity entity) {
        return new Sports(
                entity.id,
                Map.of(
                        Language.DE, new SportsContent(
                                entity.titleDe, entity.descriptionDe),
                        Language.EN, new SportsContent(
                                entity.titleEn, entity.descriptionEn)
                ),
                entity.campus,
                entity.location,
                entity.weekday,
                entity.startTime,
                entity.endTime,
                entity.requiresRegistration,
                entity.invitationLink,
                entity.email,
                entity.sportsCategory
        );
    }
}