package com.roles.usermanagement.modules.knowledgebase.repository;

import com.roles.usermanagement.modules.knowledgebase.entity.KnowledgeArticle;
import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long> {

    List<KnowledgeArticle> findByActiveTrueAndVerifiedTrue();

    Page<KnowledgeArticle> findByActiveTrueAndVerifiedTrue(Pageable pageable);

    List<KnowledgeArticle> findByCategoryAndActiveTrueAndVerifiedTrue(TicketCategory category);

    @Query("SELECT k FROM KnowledgeArticle k WHERE k.active = true AND k.verified = true AND (" +
            "LOWER(k.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(k.problem) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(k.symptoms) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(k.solution) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<KnowledgeArticle> searchByKeyword(@Param("query") String query);
}
