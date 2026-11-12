package com.roles.usermanagement.modules.assignment.service;

import com.roles.usermanagement.core.exception.BusinessValidationException;
import com.roles.usermanagement.core.exception.ResourceNotFoundException;
import com.roles.usermanagement.modules.assignment.dto.TicketAssignRequest;
import com.roles.usermanagement.modules.assignment.dto.TicketAssignmentResponse;
import com.roles.usermanagement.modules.assignment.dto.TicketReassignRequest;
import com.roles.usermanagement.modules.assignment.entity.TicketAssignmentLog;
import com.roles.usermanagement.modules.assignment.enums.AssignmentType;
import com.roles.usermanagement.modules.assignment.mapper.TicketAssignmentMapper;
import com.roles.usermanagement.modules.assignment.repository.TicketAssignmentLogRepository;
import com.roles.usermanagement.modules.statushistory.entity.TicketStatusHistory;
import com.roles.usermanagement.modules.statushistory.repository.TicketStatusHistoryRepository;
import com.roles.usermanagement.modules.technician.entity.TechnicianProfile;
import com.roles.usermanagement.modules.technician.enums.TechnicianAvailabilityStatus;
import com.roles.usermanagement.modules.technician.repository.TechnicianProfileRepository;
import com.roles.usermanagement.modules.ticket.entity.Ticket;
import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
import com.roles.usermanagement.modules.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketAssignmentService {

    private final TicketRepository ticketRepository;
    private final TicketAssignmentLogRepository logRepository;
    private final TechnicianProfileRepository technicianRepository;
    private final TicketStatusHistoryRepository statusHistoryRepository;
    private final TicketAssignmentMapper mapper;

    public TicketAssignmentService(TicketRepository ticketRepository,
                                   TicketAssignmentLogRepository logRepository,
                                   TechnicianProfileRepository technicianRepository,
                                   TicketStatusHistoryRepository statusHistoryRepository,
                                   TicketAssignmentMapper mapper) {
        this.ticketRepository = ticketRepository;
        this.logRepository = logRepository;
        this.technicianRepository = technicianRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.mapper = mapper;
    }

    @Transactional
    public TicketAssignmentResponse assignTicket(Long ticketId, TicketAssignRequest request, String assignedBy) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada: " + ticketId));

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new BusinessValidationException("Solo se pueden asignar incidencias en estado OPEN");
        }

        String targetTechnician = request.getTechnicianUsername().trim();
        validateTechnicianAvailability(targetTechnician);

        String previousTechnician = ticket.getTechnicianUsername();
        ticket.setTechnicianUsername(targetTechnician);
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticket.setAssignedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        // Update workload
        incrementTechnicianWorkload(targetTechnician);

        // Log assignment
        TicketAssignmentLog log = TicketAssignmentLog.builder()
                .ticketId(ticketId)
                .previousTechnician(previousTechnician)
                .newTechnician(targetTechnician)
                .assignmentType(AssignmentType.MANUAL)
                .reason(request.getReason() != null ? request.getReason() : "Asignación manual de técnico")
                .assignedBy(assignedBy)
                .createdAt(LocalDateTime.now())
                .build();
        TicketAssignmentLog savedLog = logRepository.save(log);

        // Log status history
        statusHistoryRepository.save(TicketStatusHistory.builder()
                .ticketId(ticketId)
                .previousStatus(TicketStatus.OPEN)
                .newStatus(TicketStatus.ASSIGNED)
                .changedBy(assignedBy)
                .reason("Ticket asignado a " + targetTechnician)
                .createdAt(LocalDateTime.now())
                .build());

        return mapper.toResponse(savedLog);
    }

    @Transactional
    public TicketAssignmentResponse reassignTicket(Long ticketId, TicketReassignRequest request, String assignedBy) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada: " + ticketId));

        if (ticket.getStatus() == TicketStatus.CLOSED || ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new BusinessValidationException("No se puede reasignar un ticket cerrado o cancelado");
        }

        String targetTechnician = request.getNewTechnicianUsername().trim();
        String previousTechnician = ticket.getTechnicianUsername();

        if (targetTechnician.equals(previousTechnician)) {
            throw new BusinessValidationException("El nuevo técnico debe ser diferente al técnico actual");
        }

        validateTechnicianAvailability(targetTechnician);

        // Decrement previous technician workload
        if (previousTechnician != null) {
            decrementTechnicianWorkload(previousTechnician);
        }
        incrementTechnicianWorkload(targetTechnician);

        ticket.setTechnicianUsername(targetTechnician);
        ticket.setAssignedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        TicketAssignmentLog log = TicketAssignmentLog.builder()
                .ticketId(ticketId)
                .previousTechnician(previousTechnician)
                .newTechnician(targetTechnician)
                .assignmentType(AssignmentType.REASSIGN)
                .reason(request.getReason())
                .assignedBy(assignedBy)
                .createdAt(LocalDateTime.now())
                .build();
        TicketAssignmentLog savedLog = logRepository.save(log);

        return mapper.toResponse(savedLog);
    }

    @Transactional
    public TicketAssignmentResponse selfAssign(Long ticketId, String technicianUsername) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada: " + ticketId));

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new BusinessValidationException("Solo puedes auto-asignarte incidencias en estado OPEN");
        }

        validateTechnicianAvailability(technicianUsername);

        ticket.setTechnicianUsername(technicianUsername);
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticket.setAssignedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        incrementTechnicianWorkload(technicianUsername);

        TicketAssignmentLog log = TicketAssignmentLog.builder()
                .ticketId(ticketId)
                .previousTechnician(null)
                .newTechnician(technicianUsername)
                .assignmentType(AssignmentType.SELF)
                .reason("Auto-asignación desde la cola de tickets pendientes")
                .assignedBy(technicianUsername)
                .createdAt(LocalDateTime.now())
                .build();

        statusHistoryRepository.save(TicketStatusHistory.builder()
                .ticketId(ticketId)
                .previousStatus(TicketStatus.OPEN)
                .newStatus(TicketStatus.ASSIGNED)
                .changedBy(technicianUsername)
                .reason("Auto-asignado por el técnico")
                .createdAt(LocalDateTime.now())
                .build());

        return mapper.toResponse(logRepository.save(log));
    }

    @Transactional
    public TicketAssignmentResponse autoAssign(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada: " + ticketId));

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new BusinessValidationException("Solo se pueden auto-asignar incidencias en estado OPEN");
        }

        List<TechnicianProfile> availableTechnicians = technicianRepository.findAvailableTechniciansOrderedByWorkload();
        if (availableTechnicians.isEmpty()) {
            throw new BusinessValidationException("No hay técnicos disponibles en este momento con capacidad suficiente");
        }

        TechnicianProfile bestTech = availableTechnicians.get(0);
        String targetTechnician = bestTech.getUsername();

        ticket.setTechnicianUsername(targetTechnician);
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticket.setAssignedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        incrementTechnicianWorkload(targetTechnician);

        TicketAssignmentLog log = TicketAssignmentLog.builder()
                .ticketId(ticketId)
                .previousTechnician(null)
                .newTechnician(targetTechnician)
                .assignmentType(AssignmentType.AUTO)
                .reason("Asignación inteligente automática por balanceo de carga")
                .assignedBy("system-scheduler")
                .createdAt(LocalDateTime.now())
                .build();

        statusHistoryRepository.save(TicketStatusHistory.builder()
                .ticketId(ticketId)
                .previousStatus(TicketStatus.OPEN)
                .newStatus(TicketStatus.ASSIGNED)
                .changedBy("system-scheduler")
                .reason("Auto-balanceo de carga hacia " + targetTechnician)
                .createdAt(LocalDateTime.now())
                .build());

        return mapper.toResponse(logRepository.save(log));
    }

    @Transactional(readOnly = true)
    public List<TicketAssignmentResponse> getAssignmentHistory(Long ticketId) {
        return mapper.toResponses(logRepository.findByTicketIdOrderByCreatedAtDesc(ticketId));
    }

    private void validateTechnicianAvailability(String username) {
        technicianRepository.findByUsername(username).ifPresent(profile -> {
            if (profile.getAvailabilityStatus() != TechnicianAvailabilityStatus.DISPONIBLE) {
                throw new BusinessValidationException("El técnico " + username + " se encuentra " + profile.getAvailabilityStatus());
            }
            if (profile.getActiveTicketsCount() >= profile.getMaxConcurrentTickets()) {
                throw new BusinessValidationException("El técnico " + username + " alcanzó su capacidad máxima simultánea");
            }
        });
    }

    private void incrementTechnicianWorkload(String username) {
        technicianRepository.findByUsername(username).ifPresent(profile -> {
            profile.setActiveTicketsCount(profile.getActiveTicketsCount() + 1);
            if (profile.getActiveTicketsCount() >= profile.getMaxConcurrentTickets()) {
                profile.setAvailabilityStatus(TechnicianAvailabilityStatus.OCUPADO);
            }
            technicianRepository.save(profile);
        });
    }

    private void decrementTechnicianWorkload(String username) {
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
