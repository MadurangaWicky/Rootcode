package com.rootcode.backend.configuration;

public class SecurityConstants {
    public static final String[] PERMITTED_PATHS = {
            "/api/user/login",
            "/api/user/registration",
            "/api/book/books/list",
            "/api/user/test",
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
} 