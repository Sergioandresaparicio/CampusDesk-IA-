package com.roles.usermanagement.modules.ticket.dto;

import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatusTransitionRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    @Schema(description = "Nuevo estado al que transita la incidencia", example = "IN_PROGRESS")
    private TicketStatus status;

    @Schema(description = "Justificación del cambio de estado", example = "Se inicia revisión técnica en sitio.")
    private String reason;

    @Schema(description = "Notas de resolución (obligatorio si se resuelve)", example = "Se reinició el punto de acceso AP-302 y se reconfiguró DHCP.")
    private String resolutionNotes;
}
