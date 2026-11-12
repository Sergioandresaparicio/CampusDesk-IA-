package com.roles.usermanagement.modules.assignment.controller;

import com.roles.usermanagement.core.common.ApiResponse;
import com.roles.usermanagement.modules.assignment.dto.TicketAssignRequest;
import com.roles.usermanagement.modules.assignment.dto.TicketAssignmentResponse;
import com.roles.usermanagement.modules.assignment.dto.TicketReassignRequest;
import com.roles.usermanagement.modules.assignment.service.TicketAssignmentService;
import com.roles.usermanagement.web.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@Tag(name = "Asignaciones", description = "Operaciones de asignación, reasignación y auto-balanceo de incidencias")
public class TicketAssignmentController {

    private final TicketAssignmentService assignmentService;

    public TicketAssignmentController(TicketAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('TICKET_ASSIGN')")
    @Operation(summary = "Asignación manual de técnico a ticket OPEN")
    public ResponseEntity<ApiResponse<TicketAssignmentResponse>> assignTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketAssignRequest request,
            Principal principal
    ) {
        TicketAssignmentResponse response = assignmentService.assignTicket(id, request, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Ticket asignado exitosamente", response));
    }

    @PutMapping("/{id}/reassign")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('TICKET_ASSIGN')")
    @Operation(summary = "Reasignación de ticket a otro técnico con justificación")
    public ResponseEntity<ApiResponse<TicketAssignmentResponse>> reassignTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketReassignRequest request,
            Principal principal
    ) {
        TicketAssignmentResponse response = assignmentService.reassignTicket(id, request, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Ticket reasignado exitosamente", response));
    }

    @PostMapping("/{id}/self-assign")
    @PreAuthorize("hasRole('TECHNICIAN') or hasRole('ADMIN')")
    @Operation(summary = "Auto-asignación de ticket por un técnico disponible")
    public ResponseEntity<ApiResponse<TicketAssignmentResponse>> selfAssign(
            @PathVariable Long id,
            Principal principal
    ) {
        TicketAssignmentResponse response = assignmentService.selfAssign(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Ticket auto-asignado exitosamente", response));
    }

    @PostMapping("/{id}/auto-assign")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('TICKET_ASSIGN')")
    @Operation(summary = "Auto-balanceo inteligente de asignación según menor carga activa")
    public ResponseEntity<ApiResponse<TicketAssignmentResponse>> autoAssign(@PathVariable Long id) {
        TicketAssignmentResponse response = assignmentService.autoAssign(id);
        return ResponseEntity.ok(ApiResponse.success("Ticket auto-asignado por balanceo de carga", response));
    }

    @GetMapping("/{id}/assignments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar historial de asignaciones de un ticket")
    public ResponseEntity<ApiResponse<List<TicketAssignmentResponse>>> getAssignmentHistory(@PathVariable Long id) {
        List<TicketAssignmentResponse> history = assignmentService.getAssignmentHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
