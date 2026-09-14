package app.neuland.model;

import java.util.regex.Pattern;

public record EmailAddress(String value) {

    private static final Pattern PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailAddress {
        if ( value == null || !PATTERN.matcher(value).matches() ) {
            throw new IllegalArgumentException("Invalid e-Mail format: " + value);
        }
    }

}
