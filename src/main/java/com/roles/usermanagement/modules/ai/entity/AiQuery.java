package com.roles.usermanagement.modules.ai.entity;

import com.roles.usermanagement.modules.ai.enums.AiQueryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Consulta de asistencia inteligente RAG disparada para un incidente (RF05, RF08).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ai_queries", indexes = {
        @Index(name = "idx_ai_query_ticket", columnList = "ticket_id"),
        @Index(name = "idx_ai_query_status", columnList = "status")
})
public class AiQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AiQueryStatus status;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "query", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AiResponse response;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = AiQueryStatus.PENDIENTE;
        }
    }
}
