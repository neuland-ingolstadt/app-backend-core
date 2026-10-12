package app.neuland.adapters.inbound.rest.security;

/**
 * Dashboard JWT roles, mirrored from the legacy backend
 * ({@code neuland.app-backend/src/utils/auth-utils.ts}).
 *
 * <p>Tokens are RS256 JWTs issued by {@code auth.neuland.ing}; the caller's
 * roles arrive in the {@code groups} claim, which SmallRye JWT maps to the
 * Quarkus caller roles used by {@code @RolesAllowed}.</p>
 *
 * <p>Every protected endpoint also accepts {@link #ADMIN}, matching the
 * legacy {@code checkAuthorization} behavior (required role <em>or</em>
 * admin).</p>
 */
public final class DashboardRoles {

    private DashboardRoles() {
    }

    /** Super-admin role, accepted by every protected endpoint. */
    public static final String ADMIN = "next-dashboard-admin";

    /** May list and resolve room reports. */
    public static final String REPORTS = "next-dashboard-reports";

    /** May create, update and delete announcements. */
    public static final String ANNOUNCEMENTS = "next-dashboard-announcements";

    /** May create, update and delete university sports. */
    public static final String SPORTS = "next-dashboard-sports";

    /** May create, update and delete events. */
    public static final String EVENTS = "next-dashboard-events";
}
