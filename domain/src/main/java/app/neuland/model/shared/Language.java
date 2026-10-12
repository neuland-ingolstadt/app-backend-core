package app.neuland.model.shared;

import java.util.Locale;

public enum Language {
    DE,
    EN;

    public String code() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Language fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Language code must not be null");
        }
        return valueOf(code.toUpperCase(Locale.ROOT));
    }
}
