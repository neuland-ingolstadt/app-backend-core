package app.neuland.adapters.inbound.rest.error;

import app.neuland.implementation.announcements.AnnouncementNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AnnouncementNotFoundExceptionMapper
        extends AbstractProblemExceptionMapper<AnnouncementNotFoundException> {

    @Override
    protected Problem problem(AnnouncementNotFoundException exception) {
        return problem(Response.Status.NOT_FOUND, exception.getMessage());
    }
}