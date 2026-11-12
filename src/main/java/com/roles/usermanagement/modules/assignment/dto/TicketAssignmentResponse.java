package com.roles.usermanagement.modules.assignment.dto;

import com.roles.usermanagement.modules.assignment.enums.AssignmentType;
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
public class TicketAssignmentResponse {

    @Schema(description = "ID del registro de asignación")
    private Long id;

    @Schema(description = "ID del ticket")
    private Long ticketId;

    @Schema(description = "Técnico anterior")
    private String previousTechnician;

    @Schema(description = "Nuevo técnico asignado")
    private String newTechnician;

    @Schema(description = "Tipo de asignación (AUTO, MANUAL, REASSIGN, SELF)")
    private AssignmentType assignmentType;

    @Schema(description = "Motivo")
    private String reason;

    @Schema(description = "Asignado por")
    private String assignedBy;

    @Schema(description = "Fecha de asignación")
    private LocalDateTime createdAt;
}
