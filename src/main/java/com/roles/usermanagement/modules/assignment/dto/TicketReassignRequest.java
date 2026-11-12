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
public class TicketReassignRequest {

    @NotBlank(message = "El nuevo técnico es obligatorio")
    @Schema(description = "Nombre de usuario del nuevo técnico", example = "tecnico_hardware")
    private String newTechnicianUsername;

    @NotBlank(message = "La justificación de reasignación es obligatoria")
    @Schema(description = "Justificación de la reasignación", example = "El problema requiere cambio de tarjeta de red física")
    private String reason;
}
