package com.roles.usermanagement.integrations.n8n.dto;

import com.roles.usermanagement.modules.knowledgebase.dto.KnowledgeArticleDto;
import com.roles.usermanagement.modules.ticket.dto.TicketResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class N8nDataResponse {

    @Schema(description = "Artículos activos y verificados listos para indexación en base vectorial Qdrant")
    private List<KnowledgeArticleDto> articlesForIndexing;

    @Schema(description = "Incidencias abiertas pendientes de clasificación o asistencia inteligente")
    private List<TicketResponse> pendingTickets;

    @Schema(description = "Total de artículos activos")
    private long totalArticles;

    @Schema(description = "Total de incidencias pendientes")
    private long totalPendingTickets;
}
