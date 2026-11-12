package com.roles.usermanagement.modules.assignment.mapper;

import com.roles.usermanagement.modules.assignment.dto.TicketAssignmentResponse;
import com.roles.usermanagement.modules.assignment.entity.TicketAssignmentLog;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketAssignmentMapper {

    TicketAssignmentResponse toResponse(TicketAssignmentLog log);

    List<TicketAssignmentResponse> toResponses(List<TicketAssignmentLog> logs);
}
