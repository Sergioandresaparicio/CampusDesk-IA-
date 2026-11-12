package com.roles.usermanagement.modules.technician.controller;

import com.roles.usermanagement.core.common.ApiResponse;
import com.roles.usermanagement.modules.technician.dto.TechnicianProfileDto;
import com.roles.usermanagement.modules.technician.dto.TechnicianWorkloadResponse;
import com.roles.usermanagement.modules.technician.enums.TechnicianAvailabilityStatus;
import com.roles.usermanagement.modules.technician.service.TechnicianService;
import com.roles.usermanagement.web.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@Tag(name = "Técnicos", description = "Directorio de técnicos de soporte, carga de trabajo y disponibilidad")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar directorio de técnicos y balance de carga de trabajo")
    public ResponseEntity<ApiResponse<List<TechnicianWorkloadResponse>>> getTechnicians() {
        List<TechnicianWorkloadResponse> list = technicianService.getTechniciansWorkload();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PutMapping("/{username}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Actualizar estado de disponibilidad del técnico (DISPONIBLE, OCUPADO, AUSENTE)")
    public ResponseEntity<ApiResponse<TechnicianProfileDto>> updateStatus(
            @PathVariable String username,
            @RequestParam TechnicianAvailabilityStatus status
    ) {
        TechnicianProfileDto updated = technicianService.updateStatus(username, status);
        return ResponseEntity.ok(ApiResponse.success("Disponibilidad actualizada", updated));
    }
}
