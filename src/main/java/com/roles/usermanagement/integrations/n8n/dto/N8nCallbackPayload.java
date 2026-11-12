package com.roles.usermanagement.integrations.n8n.dto;

import com.roles.usermanagement.modules.ai.enums.AiQueryStatus;
import com.roles.usermanagement.modules.ticket.enums.TicketPriority;
import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class N8nCallbackPayload {

    @NotNull(message = "El queryId es obligatorio")
    @Schema(description = "ID de la consulta a la cual responde el webhook de n8n", example = "1")
    private Long queryId;

    @Schema(description = "ID del ticket asociado", example = "101")
    private Long ticketId;

    @NotNull(message = "El estado de la consulta es obligatorio")
    @Schema(description = "Estado de la respuesta RAG (RESPONDIDA, SIN_RESULTADOS, ERROR)", example = "RESPONDIDA")
    private AiQueryStatus status;

    @NotBlank(message = "El texto de la respuesta es obligatorio")
    @Schema(description = "Respuesta textual generada por el LLM", example = "Basado en los procedimientos verificados, se recomienda reiniciar el servicio de DHCP y verificar el cableado del switch.")
    private String responseText;

    @Schema(description = "Pasos técnicos recomendados", example = "1. Comprobar LED de enlace. 2. Liberar y renovar IP.")
    private String suggestedSteps;

    @Schema(description = "Modelo de LLM utilizado en OpenRouter", example = "anthropic/claude-3.5-sonnet")
    private String modelUsed;

    @Schema(description = "Nivel de confianza semántica", example = "0.94")
    private Double confidenceScore;

    @Schema(description = "Prioridad sugerida por el análisis de IA", example = "ALTA")
    private TicketPriority suggestedPriority;

    @Schema(description = "Estado sugerido para el ticket", example = "IN_PROGRESS")
    private TicketStatus suggestedStatus;

    @Schema(description = "Lista de fuentes utilizadas recuperadas de Qdrant")
    private List<N8nSourcePayload> sources;
}
