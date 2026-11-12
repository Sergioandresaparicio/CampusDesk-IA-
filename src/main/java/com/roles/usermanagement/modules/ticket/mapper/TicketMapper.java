package com.roles.usermanagement.modules.ticket.mapper;

import com.roles.usermanagement.modules.ticket.dto.TicketCreateRequest;
import com.roles.usermanagement.modules.ticket.dto.TicketResponse;
import com.roles.usermanagement.modules.ticket.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    TicketResponse toResponse(Ticket ticket);

    List<TicketResponse> toResponses(List<Ticket> tickets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "applicantUsername", ignore = true)
    @Mapping(target = "technicianUsername", ignore = true)
    @Mapping(target = "resolutionNotes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "assignedAt", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    Ticket toEntity(TicketCreateRequest request);
}
