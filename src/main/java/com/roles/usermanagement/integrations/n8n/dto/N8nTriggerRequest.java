package com.roles.usermanagement.integrations.n8n.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class N8nTriggerRequest {

    @NotNull(message = "El ID del ticket es obligatorio")
    @Schema(description = "ID del ticket para el cual se dispara la asistencia de IA", example = "101")
    private Long ticketId;

    @NotBlank(message = "La consulta técnica es obligatoria")
    @Schema(description = "Pregunta técnica o descripción para búsqueda semántica RAG", example = "No hay conectividad a Internet en el aula 302 tras reinicio del switch")
    private String question;

    @Builder.Default
    @Schema(description = "Indica si el disparo hacia n8n se procesa de forma asíncrona", example = "false")
    private Boolean async = false;
}
