package com.roles.usermanagement.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Explicit Cross-Origin Resource Sharing (CORS) configuration.
 * Authorizes the local Frontend client origins, local development ports, and local n8n automation instances.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allowed local frontend and integration origins
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:[*]",
                "http://127.0.0.1:[*]"
        ));
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5500",
                "http://127.0.0.1:5500",
                "http://localhost:5501",
                "http://127.0.0.1:5501",
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:5678",
                "http://127.0.0.1:5678",
                "http://localhost:8051"
        ));

        // Allowed HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allowed Headers including M2M API Key
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-N8N-Api-Key",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));

        config.setExposedHeaders(List.of("Authorization", "X-N8N-Api-Key"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
