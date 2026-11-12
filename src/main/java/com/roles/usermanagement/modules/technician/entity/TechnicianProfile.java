package com.roles.usermanagement.modules.technician.entity;

import com.roles.usermanagement.modules.technician.enums.TechnicianAvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "technician_profiles")
public class TechnicianProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(length = 100)
    private String specialty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private TechnicianAvailabilityStatus availabilityStatus = TechnicianAvailabilityStatus.DISPONIBLE;

    @Column(name = "active_tickets_count", nullable = false)
    @Builder.Default
    private Integer activeTicketsCount = 0;

    @Column(name = "max_concurrent_tickets", nullable = false)
    @Builder.Default
    private Integer maxConcurrentTickets = 5;
}
