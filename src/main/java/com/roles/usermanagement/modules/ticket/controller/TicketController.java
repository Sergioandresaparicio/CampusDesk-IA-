package com.roles.usermanagement.modules.ticket.controller;

import com.roles.usermanagement.core.common.ApiResponse;
import com.roles.usermanagement.core.common.PageRequestDto;
import com.roles.usermanagement.core.common.PageResponse;
import com.roles.usermanagement.modules.ticket.dto.TicketCreateRequest;
import com.roles.usermanagement.modules.ticket.dto.TicketResponse;
import com.roles.usermanagement.modules.ticket.dto.TicketUpdateRequest;
import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import com.roles.usermanagement.modules.ticket.enums.TicketPriority;
import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
import com.roles.usermanagement.modules.ticket.service.TicketService;
import com.roles.usermanagement.web.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tickets")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@Tag(name = "Tickets", description = "Gestión integral de incidencias tecnológicas, filtros dinámicos y paginación")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Registrar nueva incidencia", description = "Crea un ticket en estado inicial OPEN asignado al usuario solicitante")
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(@Valid @RequestBody TicketCreateRequest request,
                                                                    Principal principal) {
        TicketResponse response = ticketService.createTicket(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Incidencia registrada con éxito", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar incidencia por ID")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(@PathVariable Long id) {
        TicketResponse response = ticketService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar incidencias paginadas con filtros dinámicos")
    public ResponseEntity<ApiResponse<PageResponse<TicketResponse>>> getTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) TicketCategory category,
            @RequestParam(required = false) String applicantUsername,
            @RequestParam(required = false) String technicianUsername,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Valid PageRequestDto pageRequest
    ) {
        PageResponse<TicketResponse> page = ticketService.getTickets(
                status, priority, category, applicantUsername, technicianUsername, search, startDate, endDate, pageRequest
        );
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Actualizar datos de una incidencia")
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateRequest request,
            Principal principal
    ) {
        TicketResponse response = ticketService.updateTicket(id, request, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Incidencia actualizada exitosamente", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar incidencia (Solo ADMIN)")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(ApiResponse.success("Incidencia eliminada con éxito", null));
    }
}
