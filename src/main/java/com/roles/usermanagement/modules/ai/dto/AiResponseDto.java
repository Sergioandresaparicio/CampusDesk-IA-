package com.roles.usermanagement.modules.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {

    @Schema(description = "ID de la respuesta de IA", example = "50")
    private Long id;

    @Schema(description = "Solución sugerida por el modelo RAG", example = "Verifique la tabla ARP del switch y renueve el lease DHCP.")
    private String responseText;

    @Schema(description = "Pasos técnicos recomendados", example = "1. Acceder al switch. 2. Ejecutar clear ip dhcp binding.")
    private String suggestedSteps;

    @Schema(description = "Modelo de IA utilizado", example = "meta-llama/llama-3-70b-instruct")
    private String modelUsed;

    @Schema(description = "Nivel de confianza de la respuesta", example = "0.92")
    private Double confidenceScore;

    @Schema(description = "Fecha de generación")
    private LocalDateTime createdAt;

    @Schema(description = "Fuentes documentales utilizadas con similitud semántica")
    private List<AiResponseSourceDto> sources;

    @Schema(description = "Calificación recibida")
    private AiRatingDto rating;
}
