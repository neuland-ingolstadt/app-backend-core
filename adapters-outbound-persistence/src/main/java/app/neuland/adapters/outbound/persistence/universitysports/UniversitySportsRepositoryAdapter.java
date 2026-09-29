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
import java.util.stream.Collectors;

@ApplicationScoped
public class UniversitySportsRepositoryAdapter
        implements UniversitySportsRepository {

    private final UniversitySportsPanacheRepository repository;

    public UniversitySportsRepositoryAdapter(
            UniversitySportsPanacheRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Optional<Sports> findById(long id) {
        return Optional.ofNullable(repository.findById(id))
                .map(this::toDomain);
    }

    @Override
    @Transactional
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
        requireContent(sport, Language.DE);
        requireContent(sport, Language.EN);

        entity.contents.clear();
        sport.contents().forEach((language, content) -> {
            UniversitySportsContentEmbeddable embeddable =
                    new UniversitySportsContentEmbeddable();
            embeddable.title = content.title();
            embeddable.description = content.description();
            entity.contents.put(language, embeddable);
        });

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
        Map<Language, SportsContent> contents =
                entity.contents.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> new SportsContent(
                                        entry.getValue().title,
                                        entry.getValue().description
                                )
                        ));

        return new Sports(
                entity.id,
                contents,
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
