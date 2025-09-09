package com.parking.parkingapp.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import java.util.Date;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${elasticsearch.security.jwt.secret-key}")
    private String secretB64;

    @Value("${elasticsearch.security.jwt.issuer}")
    private String expectedIssuer;

    @Value("${elasticsearch.security.jwt.audience}")
    private String expectedAudience;

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {

        // 401 personalizado (incluye 'token expirado' y WWW-Authenticate)
        AuthenticationEntryPoint bearerEntryPoint = (req, res, ex) -> {
            String message = "Token inválido";
            String errorCode = "invalid_token";
            String description = ex.getMessage();

            if (ex instanceof OAuth2AuthenticationException oae) {
                OAuth2Error err = oae.getError();
                if (err != null) {
                    if (err.getErrorCode() != null) errorCode = err.getErrorCode();
                    if (err.getDescription() != null) description = err.getDescription();
                }
            }
            if (description != null && description.toLowerCase().contains("expired")) {
                message = "Token expirado";
            }

            res.setHeader("WWW-Authenticate",
                    String.format("Bearer error=\"%s\", error_description=\"%s\"",
                            errorCode, escapeJson(description)));

            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write(String.format(
                    "{\"code\":401,\"message\":\"%s\",\"error\":\"%s\",\"details\":\"%s\"}",
                    message, errorCode, escapeJson(description)));
        };

        // 403 personalizado
        AccessDeniedHandler bearerDenied = (req, res, ex) -> {
            res.setHeader("WWW-Authenticate", "Bearer");
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentType("application/json");
            res.getWriter().write("{\"code\":403,\"message\":\"Acceso denegado\"}");
        };

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(bearerEntryPoint)
                        .accessDeniedHandler(bearerDenied)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .authenticationEntryPoint(bearerEntryPoint)
                        .accessDeniedHandler(bearerDenied)
                        .jwt(jwt -> jwt.decoder(jwtDecoder(secretB64, expectedIssuer, expectedAudience)))
                );

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${elasticsearch.security.jwt.secret-key}") String secretB64,
            @Value("${elasticsearch.security.jwt.issuer}") String expectedIssuer,
            @Value("${elasticsearch.security.jwt.audience}") String expectedAudience
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secretB64);
        var key = new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA256");

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // Validador por defecto (firma, exp, nbf, etc.)
        OAuth2TokenValidator<Jwt> withDefaults = JwtValidators.createDefault();

        // Validador de issuer
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(expectedIssuer);

        // Validador de audience
        OAuth2TokenValidator<Jwt> withAudience = new OAuth2TokenValidator<>() {
            @Override
            public OAuth2TokenValidatorResult validate(Jwt token) {
                if (token.getAudience() != null && token.getAudience().contains(expectedAudience)) {
                    return OAuth2TokenValidatorResult.success();
                }
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Invalid audience", null)
                );
            }
        };

        // Combinamos validadores y ajustamos clock skew
        OAuth2TokenValidator<Jwt> withSkew =
                new DelegatingOAuth2TokenValidator<>(
                        withDefaults,
                        withIssuer,
                        withAudience,
                        new OAuth2TokenValidator<Jwt>() {
                            @Override
                            public OAuth2TokenValidatorResult validate(Jwt token) {
                                Date expiresAt = Date.from(token.getExpiresAt());
                                Date now = new Date();
                                return OAuth2TokenValidatorResult.success();
                            }
                        }
                );

        decoder.setJwtValidator(withSkew);
        return decoder;
    }


    // Evita romper el JSON si la descripción trae comillas o saltos de línea
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
