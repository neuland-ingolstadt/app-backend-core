package app.neuland.ports.inbound;

import app.neuland.model.universitysports.Sports;
import java.util.List;

public interface UniversitySportsUseCase {
    List<Sports> list();
    Sports get(long id);
    Sports create(Sports sport, String userId);
    Sports update(long id, Sports sport, String userId);
    void delete(long id, String userId);
}