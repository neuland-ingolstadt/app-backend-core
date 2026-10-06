package app.neuland.security;

import app.neuland.adapters.inbound.security.SecurityRoles;
import io.smallrye.jwt.build.Jwt;

public final class JwtTestFixture {

    private static final String ISSUER = "test-issuer";

    private JwtTestFixture() {
    }

    public static String reportsToken() {
        return tokenWithGroup(SecurityRoles.reportsRole);
    }

    public static String adminToken() {
        return tokenWithGroup(SecurityRoles.adminRole);
    }

    public static String wrongGroupToken() {
        return tokenWithGroup("some-other-group");
    }

    private static String tokenWithGroup(String group) {
        return Jwt.issuer(ISSUER)
                .subject("test-user")
                .groups(group)
                .sign();
    }
}
