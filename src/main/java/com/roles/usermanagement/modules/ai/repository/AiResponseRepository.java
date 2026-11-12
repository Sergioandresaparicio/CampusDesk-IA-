package com.roles.usermanagement.modules.ai.repository;

import com.roles.usermanagement.modules.ai.entity.AiResponse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiResponseRepository extends JpaRepository<AiResponse, Long> {

    @EntityGraph(attributePaths = {"sources", "sources.article", "rating"})
    Optional<AiResponse> findByQueryId(Long queryId);
}
