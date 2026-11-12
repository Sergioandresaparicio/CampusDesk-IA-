package com.roles.usermanagement.integrations.n8n.controller;

import com.roles.usermanagement.core.common.ApiResponse;
import com.roles.usermanagement.integrations.n8n.dto.N8nCallbackPayload;
import com.roles.usermanagement.integrations.n8n.dto.N8nDataResponse;
import com.roles.usermanagement.integrations.n8n.dto.N8nTriggerRequest;
import com.roles.usermanagement.integrations.n8n.dto.N8nTriggerResponse;
import com.roles.usermanagement.integrations.n8n.service.N8nIntegrationService;
import com.roles.usermanagement.modules.ai.dto.AiQueryDto;
import com.roles.usermanagement.modules.ai.dto.AiRatingDto;
import com.roles.usermanagement.modules.ai.dto.AiResponseDto;
import com.roles.usermanagement.web.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/integrations/n8n")
@Tag(name = "Integración n8n & Asistencia IA", description = "Endpoints de comunicación M2M, webhooks y callbacks de IA (RF03, RF04, RF05)")
public class N8nIntegrationController {

    private final N8nIntegrationService integrationService;

    public N8nIntegrationController(N8nIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping("/trigger")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @Operation(summary = "Disparar flujo de asistencia inteligente RAG hacia n8n",
            description = "Envía la incidencia y consulta técnica hacia el webhook local de n8n para análisis vectorial y generación RAG.")
    public ResponseEntity<ApiResponse<N8nTriggerResponse>> triggerN8n(
            @Valid @RequestBody N8nTriggerRequest request,
            Principal principal
    ) {
        N8nTriggerResponse response = integrationService.triggerN8n(request, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Disparo de asistencia ejecutado", response));
    }

    @PostMapping("/callback")
    @PreAuthorize("hasRole('M2M_N8N') or hasRole('ADMIN')")
    @SecurityRequirement(name = OpenApiConfig.N8N_API_KEY)
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @Operation(summary = "Callback M2M para recepción de respuestas desde n8n",
            description = "Endpoint receptor protegido por cabecera X-N8N-Api-Key utilizado por n8n para actualizar estados y persistir soluciones RAG.")
    public ResponseEntity<ApiResponse<AiResponseDto>> callback(
            @Valid @RequestBody N8nCallbackPayload payload
    ) {
        AiResponseDto response = integrationService.processCallback(payload);
        return ResponseEntity.ok(ApiResponse.success("Respuesta RAG y fuentes registradas con éxito", response));
    }

    @GetMapping("/data")
    @PreAuthorize("hasRole('M2M_N8N') or hasRole('ADMIN')")
    @SecurityRequirement(name = OpenApiConfig.N8N_API_KEY)
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @Operation(summary = "Consultar datos optimizados para nodos HTTP Request de n8n",
            description = "Retorna artículos técnicos activos y verificados para indexación en Qdrant e incidencias pendientes para triage.")
    public ResponseEntity<ApiResponse<N8nDataResponse>> getDataForN8n() {
        N8nDataResponse data = integrationService.getDataForN8n();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/queries/{queryId}/rate")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @Operation(summary = "Calificar utilidad de la respuesta de IA de 1 a 5 (RF08)",
            description = "Permite evaluar la respuesta generada por el Asistente RAG para alimentar métricas de calidad y evaluación del modelo.")
    public ResponseEntity<ApiResponse<AiRatingDto>> rateResponse(
            @PathVariable Long queryId,
            @RequestParam int score,
            @RequestParam(required = false) String comment,
            Principal principal
    ) {
        AiRatingDto rating = integrationService.rateResponse(queryId, score, comment, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Calificación registrada con éxito", rating));
    }

    @GetMapping("/queries/ticket/{ticketId}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @Operation(summary = "Consultar historial de asistencias inteligentes de un ticket (RF08)")
    public ResponseEntity<ApiResponse<List<AiQueryDto>>> getQueryHistory(@PathVariable Long ticketId) {
        List<AiQueryDto> history = integrationService.getQueryHistory(ticketId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
