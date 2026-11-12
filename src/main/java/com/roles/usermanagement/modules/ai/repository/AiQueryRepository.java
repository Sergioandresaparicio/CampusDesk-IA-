package com.roles.usermanagement.modules.ai.repository;

import com.roles.usermanagement.modules.ai.entity.AiQuery;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiQueryRepository extends JpaRepository<AiQuery, Long> {

    @EntityGraph(attributePaths = {"response", "response.sources", "response.sources.article", "response.rating"})
    List<AiQuery> findByTicketIdOrderByCreatedAtDesc(Long ticketId);

    @EntityGraph(attributePaths = {"response", "response.sources", "response.sources.article", "response.rating"})
    Optional<AiQuery> findById(Long id);
}
