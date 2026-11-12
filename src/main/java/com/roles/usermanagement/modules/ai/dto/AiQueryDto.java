package com.roles.usermanagement.modules.ai.dto;

import com.roles.usermanagement.modules.ai.enums.AiQueryStatus;
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
public class AiQueryDto {

    @Schema(description = "ID de la consulta")
    private Long id;

    @Schema(description = "ID del ticket asociado")
    private Long ticketId;

    @Schema(description = "Pregunta realizada")
    private String question;

    @Schema(description = "Estado de la consulta")
    private AiQueryStatus status;

    @Schema(description = "Usuario que realizó la consulta")
    private String username;

    @Schema(description = "Fecha de la consulta")
    private LocalDateTime createdAt;

    @Schema(description = "Respuesta inteligente generada")
    private AiResponseDto response;
}
