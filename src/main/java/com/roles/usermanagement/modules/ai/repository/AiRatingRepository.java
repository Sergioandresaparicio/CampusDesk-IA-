package com.roles.usermanagement.modules.ai.repository;

import com.roles.usermanagement.modules.ai.entity.AiRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiRatingRepository extends JpaRepository<AiRating, Long> {

    Optional<AiRating> findByResponseId(Long responseId);

    @Query("SELECT AVG(r.score) FROM AiRating r")
    Double findAverageRating();
}
