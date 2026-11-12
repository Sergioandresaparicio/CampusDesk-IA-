package com.roles.usermanagement.web.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter for Machine-to-Machine (M2M) authentication supporting n8n webhooks and callbacks.
 * Checks the X-N8N-Api-Key header to grant authenticated M2M credentials without disrupting user JWT flows.
 */
@Component
public class N8nApiKeyFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "X-N8N-Api-Key";

    private final String expectedApiKey;

    public N8nApiKeyFilter(@Value("${n8n.api-key:campusdesk-n8n-m2m-secret-key-2026}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKeyHeader = request.getHeader(API_KEY_HEADER);
        if (apiKeyHeader != null && !apiKeyHeader.isBlank()) {
            if (apiKeyHeader.trim().equals(expectedApiKey.trim())) {
                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_M2M_N8N"),
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("M2M_ACCESS"),
                        new SimpleGrantedAuthority("TICKET_STATUS_UPDATE"),
                        new SimpleGrantedAuthority("COMMENT_CREATE"),
                        new SimpleGrantedAuthority("AI_MANAGE"),
                        new SimpleGrantedAuthority("KNOWLEDGE_MANAGE")
                );
                var authentication = new UsernamePasswordAuthenticationToken(
                        "n8n-automation-agent",
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
