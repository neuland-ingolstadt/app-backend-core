package app.neuland.implementation;

public class SportsNotFoundException extends RuntimeException {
    public SportsNotFoundException(long id) {
        super("Sport not found: " + id);
    }
}
