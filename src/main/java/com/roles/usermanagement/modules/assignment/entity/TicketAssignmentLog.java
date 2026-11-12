package com.roles.usermanagement.modules.assignment.entity;

import com.roles.usermanagement.modules.assignment.enums.AssignmentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ticket_assignment_logs", indexes = {
        @Index(name = "idx_assignment_ticket", columnList = "ticket_id"),
        @Index(name = "idx_assignment_technician", columnList = "new_technician")
})
public class TicketAssignmentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "previous_technician", length = 50)
    private String previousTechnician;

    @Column(name = "new_technician", nullable = false, length = 50)
    private String newTechnician;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_type", nullable = false, length = 30)
    private AssignmentType assignmentType;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "assigned_by", nullable = false, length = 50)
    private String assignedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
