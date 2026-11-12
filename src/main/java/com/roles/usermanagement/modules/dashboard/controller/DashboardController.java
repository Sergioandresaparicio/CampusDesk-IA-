package com.roles.usermanagement.modules.dashboard.controller;

import com.roles.usermanagement.core.common.ApiResponse;
import com.roles.usermanagement.modules.dashboard.dto.DashboardMetricsResponse;
import com.roles.usermanagement.modules.dashboard.service.DashboardService;
import com.roles.usermanagement.web.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@Tag(name = "Dashboard Analítico", description = "Indicadores cuantitativos, métricas históricas reales y KPI (RF06, RF07)")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener estadísticas analíticas globales del sistema")
    public ResponseEntity<ApiResponse<DashboardMetricsResponse>> getStats() {
        DashboardMetricsResponse metrics = dashboardService.getMetrics();
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }
}
