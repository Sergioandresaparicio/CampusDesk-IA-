package com.roles.usermanagement.modules.ticket.repository;

import com.roles.usermanagement.modules.ticket.entity.Ticket;
import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import com.roles.usermanagement.modules.ticket.enums.TicketPriority;
import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    Page<Ticket> findByApplicantUsername(String applicantUsername, Pageable pageable);

    Page<Ticket> findByTechnicianUsername(String technicianUsername, Pageable pageable);

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    long countByStatus(TicketStatus status);

    long countByCategory(TicketCategory category);

    long countByPriority(TicketPriority priority);

    long countByTechnicianUsernameAndStatusIn(String technicianUsername, List<TicketStatus> statuses);

    @Query("SELECT t.category as category, COUNT(t) as count FROM Ticket t GROUP BY t.category")
    List<Map<String, Object>> countGroupedByCategory();

    @Query("SELECT t.status as status, COUNT(t) as count FROM Ticket t GROUP BY t.status")
    List<Map<String, Object>> countGroupedByStatus();

    @Query("SELECT t.priority as priority, COUNT(t) as count FROM Ticket t GROUP BY t.priority")
    List<Map<String, Object>> countGroupedByPriority();

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate")
    long countBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = 'RESOLVED' OR t.status = 'CLOSED'")
    long countResolvedOrClosed();
}
