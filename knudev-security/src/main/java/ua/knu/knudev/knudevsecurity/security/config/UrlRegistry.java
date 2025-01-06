package ua.knu.knudev.knudevsecurity.security.config;

import java.util.Set;

public class UrlRegistry {

    public static final String[] WHITE_LIST_URLS = {
            "/account/register",
            "/auth/login",
            "/test/guest",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/api/public/**",
            "/api/public/authenticate",
            "/actuator/*",
            "/swagger-ui/**",
            "/error/**",
    };
    public static final Set<String> AUTH_EXCLUDED_URLS = Set.of(
            "/account/create-password",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/api/public/**",
            "/api/public/authenticate",
            "/actuator/*",
            "/swagger-ui/**"
    );
    public static String AUTH_URL = "/auth";

}
