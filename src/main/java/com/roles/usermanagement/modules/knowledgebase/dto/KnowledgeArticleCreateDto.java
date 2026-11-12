package com.roles.usermanagement.modules.knowledgebase.dto;

import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeArticleCreateDto {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede superar 200 caracteres")
    @Schema(description = "Título del artículo", example = "Solución a desconexión intermitente de WiFi")
    private String title;

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "Categoría tecnológica", example = "REDES")
    private TicketCategory category;

    @NotBlank(message = "La descripción del problema es obligatoria")
    @Schema(description = "Descripción del problema")
    private String problem;

    @Schema(description = "Síntomas observados")
    private String symptoms;

    @Schema(description = "Causa raíz identificada")
    private String cause;

    @NotBlank(message = "El procedimiento de solución es obligatorio")
    @Schema(description = "Procedimiento paso a paso de resolución")
    private String solution;

    @Builder.Default
    @Schema(description = "Indica si el artículo está activo")
    private Boolean active = true;

    @Builder.Default
    @Schema(description = "Indica si el artículo fue verificado por un administrador")
    private Boolean verified = true;
}
