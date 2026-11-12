package com.roles.usermanagement.modules.ticket.dto;

import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import com.roles.usermanagement.modules.ticket.enums.TicketPriority;
import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
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
public class TicketResponse {

    @Schema(description = "ID único de la incidencia", example = "101")
    private Long id;

    @Schema(description = "Título", example = "Falla de conectividad WiFi")
    private String title;

    @Schema(description = "Descripción")
    private String description;

    @Schema(description = "Categoría")
    private TicketCategory category;

    @Schema(description = "Prioridad")
    private TicketPriority priority;

    @Schema(description = "Estado actual")
    private TicketStatus status;

    @Schema(description = "Usuario solicitante", example = "usuario_campus")
    private String applicantUsername;

    @Schema(description = "Técnico asignado", example = "tecnico_redes")
    private String technicianUsername;

    @Schema(description = "Dispositivo afectado")
    private String device;

    @Schema(description = "Ubicación o sala")
    private String room;

    @Schema(description = "Notas de resolución técnica")
    private String resolutionNotes;

    @Schema(description = "Fecha de registro")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última modificación")
    private LocalDateTime updatedAt;

    @Schema(description = "Fecha de asignación a técnico")
    private LocalDateTime assignedAt;

    @Schema(description = "Fecha de resolución")
    private LocalDateTime resolvedAt;

    @Schema(description = "Fecha de cierre definitivo")
    private LocalDateTime closedAt;
}
