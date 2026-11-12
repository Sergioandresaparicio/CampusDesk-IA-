package com.roles.usermanagement.modules.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAssignRequest {

    @NotBlank(message = "El nombre de usuario del técnico es obligatorio")
    @Schema(description = "Nombre de usuario del técnico asignado", example = "tecnico_redes")
    private String technicianUsername;

    @Schema(description = "Motivo de la asignación", example = "Asignación inicial por especialidad en redes")
    private String reason;
}
