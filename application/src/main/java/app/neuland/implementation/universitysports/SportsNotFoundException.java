package app.neuland.implementation.universitysports;

public class SportsNotFoundException extends RuntimeException {
    public SportsNotFoundException(long id) {
        super("Sport not found: " + id);
    }
}
