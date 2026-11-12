package com.roles.usermanagement.modules.knowledgebase.dto;

import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeArticleDto {

    @Schema(description = "ID del artículo")
    private Long id;

    @Schema(description = "Título")
    private String title;

    @Schema(description = "Categoría")
    private TicketCategory category;

    @Schema(description = "Problema")
    private String problem;

    @Schema(description = "Síntomas")
    private String symptoms;

    @Schema(description = "Causa raíz")
    private String cause;

    @Schema(description = "Solución paso a paso")
    private String solution;

    @Schema(description = "Activo para RAG")
    private Boolean active;

    @Schema(description = "Verificado")
    private Boolean verified;

    @Schema(description = "ID de embedding en Qdrant")
    private String embeddingId;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}
