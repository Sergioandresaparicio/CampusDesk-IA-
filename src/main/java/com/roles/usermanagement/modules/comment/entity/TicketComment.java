package com.roles.usermanagement.modules.comment.entity;

import com.roles.usermanagement.modules.comment.enums.CommentVisibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ticket_comments", indexes = {
        @Index(name = "idx_comment_ticket", columnList = "ticket_id"),
        @Index(name = "idx_comment_author", columnList = "author_username")
})
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "author_username", nullable = false, length = 50)
    private String authorUsername;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private CommentVisibility visibility = CommentVisibility.PUBLIC;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (visibility == null) {
            visibility = CommentVisibility.PUBLIC;
        }
    }
}
