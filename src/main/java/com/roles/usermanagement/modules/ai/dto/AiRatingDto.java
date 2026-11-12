package com.roles.usermanagement.modules.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRatingDto {

    @Schema(description = "ID de la calificación")
    private Long id;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    @Schema(description = "Calificación de utilidad de la respuesta de 1 a 5", example = "5")
    private Integer score;

    @Schema(description = "Comentario opcional sobre la calidad de la respuesta", example = "Solución exacta y rápida")
    private String comment;

    @Schema(description = "Usuario que calificó")
    private String ratedBy;

    @Schema(description = "Fecha de la calificación")
    private LocalDateTime createdAt;
}
