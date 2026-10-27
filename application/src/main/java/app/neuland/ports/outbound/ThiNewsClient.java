package app.neuland.ports.outbound;

import app.neuland.model.thinews.ThiNewsItem;

import java.util.List;

public interface ThiNewsClient {

    List<ThiNewsItem> fetchAll();
}
