package com.roles.usermanagement.modules.knowledgebase.entity;

import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Artículos técnicos y soluciones documentadas de la Base de Conocimiento (RF02).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "knowledge_articles", indexes = {
        @Index(name = "idx_kb_category", columnList = "category"),
        @Index(name = "idx_kb_active_verified", columnList = "active, verified")
})
public class KnowledgeArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketCategory category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String problem;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String cause;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String solution;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = true;

    @Column(name = "embedding_id", length = 100)
    private String embeddingId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (active == null) active = true;
        if (verified == null) verified = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
