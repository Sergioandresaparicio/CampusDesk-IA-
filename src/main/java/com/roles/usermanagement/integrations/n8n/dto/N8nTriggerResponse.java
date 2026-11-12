package com.roles.usermanagement.integrations.n8n.dto;

import com.roles.usermanagement.modules.ai.dto.AiResponseDto;
import com.roles.usermanagement.modules.ai.enums.AiQueryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class N8nTriggerResponse {

    @Schema(description = "ID de la consulta registrada en BD local", example = "1")
    private Long queryId;

    @Schema(description = "ID del ticket", example = "101")
    private Long ticketId;

    @Schema(description = "Estado actual de la consulta", example = "RESPONDIDA")
    private AiQueryStatus status;

    @Schema(description = "Mensaje informativo del flujo", example = "Flujo de asistencia RAG ejecutado exitosamente")
    private String message;

    @Schema(description = "Indica si se despachó hacia el webhook de n8n", example = "true")
    private boolean dispatchedToN8n;

    @Schema(description = "Respuesta de IA generada síncronamente (si aplica)")
    private AiResponseDto aiResponse;
}
