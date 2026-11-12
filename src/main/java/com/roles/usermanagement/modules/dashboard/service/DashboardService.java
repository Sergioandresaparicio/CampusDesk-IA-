package com.roles.usermanagement.modules.dashboard.service;

import com.roles.usermanagement.modules.ai.repository.AiRatingRepository;
import com.roles.usermanagement.modules.dashboard.dto.DashboardMetricsResponse;
import com.roles.usermanagement.modules.ticket.entity.Ticket;
import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import com.roles.usermanagement.modules.ticket.enums.TicketPriority;
import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
import com.roles.usermanagement.modules.ticket.mapper.TicketMapper;
import com.roles.usermanagement.modules.ticket.repository.TicketRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final AiRatingRepository aiRatingRepository;
    private final TicketMapper ticketMapper;

    public DashboardService(TicketRepository ticketRepository,
                            AiRatingRepository aiRatingRepository,
                            TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.aiRatingRepository = aiRatingRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional(readOnly = true)
    public DashboardMetricsResponse getMetrics() {
        long total = ticketRepository.count();
        long open = ticketRepository.countByStatus(TicketStatus.OPEN);
        long inProgress = ticketRepository.countByStatus(TicketStatus.IN_PROGRESS)
                + ticketRepository.countByStatus(TicketStatus.ASSIGNED)
                + ticketRepository.countByStatus(TicketStatus.WAITING_CLIENT);
        long resolved = ticketRepository.countByStatus(TicketStatus.RESOLVED);
        long closed = ticketRepository.countByStatus(TicketStatus.CLOSED);

        double resolutionRate = total > 0 ? ((double) (resolved + closed) / total) * 100.0 : 0.0;

        // Calculate MTTR in minutes for resolved/closed tickets
        List<Ticket> allTickets = ticketRepository.findAll();
        double avgMinutes = allTickets.stream()
                .filter(t -> t.getResolvedAt() != null && t.getCreatedAt() != null)
                .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getResolvedAt()).toMinutes())
                .average()
                .orElse(0.0);

        // Group by category
        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (TicketCategory cat : TicketCategory.values()) {
            byCategory.put(cat.name(), ticketRepository.countByCategory(cat));
        }

        // Group by priority
        Map<String, Long> byPriority = new LinkedHashMap<>();
        for (TicketPriority pri : TicketPriority.values()) {
            byPriority.put(pri.name(), ticketRepository.countByPriority(pri));
        }

        // Recent tickets
        List<Ticket> recent = ticketRepository.findAll(
                PageRequest.of(0, 5, Sort.by("createdAt").descending())
        ).getContent();

        // AI rating
        Double avgRating = aiRatingRepository.findAverageRating();

        return DashboardMetricsResponse.builder()
                .totalTickets(total)
                .openTickets(open)
                .inProgressTickets(inProgress)
                .resolvedTickets(resolved)
                .closedTickets(closed)
                .resolutionRate(Math.round(resolutionRate * 10.0) / 10.0)
                .averageResolutionMinutes(Math.round(avgMinutes * 10.0) / 10.0)
                .ticketsByCategory(byCategory)
                .ticketsByPriority(byPriority)
                .averageAiRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 5.0)
                .recentTickets(ticketMapper.toResponses(recent))
                .build();
    }
}
