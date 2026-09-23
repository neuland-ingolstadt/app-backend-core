package app.neuland.ports.inbound;

import app.neuland.model.universitysports.Sports;
import java.util.List;

public interface UniversitySportsUseCase {
    List<Sports> list();
    Sports get(long id);
    Sports create(Sports sport);
    Sports update(long id, Sports sport);
    void delete(long id);
}