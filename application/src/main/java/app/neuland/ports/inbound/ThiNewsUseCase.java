package app.neuland.ports.inbound;

import app.neuland.model.thinews.ThiNewsItem;

import java.util.List;

public interface ThiNewsUseCase {

    List<ThiNewsItem> list();
}
