package com.roles.usermanagement.modules.lifecycle.service;

import com.roles.usermanagement.core.exception.BusinessValidationException;
import com.roles.usermanagement.core.exception.ResourceNotFoundException;
import com.roles.usermanagement.modules.statushistory.entity.TicketStatusHistory;
import com.roles.usermanagement.modules.statushistory.repository.TicketStatusHistoryRepository;
import com.roles.usermanagement.modules.technician.entity.TechnicianProfile;
import com.roles.usermanagement.modules.technician.enums.TechnicianAvailabilityStatus;
import com.roles.usermanagement.modules.technician.repository.TechnicianProfileRepository;
import com.roles.usermanagement.modules.ticket.dto.TicketResponse;
import com.roles.usermanagement.modules.ticket.dto.TicketStatusTransitionRequest;
import com.roles.usermanagement.modules.ticket.entity.Ticket;
import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
import com.roles.usermanagement.modules.ticket.mapper.TicketMapper;
import com.roles.usermanagement.modules.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TicketLifecycleService {

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository statusHistoryRepository;
    private final TechnicianProfileRepository technicianRepository;
    private final TicketMapper ticketMapper;

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED_TRANSITIONS = Map.of(
            TicketStatus.OPEN, Set.of(TicketStatus.ASSIGNED, TicketStatus.CANCELLED),
            TicketStatus.ASSIGNED, Set.of(TicketStatus.IN_PROGRESS, TicketStatus.WAITING_CLIENT, TicketStatus.CANCELLED),
            TicketStatus.IN_PROGRESS, Set.of(TicketStatus.WAITING_CLIENT, TicketStatus.RESOLVED, TicketStatus.CANCELLED),
            TicketStatus.WAITING_CLIENT, Set.of(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED, TicketStatus.CANCELLED),
            TicketStatus.RESOLVED, Set.of(TicketStatus.CLOSED, TicketStatus.IN_PROGRESS),
            TicketStatus.CLOSED, Set.of(),
            TicketStatus.CANCELLED, Set.of()
    );

    public TicketLifecycleService(TicketRepository ticketRepository,
                                  TicketStatusHistoryRepository statusHistoryRepository,
                                  TechnicianProfileRepository technicianRepository,
                                  TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.technicianRepository = technicianRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional
    public TicketResponse transitionStatus(Long ticketId, TicketStatusTransitionRequest request, String changedBy) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada: " + ticketId));

        TicketStatus current = ticket.getStatus();
        TicketStatus target = request.getStatus();

        if (current == target) {
            return ticketMapper.toResponse(ticket);
        }

        Set<TicketStatus> validNextStatuses = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!validNextStatuses.contains(target)) {
            throw new BusinessValidationException(
                    "Transición no permitida: no se puede pasar del estado " + current + " a " + target
            );
        }

        if (target == TicketStatus.RESOLVED) {
            if (request.getResolutionNotes() == null || request.getResolutionNotes().isBlank()) {
                throw new BusinessValidationException("Para resolver un ticket, las notas de resolución son obligatorias");
            }
            ticket.setResolutionNotes(request.getResolutionNotes().trim());
            ticket.setResolvedAt(LocalDateTime.now());
            releaseTechnicianWorkload(ticket.getTechnicianUsername());
        }

        if (target == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
            releaseTechnicianWorkload(ticket.getTechnicianUsername());
        }

        if (target == TicketStatus.CANCELLED) {
            releaseTechnicianWorkload(ticket.getTechnicianUsername());
        }

        ticket.setStatus(target);
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updated = ticketRepository.save(ticket);

        TicketStatusHistory history = TicketStatusHistory.builder()
                .ticketId(ticketId)
                .previousStatus(current)
                .newStatus(target)
                .changedBy(changedBy)
                .reason(request.getReason() != null ? request.getReason() : "Transición de estado")
                .createdAt(LocalDateTime.now())
                .build();
        statusHistoryRepository.save(history);

        return ticketMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<TicketStatusHistory> getStatusHistory(Long ticketId) {
        return statusHistoryRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }

    private void releaseTechnicianWorkload(String username) {
        if (username != null && !username.isBlank()) {
            technicianRepository.findByUsername(username).ifPresent(profile -> {
                profile.setActiveTicketsCount(Math.max(0, profile.getActiveTicketsCount() - 1));
                if (profile.getAvailabilityStatus() == TechnicianAvailabilityStatus.OCUPADO
                        && profile.getActiveTicketsCount() < profile.getMaxConcurrentTickets()) {
                    profile.setAvailabilityStatus(TechnicianAvailabilityStatus.DISPONIBLE);
                }
                technicianRepository.save(profile);
            });
        }
    }
}
