package app.neuland.domain.universitySports;

import java.util.Optional;

public record SportsContent(String title, String description) {

    public SportsContent {
        if ( title == null || title.isBlank() ) {
            throw new IllegalArgumentException("Title must not be blank");
        }
    }

    public Optional<String> descriptionOpt() {
        return Optional.ofNullable(description);
    }

}
