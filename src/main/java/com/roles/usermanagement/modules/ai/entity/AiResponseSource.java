package com.roles.usermanagement.modules.ai.entity;

import com.roles.usermanagement.modules.knowledgebase.entity.KnowledgeArticle;
import jakarta.persistence.*;
import lombok.*;

/**
 * Fuente documental recuperada de la base de conocimiento para la respuesta RAG (RF05).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ai_response_sources")
public class AiResponseSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private AiResponse response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private KnowledgeArticle article;

    @Column(name = "similarity_score")
    private Double similarityScore;
}
