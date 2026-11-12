package com.roles.usermanagement.modules.technician.dto;

import com.roles.usermanagement.modules.technician.enums.TechnicianAvailabilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianProfileDto {

    @Schema(description = "Nombre de usuario del técnico", example = "tecnico_redes")
    private String username;

    @Schema(description = "Especialidad técnica", example = "Conectividad y Redes")
    private String specialty;

    @Schema(description = "Estado de disponibilidad", example = "DISPONIBLE")
    private TechnicianAvailabilityStatus availabilityStatus;

    @Schema(description = "Cantidad de tickets activos en atención", example = "2")
    private Integer activeTicketsCount;

    @Schema(description = "Capacidad máxima simultánea de tickets", example = "5")
    private Integer maxConcurrentTickets;
}
