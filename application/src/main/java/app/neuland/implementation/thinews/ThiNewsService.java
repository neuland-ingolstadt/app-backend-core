package app.neuland.implementation.thinews;

import app.neuland.model.thinews.ThiNewsItem;
import app.neuland.ports.inbound.ThiNewsUseCase;
import app.neuland.ports.outbound.ThiNewsClient;

import java.util.List;

public class ThiNewsService implements ThiNewsUseCase {

    private final ThiNewsClient thiNewsClient;

    public ThiNewsService(ThiNewsClient thiNewsClient) {
        this.thiNewsClient = thiNewsClient;
    }

    @Override
    public List<ThiNewsItem> list() {
        return thiNewsClient.fetchAll();
    }
}
