package com.roles.usermanagement.modules.assignment.repository;

import com.roles.usermanagement.modules.assignment.entity.TicketAssignmentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketAssignmentLogRepository extends JpaRepository<TicketAssignmentLog, Long> {

    List<TicketAssignmentLog> findByTicketIdOrderByCreatedAtDesc(Long ticketId);

    List<TicketAssignmentLog> findByNewTechnician(String technicianUsername);
}
