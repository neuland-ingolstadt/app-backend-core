package app.neuland.ports.outbound;

import app.neuland.domain.universitysports.Sports;
import java.util.List;
import java.util.Optional;

public interface UniversitySportsRepository {
    Optional<Sports> findById(long id);
    List<Sports> findAll();
    Sports save(Sports sport);
    void deleteById(long id);
}