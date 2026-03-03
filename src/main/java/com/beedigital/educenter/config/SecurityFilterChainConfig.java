package com.beedigital.educenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * SecurityFilterChain - Configuration Spring Security
 *
 * ✅ Endpoints PUBLICS (sans authentification):
 * - POST /api/auth/login
 * - POST /api/auth/register
 * - POST /api/auth/refresh
 * - GET  /api/auth/verify
 *
 * 🔒 Endpoints PROTÉGÉS (avec JWT):
 * - GET  /api/auth/me
 * - POST /api/users
 * - GET  /api/users
 * - etc...
 *
 * @author Équipe Développement
 * @version 2.0
 */
@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    /**
     * Password Encoder - BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS Configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Security Filter Chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver CSRF (API stateless)
                .csrf(csrf -> csrf.disable())

                // CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Sessions
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Autoriser les endpoints PUBLICS
                .authorizeHttpRequests(authz -> authz
                        // ✅ ENDPOINTS PUBLICS - Sans authentification
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/refresh").permitAll()
                        .requestMatchers("/api/auth/verify").permitAll()

                        // ✅ Actuator + Health (optionnel)
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/health/**").permitAll()

                        // 🔒 TOUS LES AUTRES - Authentification requise
                        .anyRequest().authenticated()
                )

                // HTTP Basic (optionnel, pour tester rapidement)
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}