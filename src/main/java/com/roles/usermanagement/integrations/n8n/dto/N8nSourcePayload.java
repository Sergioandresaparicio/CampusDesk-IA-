package com.roles.usermanagement.integrations.n8n.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class N8nSourcePayload {

    @Schema(description = "ID del artículo de la base de conocimiento", example = "1")
    private Long articleId;

    @Schema(description = "Puntuación de similitud semántica calculada (0.0 a 1.0)", example = "0.88")
    private Double similarityScore;
}
