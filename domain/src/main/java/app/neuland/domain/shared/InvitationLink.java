package app.neuland.domain.shared;

import java.net.URI;
import java.net.URISyntaxException;

public record InvitationLink(URI value) {

    public InvitationLink {
        if ( value.getScheme() == null || value.getHost() == null ) {
            throw new IllegalArgumentException("Invalid URL format: " + value);
        }
    }

    public static InvitationLink of(String rawUrl) {
        try {
            return new InvitationLink(new URI(rawUrl));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL form: " + rawUrl, e);
        }
    }

}
