package com.roles.usermanagement.modules.ticket.service;

import com.roles.usermanagement.core.common.PageRequestDto;
import com.roles.usermanagement.core.common.PageResponse;
import com.roles.usermanagement.core.exception.BusinessValidationException;
import com.roles.usermanagement.core.exception.ResourceNotFoundException;
import com.roles.usermanagement.modules.statushistory.entity.TicketStatusHistory;
import com.roles.usermanagement.modules.statushistory.repository.TicketStatusHistoryRepository;
import com.roles.usermanagement.modules.ticket.dto.TicketCreateRequest;
import com.roles.usermanagement.modules.ticket.dto.TicketResponse;
import com.roles.usermanagement.modules.ticket.dto.TicketUpdateRequest;
import com.roles.usermanagement.modules.ticket.entity.Ticket;
import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import com.roles.usermanagement.modules.ticket.enums.TicketPriority;
import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
import com.roles.usermanagement.modules.ticket.mapper.TicketMapper;
import com.roles.usermanagement.modules.ticket.repository.TicketRepository;
import com.roles.usermanagement.modules.ticket.repository.TicketSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository historyRepository;
    private final TicketMapper ticketMapper;

    public TicketService(TicketRepository ticketRepository,
                         TicketStatusHistoryRepository historyRepository,
                         TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional
    public TicketResponse createTicket(TicketCreateRequest request, String applicantUsername) {
        if (applicantUsername == null || applicantUsername.isBlank()) {
            throw new BusinessValidationException("El usuario solicitante es obligatorio");
        }

        Ticket ticket = ticketMapper.toEntity(request);
        ticket.setApplicantUsername(applicantUsername.trim());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());

        Ticket saved = ticketRepository.save(ticket);

        // Record initial status history
        TicketStatusHistory history = TicketStatusHistory.builder()
                .ticketId(saved.getId())
                .previousStatus(null)
                .newStatus(TicketStatus.OPEN)
                .changedBy(applicantUsername.trim())
                .reason("Creación inicial de la incidencia tecnológica")
                .createdAt(LocalDateTime.now())
                .build();
        historyRepository.save(history);

        return ticketMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con ID: " + id));
        return ticketMapper.toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public PageResponse<TicketResponse> getTickets(
            TicketStatus status,
            TicketPriority priority,
            TicketCategory category,
            String applicantUsername,
            String technicianUsername,
            String searchTerm,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PageRequestDto pageRequest
    ) {
        Pageable pageable = pageRequest.toPageable();
        Specification<Ticket> spec = TicketSpecifications.withFilters(
                status, priority, category, applicantUsername, technicianUsername, searchTerm, startDate, endDate
        );

        Page<Ticket> page = ticketRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(ticketMapper::toResponse));
    }

    @Transactional
    public TicketResponse updateTicket(Long id, TicketUpdateRequest request, String modifiedBy) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con ID: " + id));

        if (ticket.getStatus() == TicketStatus.CLOSED || ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new BusinessValidationException("No se puede modificar una incidencia en estado " + ticket.getStatus());
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            ticket.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            ticket.setDescription(request.getDescription().trim());
        }
        if (request.getCategory() != null) {
            ticket.setCategory(request.getCategory());
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }
        if (request.getDevice() != null) {
            ticket.setDevice(request.getDevice());
        }
        if (request.getRoom() != null) {
            ticket.setRoom(request.getRoom());
        }
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Incidencia no encontrada con ID: " + id);
        }
        ticketRepository.deleteById(id);
    }
}
