package app.neuland.adapters.outbound.persistence.announcements;

import app.neuland.model.announcement.Announcement;
import app.neuland.model.announcement.AnnouncementContent;
import app.neuland.model.shared.Language;
import app.neuland.ports.outbound.AnnouncementRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class AnnouncementRepositoryAdapter
        implements AnnouncementRepository {

    private final AnnouncementPanacheRepository repository;

    public AnnouncementRepositoryAdapter(
            AnnouncementPanacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Announcement> findById(long id) {
        return Optional.ofNullable(repository.findById(id))
                .map(this::toDomain);
    }

    @Override
    public List<Announcement> findAll() {
        return repository.listAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Announcement> findActiveAt(Instant at) {
        return repository
                .find(
                        "startDateTime <= ?1 and endDateTime >= ?1",
                        at
                )
                .list()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Announcement save(Announcement announcement) {
        AnnouncementEntity entity;

        if (announcement.id() == null) {
            entity = new AnnouncementEntity();
            repository.persist(entity);
        } else {
            entity = repository.findById(announcement.id());

            if (entity == null) {
                throw new IllegalArgumentException(
                        "Announcement not found: " + announcement.id()
                );
            }
        }

        entity.platforms.clear();
        entity.platforms.addAll(announcement.platforms());

        entity.userKinds.clear();
        entity.userKinds.addAll(announcement.userKinds());

        entity.contents.clear();

        announcement.contents().forEach((language, content) -> {
            AnnouncementContentEmbeddable embeddable =
                    new AnnouncementContentEmbeddable();

            embeddable.title = content.title();
            embeddable.description = content.description();

            entity.contents.put(language, embeddable);
        });

        entity.startDateTime = announcement.startDateTime();
        entity.endDateTime = announcement.endDateTime();
        entity.priority = announcement.priority();
        entity.url = announcement.url();
        entity.imageUrl = announcement.imageUrl();

        return toDomain(entity);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        AnnouncementEntity entity = repository.findById(id);

        if (entity == null) {
            throw new IllegalArgumentException(
                    "Announcement not found: " + id
            );
        }

        repository.delete(entity);
    }

    private Announcement toDomain(AnnouncementEntity entity) {
        Map<Language, AnnouncementContent> contents =
                entity.contents.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> new AnnouncementContent(
                                        entry.getValue().title,
                                        entry.getValue().description
                                )
                        ));

        return new Announcement(
                entity.id,
                entity.platforms,
                entity.userKinds,
                contents,
                entity.startDateTime,
                entity.endDateTime,
                entity.priority,
                entity.url,
                entity.imageUrl
        );
    }
}
