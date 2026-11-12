package com.roles.usermanagement.modules.dashboard.dto;

import com.roles.usermanagement.modules.ticket.dto.TicketResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsResponse {

    @Schema(description = "Total histórico de incidencias registradas", example = "150")
    private long totalTickets;

    @Schema(description = "Incidencias abiertas sin asignar", example = "25")
    private long openTickets;

    @Schema(description = "Incidencias en progreso o asignadas", example = "40")
    private long inProgressTickets;

    @Schema(description = "Incidencias resueltas exitosamente", example = "65")
    private long resolvedTickets;

    @Schema(description = "Incidencias cerradas definitivamente", example = "20")
    private long closedTickets;

    @Schema(description = "Tasa de resolución porcentual (0.0% a 100.0%)", example = "85.5")
    private double resolutionRate;

    @Schema(description = "Tiempo medio de resolución en minutos (MTTR)", example = "142.3")
    private double averageResolutionMinutes;

    @Schema(description = "Distribución de incidencias por categoría tecnológica")
    private Map<String, Long> ticketsByCategory;

    @Schema(description = "Distribución de incidencias por nivel de prioridad")
    private Map<String, Long> ticketsByPriority;

    @Schema(description = "Calificación promedio de utilidad del Asistente RAG (1.0 a 5.0)", example = "4.8")
    private double averageAiRating;

    @Schema(description = "Incidencias recientes registradas")
    private List<TicketResponse> recentTickets;
}
