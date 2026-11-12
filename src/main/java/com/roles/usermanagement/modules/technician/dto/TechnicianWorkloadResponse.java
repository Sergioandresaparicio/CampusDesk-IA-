package com.roles.usermanagement.modules.technician.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianWorkloadResponse {

    @Schema(description = "Nombre de usuario del técnico")
    private String username;

    @Schema(description = "Especialidad")
    private String specialty;

    @Schema(description = "Tickets activos")
    private Integer activeTickets;

    @Schema(description = "Capacidad máxima")
    private Integer maxCapacity;

    @Schema(description = "Porcentaje de ocupación actual", example = "40.0")
    private Double workloadPercentage;

    @Schema(description = "Disponibilidad para recibir tickets", example = "true")
    private Boolean available;
}
