package com.roles.usermanagement.modules.ai.dto;

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
public class AiQueryRequest {

    @NotNull(message = "El ID del ticket es obligatorio")
    @Schema(description = "ID del ticket para el cual se solicita orientación inteligente", example = "101")
    private Long ticketId;

    @NotBlank(message = "La pregunta o consulta no puede estar vacía")
    @Schema(description = "Pregunta específica o descripción del problema a consultar", example = "¿Cómo resolver el error de concesión de IP DHCP en el switch?")
    private String question;
}
