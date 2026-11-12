package com.roles.usermanagement.modules.comment.service;

import com.roles.usermanagement.core.exception.BusinessValidationException;
import com.roles.usermanagement.core.exception.ResourceNotFoundException;
import com.roles.usermanagement.modules.comment.dto.TicketCommentRequest;
import com.roles.usermanagement.modules.comment.dto.TicketCommentResponse;
import com.roles.usermanagement.modules.comment.entity.TicketComment;
import com.roles.usermanagement.modules.comment.enums.CommentVisibility;
import com.roles.usermanagement.modules.comment.mapper.TicketCommentMapper;
import com.roles.usermanagement.modules.comment.repository.TicketCommentRepository;
import com.roles.usermanagement.modules.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final TicketCommentMapper mapper;

    public TicketCommentService(TicketCommentRepository commentRepository,
                                TicketRepository ticketRepository,
                                TicketCommentMapper mapper) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.mapper = mapper;
    }

    @Transactional
    public TicketCommentResponse addComment(Long ticketId, TicketCommentRequest request, String authorUsername, boolean isStaff) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Incidencia no encontrada: " + ticketId);
        }

        CommentVisibility visibility = request.getVisibility() != null
                ? request.getVisibility()
                : CommentVisibility.PUBLIC;

        if (visibility == CommentVisibility.INTERNAL && !isStaff) {
            throw new BusinessValidationException("Solo el personal técnico o administradores pueden publicar notas internas");
        }

        TicketComment comment = TicketComment.builder()
                .ticketId(ticketId)
                .authorUsername(authorUsername)
                .content(request.getContent().trim())
                .visibility(visibility)
                .createdAt(LocalDateTime.now())
                .build();

        return mapper.toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<TicketCommentResponse> getComments(Long ticketId, boolean isStaff) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Incidencia no encontrada: " + ticketId);
        }

        List<TicketComment> comments = isStaff
                ? commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                : commentRepository.findByTicketIdAndVisibilityOrderByCreatedAtAsc(ticketId, CommentVisibility.PUBLIC);

        return mapper.toResponses(comments);
    }
}
