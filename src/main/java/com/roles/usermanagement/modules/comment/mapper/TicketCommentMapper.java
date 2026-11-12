package com.roles.usermanagement.modules.comment.mapper;

import com.roles.usermanagement.modules.comment.dto.TicketCommentRequest;
import com.roles.usermanagement.modules.comment.dto.TicketCommentResponse;
import com.roles.usermanagement.modules.comment.entity.TicketComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketCommentMapper {

    TicketCommentResponse toResponse(TicketComment comment);

    List<TicketCommentResponse> toResponses(List<TicketComment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ticketId", ignore = true)
    @Mapping(target = "authorUsername", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TicketComment toEntity(TicketCommentRequest request);
}
