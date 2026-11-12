package com.roles.usermanagement.modules.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseSourceDto {

    @Schema(description = "ID del artículo de la base de conocimiento", example = "1")
    private Long articleId;

    @Schema(description = "Título del artículo", example = "Configuración de Servidores DHCP")
    private String articleTitle;

    @Schema(description = "Puntuación de similitud semántica calculada por embeddings (0.0 a 1.0)", example = "0.89")
    private Double similarityScore;
}
