package com.roles.usermanagement.modules.comment.repository;

import com.roles.usermanagement.modules.comment.entity.TicketComment;
import com.roles.usermanagement.modules.comment.enums.CommentVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    List<TicketComment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);

    List<TicketComment> findByTicketIdAndVisibilityOrderByCreatedAtAsc(Long ticketId, CommentVisibility visibility);
}
