package com.roles.usermanagement.modules.lifecycle.controller;

import com.roles.usermanagement.core.common.ApiResponse;
import com.roles.usermanagement.modules.lifecycle.service.TicketLifecycleService;
import com.roles.usermanagement.modules.statushistory.entity.TicketStatusHistory;
import com.roles.usermanagement.modules.ticket.dto.TicketResponse;
import com.roles.usermanagement.modules.ticket.dto.TicketStatusTransitionRequest;
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
@Tag(name = "Ciclo de Vida", description = "Motor de transiciones de estados del ciclo de vida de incidencias")
public class TicketLifecycleController {

    private final TicketLifecycleService lifecycleService;

    public TicketLifecycleController(TicketLifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Transicionar estado del ticket", description = "Valida y aplica la máquina de estados (OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED)")
    public ResponseEntity<ApiResponse<TicketResponse>> transitionStatus(
            @PathVariable Long id,
            @Valid @RequestBody TicketStatusTransitionRequest request,
            Principal principal
    ) {
        TicketResponse response = lifecycleService.transitionStatus(id, request, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Estado de incidencia actualizado a " + response.getStatus(), response));
    }

    @GetMapping("/{id}/status-history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar trazabilidad e historial de cambios de estado")
    public ResponseEntity<ApiResponse<List<TicketStatusHistory>>> getStatusHistory(@PathVariable Long id) {
        List<TicketStatusHistory> history = lifecycleService.getStatusHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
