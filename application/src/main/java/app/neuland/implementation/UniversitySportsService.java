package app.neuland.implementation;

import app.neuland.model.universitysports.Sports;
import app.neuland.ports.inbound.UniversitySportsUseCase;
import app.neuland.ports.outbound.UniversitySportsRepository;
import java.util.List;

public class UniversitySportsService implements UniversitySportsUseCase {

    private final UniversitySportsRepository universitySportsRepository;
    public UniversitySportsService(UniversitySportsRepository universitySportsRepository) {
        this.universitySportsRepository = universitySportsRepository;
    }

    @Override
    public List<Sports> list() {
        return universitySportsRepository.findAll();
    }

    @Override
    public Sports get(long id) {
        return universitySportsRepository
                .findById(id)
                .orElseThrow(() -> new SportsNotFoundException(id));
    }

    @Override
    public Sports create(Sports sport) {
        Sports toCreate = new Sports(
                null,
                sport.contents(),
                sport.campus(),
                sport.location(),
                sport.weekday(),
                sport.startTime(),
                sport.endTime(),
                sport.requiresRegistration(),
                sport.invitationLink(),
                sport.email(),
                sport.sportsCategory()
        );

        return universitySportsRepository.save(toCreate);

    }

    @Override
    public Sports update(long id, Sports sport) {
        universitySportsRepository
                .findById(id)
                .orElseThrow(() -> new SportsNotFoundException(id));

        Sports updated = new Sports(
                id,
                sport.contents(),
                sport.campus(),
                sport.location(),
                sport.weekday(),
                sport.startTime(),
                sport.endTime(),
                sport.requiresRegistration(),
                sport.invitationLink(),
                sport.email(),
                sport.sportsCategory()
        );

        return universitySportsRepository.save(updated);

    }

    @Override
    public void delete(long id) {
        universitySportsRepository
                .findById(id)
                .orElseThrow(() -> new SportsNotFoundException(id));

        universitySportsRepository.deleteById(id);
    }

}
