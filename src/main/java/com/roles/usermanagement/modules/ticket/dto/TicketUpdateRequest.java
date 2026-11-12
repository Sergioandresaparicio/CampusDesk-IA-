package com.roles.usermanagement.modules.ticket.dto;

import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import com.roles.usermanagement.modules.ticket.enums.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateRequest {

    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    @Schema(description = "Título descriptivo del incidente")
    private String title;

    @Schema(description = "Descripción detallada del incidente")
    private String description;

    @Schema(description = "Categoría del incidente")
    private TicketCategory category;

    @Schema(description = "Prioridad asignada")
    private TicketPriority priority;

    @Schema(description = "Identificador del dispositivo")
    private String device;

    @Schema(description = "Ubicación o sala")
    private String room;
}
