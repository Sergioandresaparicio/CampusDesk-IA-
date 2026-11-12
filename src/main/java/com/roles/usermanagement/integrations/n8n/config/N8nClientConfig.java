package com.roles.usermanagement.integrations.n8n.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class N8nClientConfig {

    @Value("${n8n.webhook.url:http://localhost:5678/webhook/campusdesk-incident}")
    private String webhookUrl;

    @Value("${n8n.api-key:campusdesk-n8n-m2m-secret-key-2026}")
    private String apiKey;

    @Bean
    public RestClient n8nRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(15));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeader("X-N8N-Api-Key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
